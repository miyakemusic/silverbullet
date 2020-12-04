package jp.silverbullet.dev;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.silverbullet.core.Zip;
import jp.silverbullet.core.dependency2.RequestRejectedException;
import jp.silverbullet.core.sequncer.Sequencer;


class UserModel {

	private Map<String, BuilderModelImpl> builderModels = new HashMap<>();
	private Map<String, BuilderModelImpl> runtimeModels = new HashMap<>();
	private Automator automator = null;
	
	private AutomatorInterface automaterInterface = new AutomatorInterface() {

		@Override
		public void write(String device, String id, String value) {
			Sequencer sequencer = runtimeModels.get(device).getSequencer();
			try {
				sequencer.requestChange(id, value);
			} catch (RequestRejectedException e) {
				e.printStackTrace();
			}
		}

		@Override
		public String read(String addr, String query) {
			//return runtimeModels
			return null;
		}				
	};
	
	public UserModel() {
		automator = new Automator(automaterInterface);
	}
	
	public BuilderModelImpl getBuilderModel(String app) {
		return builderModels.get(app);
	}

	public Map<String, BuilderModelImpl> getBuilderModels() {
		return builderModels;
	}

	public Map<String, BuilderModelImpl> getRuntimeModels() {
		return runtimeModels;
	}

	public void reloadRuntime(String folder) {
		for (BuilderModelImpl model : runtimeModels.values()) {
			model.load(folder);
		}
	}

	public BuilderModelImpl addRuntimeModel(String app, String device, String userid) {
		try {
			BuilderModelImpl runtimeModel = SbFiles.loadAfile(userid, SbFiles.PERSISTENT_FOLDER + "/" + userid + "/" + app + ".zip", device);
			runtimeModel.setApplicationName(app);
			runtimeModels.put(device, runtimeModel);
			
			runtimeModel.getSequencer().addSequencerListener(automator.createListener(device));
			System.out.println("Runtime Model was generated. :" + 
					device + " @" + Thread.currentThread().getName() + ":" + runtimeModels.hashCode());
			return runtimeModel;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void addDevModel(BuilderModelImpl model, String app) {
		this.builderModels.put(app, model);
	}	
	

	public Automator getAutomator() {
		return this.automator;
	}

	public void removeDevice(String device) {
		runtimeModels.remove(device);
		this.automator.removeDevice(device);
	}

	public BuilderModelImpl getModel(String app, String device) {
		if (!runtimeModels.containsKey(device)) {
			return this.getBuilderModel(app);
		}
		else {
			BuilderModelImpl model = runtimeModels.get(device);
			return model;
		}
	}
}

public abstract class BuilderModelHolder {

	public static final String NO_DEVICE = "NO_DEVICE";
	public static final String DEFAULT_USER_SERIAL = "Default00";
	public static final String DEFAULT_USER_FILE = "silverbullet.zip";
	public static final String DEFAULT_USER_NAME = "silverbullet";
	
	private Map<String, UserModel> allUsers = new HashMap<>();
	
	
	public BuilderModelHolder() {

	}

	public Automator getAutomator(String userid) {
		return this.allUsers.get(userid).getAutomator();
	}	

	public void loadAll() {
		SbFiles.createTmpFolderIfNotExists();
		
		new SbFiles.WalkThrough() {
			@Override
			protected void handleUserId(String userid, List<String> list) {
				UserModel userModel = new UserModel();
				allUsers.put(userid, userModel);
				
				for (String path : list) {
					BuilderModelImpl model;
					try {
						model = SbFiles.loadAfile(userid, path, NO_DEVICE);
						String app = createApp(path);
						userModel.addDevModel(model, app);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
	
	}

	public BuilderModelImpl get(String userid, String app) {
		return this.allUsers.get(userid).getBuilderModel(app);
	}

	public BuilderModelImpl getBuilderModel(String userid, String app, String device) {
		return this.allUsers.get(userid).getModel(app, device);
	}
	
	private synchronized BuilderModelImpl generateModel(String userid, String app, String device) {
		return this.allUsers.get(userid).addRuntimeModel(app, device, userid);
	}


	public void createDevice(String userid, String app, String device) {
		generateModel(userid, app, device);
	}

	public void newApplication(String userid, String sesssionID) {
		BuilderModelImpl model = new BuilderModelImpl() {

			@Override
			protected String getAccessToken() {
				return BuilderModelHolder.this.getAccessToken(userid);
			}
			
		};
		
		Map<String, BuilderModelImpl> builderModels = this.allUsers.get(userid).getBuilderModels();
		builderModels.put(String.valueOf(System.currentTimeMillis()), model);
	}

	abstract protected String getAccessToken(String userid);

	public List<String> getApplications(String userid) {
		UserModel userModel = this.allUsers.get(userid);
		Map<String, BuilderModelImpl> builderModels = userModel.getBuilderModels();
		return new ArrayList<String>(builderModels.keySet());
	}

	public void deleteDevice(String userid, String app, String device) {
		this.allUsers.get(userid).removeDevice(device);
	}

	public boolean containsId(String id) {
		return this.allUsers.containsKey(id);
	}

	public void createNewAccount(String id) {
		this.allUsers.put(id, new UserModel());
	}

	public String save(String userid, String app) {
		SbFiles.createTmpFolderIfNotExists();
		
		allUsers.get(userid).getBuilderModel(app).save(SbFiles.TMP_FOLDER);
		
		SbFiles.createFolderIfNotExists(SbFiles.PERSISTENT_FOLDER + "/" + userid);
		String filename = SbFiles.PERSISTENT_FOLDER + "/" + userid + "/" + app + ".zip";
		Zip.zip(SbFiles.TMP_FOLDER, filename);	
		
		allUsers.get(userid).reloadRuntime(SbFiles.TMP_FOLDER);
		return filename;
	}
	
	public void load(String userid) {
		UserModel userModel = new UserModel();
		this.allUsers.put(userid, userModel);
		
		File subFolder = new File(SbFiles.PERSISTENT_FOLDER + "/" + userid);
		
		if (!subFolder.exists()) {
			return;
		}
		
		for (File file2 : subFolder.listFiles()) {
			String filename = file2.getAbsolutePath();
			try {
				BuilderModelImpl model = SbFiles.loadAfile(userid, filename, NO_DEVICE);
				String app = createApp(filename);
				userModel.addDevModel(model, app);
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}
	}

	protected String createApp(String filename) {
		return new File(filename).getName().replace(".zip", "");
	}

	public void load(String userid, String path) {
		try {
			BuilderModelImpl model = SbFiles.loadAfile(userid, path, NO_DEVICE);
			String app = createApp(path);
			this.allUsers.get(userid).addDevModel(model, app);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	public List<String> getActiveDevices(String userid) {
		List<String> ret = new ArrayList<>();
		
		Map<String, BuilderModelImpl> models = this.allUsers.get(userid).getRuntimeModels();
		for (String key : models.keySet()) {
			ret.add(key + "," + models.get(key).getApplicationName());
		}
		return ret;
	}
}

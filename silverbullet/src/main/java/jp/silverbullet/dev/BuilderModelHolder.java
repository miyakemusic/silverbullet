package jp.silverbullet.dev;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import jp.silverbullet.core.Zip;
import jp.silverbullet.core.register2.RegisterUpdates;
import jp.silverbullet.web.WebSocketBroadcaster;

class UserModel {
	private Map<String, BuilderModelImpl> builderModels = new HashMap<>();
	private Map<String, BuilderModelImpl> runtimeModels = new HashMap<>();
	
	public BuilderModelImpl getBuilderModel(String app) {
		return builderModels.get(app);
	}

	public Map<String, BuilderModelImpl> getBuilderModels() {
		return builderModels;
	}

	public Map<String, BuilderModelImpl> getRuntimeModels() {
		return runtimeModels;
	}
	
	
}

public abstract class BuilderModelHolder {
	public static final String PERSISTENT_FOLDER = "./persistent";
	public static final String TMP_FOLDER = PERSISTENT_FOLDER + "/sv_tmp";
	private static final String NO_DEVICE = "NO_DEVICE";
	
	private Map<String, UserModel> allUsers = new HashMap<>();
	
	public BuilderModelHolder() {

	}

	public String save(String userid, String app) {
		createTmpFolderIfNotExists();
		
		allUsers.get(userid).getBuilderModel(app).save(TMP_FOLDER);

		
		createFolderIfNotExists(PERSISTENT_FOLDER + "/" + userid);
		String filename = PERSISTENT_FOLDER + "/" + userid + "/" + app + ".zip";
		Zip.zip(TMP_FOLDER, filename);	
		
		return filename;
	}
	

	private void createFolderIfNotExists(String folder) {
		if (!Files.exists(Paths.get(folder))) {
			try {
				Files.createDirectory(Paths.get(folder));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static synchronized void createTmpFolderIfNotExists() {
		if (!Files.exists(Paths.get(PERSISTENT_FOLDER))) {
			try {
				Files.createDirectory(Paths.get(PERSISTENT_FOLDER));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (!Files.exists(Paths.get(TMP_FOLDER))) {
			try {
				Files.createDirectory(Paths.get(TMP_FOLDER));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void loadAll() {
		createTmpFolderIfNotExists();
		
		for (File file : new File(PERSISTENT_FOLDER).listFiles()) {
			if (file.isDirectory()) {
				if (file.getName().equals(new File(TMP_FOLDER).getName())) {
					continue;
				}
				UserModel userModel = new UserModel();
				String userid = file.getName();
				this.allUsers.put(userid, userModel);
				for (File file2 : file.listFiles()) {

					String filename = file2.getAbsolutePath();
					try {
						BuilderModelImpl model = loadAfile(userid, filename, NO_DEVICE);
						userModel.getBuilderModels().put(new File(filename).getName().replace(".zip", ""), model);
					} catch (IOException e) {
						e.printStackTrace();
					}		
				}
			}			
		}
	}
	
	private synchronized BuilderModelImpl loadAfile(String userid, String filename, String device) throws IOException {
		if (filename.endsWith(".zip")) {
			createTmpFolderIfNotExists();
			FileUtils.cleanDirectory(new File(TMP_FOLDER));
			Zip.unzip(filename, TMP_FOLDER);
			BuilderModelImpl model = new BuilderModelImpl() {
				@Override
				protected String getAccessToken() {
					return BuilderModelHolder.this.getAccessToken(userid);
				}	
			};
			model.load(TMP_FOLDER);			
			model.setDevice(device);
			new SvClientHandler(userid, device, model);
			return model;
		}
		throw new IOException();
	}

	public BuilderModelImpl get(String userid, String app) {
		return this.allUsers.get(userid).getBuilderModel(app);
	}

	public BuilderModelImpl getBuilderModel(String userid, String app, String device) {
		Map<String, BuilderModelImpl> runtimeModels = this.allUsers.get(userid).getRuntimeModels();
		if (!runtimeModels.containsKey(device)) {
			return this.allUsers.get(userid).getBuilderModel(app);
		}
		else {
			BuilderModelImpl model = runtimeModels.get(device);
			return model;
		}
	}
	
	private synchronized BuilderModelImpl generateModel(String userid, String app, String device) {
		try {
			Map<String, BuilderModelImpl> runtimeModels = this.allUsers.get(userid).getRuntimeModels();
			BuilderModelImpl r = this.loadAfile(userid, PERSISTENT_FOLDER + "/" + userid + "/" + app + ".zip", device);
			runtimeModels.put(device, r);
			System.out.println("Runtime Model was generated. :" + 
					device + " @" + Thread.currentThread().getName() + ":" + runtimeModels.hashCode());
			return r;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
		Map<String, BuilderModelImpl> builderModels = this.allUsers.get(userid).getBuilderModels();
		return new ArrayList<String>(builderModels.keySet());
	}

	public void deleteDevice(String userid, String app, String device) {
		Map<String, BuilderModelImpl> runtimeModels = this.allUsers.get(userid).getRuntimeModels();
		runtimeModels.remove(device);
	}

	public boolean containsId(String id) {
		return this.allUsers.containsKey(id);
	}

	public void createNewAccount(String id) {
		this.allUsers.put(id, new UserModel());
	}

	public void load(String userid) {
		UserModel userModel = new UserModel();
		this.allUsers.put(userid, userModel);
		
		File subFolder = new File(PERSISTENT_FOLDER + "/" + userid);
		
		if (!subFolder.exists()) {
			return;
		}
		
		for (File file2 : subFolder.listFiles()) {
			String filename = file2.getAbsolutePath();
			try {
				BuilderModelImpl model = loadAfile(userid, filename, NO_DEVICE);
				userModel.getBuilderModels().put(new File(filename).getName().replace(".zip", ""), model);
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}
	}
}

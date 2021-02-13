package jp.silverbullet.dev;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.silverbullet.core.Zip;
import jp.silverbullet.testspec.NetworkConfiguration;
import jp.silverbullet.testspec.NetworkTestConfigurationHolder;
import jp.silverbullet.testspec.TsPresentationNodes;
import jp.silverbullet.testspec.TsTestSpec;
import jp.silverbullet.web.DeviceProperty;

public abstract class BuilderModelHolder {

	public static final String NO_DEVICE = "NO_DEVICE";
	public static final String DEFAULT_USER_SERIAL = "Default00";
	public static final String DEFAULT_USER_FILE = "silverbullet.zip";
	public static final String DEFAULT_USER_NAME = "silverbullet";
	
	private Map<String, UserModel> allUsers = new HashMap<>();
	private SbFiles sbFiles = new SbFiles();
	
	public BuilderModelHolder() {

	}

	public Automator getAutomator(String userid) {
		return this.allUsers.get(userid).getAutomator();
	}	

	public void loadAll() {
		sbFiles.createTmpFolderIfNotExists();
		
		new SbFiles.WalkThrough() {
			@Override
			protected void handleUserId(String userid, List<String> list) {
				UserModel userModel = new UserModel(userid);
				allUsers.put(userid, userModel);
				
				for (String path : list) {
					BuilderModelImpl model;
					try {
						model = sbFiles.loadAfile(userid, path, NO_DEVICE);
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
		this.allUsers.put(id, new UserModel(id));
	}

	public String save(String userid, String app) {
		String folder = allUsers.get(userid).getBuilderModel(app).save();
		//String folder = allUsers.get(userid).save(app);
		allUsers.get(userid).getAutomator().save(folder + "/Automator.json");
		allUsers.get(userid).getNetworkTestConfig().save(folder);
		
		sbFiles.createFolderIfNotExists(userid);
		String filename = SbFiles.PERSISTENT_FOLDER + "/" + userid + "/" + app + ".zip";
		Zip.zip(folder, filename);	
		
		allUsers.get(userid).reloadRuntime();
		return filename;
	}
	
	public void load(String userid) {
		UserModel userModel = new UserModel(userid);
		this.allUsers.put(userid, userModel);
		
		File subFolder = new File(SbFiles.PERSISTENT_FOLDER + "/" + userid);
		
		if (!subFolder.exists()) {
			return;
		}
		
		for (File file2 : subFolder.listFiles()) {
			String filename = file2.getAbsolutePath();
			String app = createApp(filename);

			try {
				BuilderModelImpl model = sbFiles.loadAfile(userid, app, NO_DEVICE);
				userModel.addDevModel(model, app);
				allUsers.get(userid).getAutomator().load(SbFiles.TMP_FOLDER + "/Automator.json");
				allUsers.get(userid).getNetworkTestConfig().load(SbFiles.TMP_FOLDER);
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
			BuilderModelImpl model = sbFiles.loadAfile(userid, path, NO_DEVICE);
			String app = createApp(path);
			this.allUsers.get(userid).addDevModel(model, app);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	public List<DeviceProperty> getActiveDevices(String userid) {
		List<DeviceProperty> ret = new ArrayList<>();
		
		Map<String, BuilderModelImpl> models = this.allUsers.get(userid).getRuntimeModels();
		for (String key : models.keySet()) {
			DeviceProperty prop = new DeviceProperty(key, models.get(key).getApplicationName());
			ret.add(prop);
		}
		return ret;
	}

	public String getStorePath(String userid) {
		return sbFiles.getStorePath(userid);
	}
	
	public List<String> getStorePaths(String userid) {
		List<String> ret = new ArrayList<>();
		for (String path : sbFiles.getStorePaths(userid)) {
			ret.add(new File(path).getName());
		}
		return ret;
	}

	public void setPath(String persistentPath) {
		SbFiles.setPERSISTENT_FOLDER(persistentPath);
	}

	public NetworkTestConfigurationHolder getTestConfig(String userid) {
		return this.allUsers.get(userid).getNetworkTestConfig();
	}

	public void registerScript(String projectName, String userid) {
		UserModel userModel = this.allUsers.get(userid);
		TsTestSpec testSpec = userModel.getNetworkTestConfig().testSpec(projectName);
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(projectName);
		testSpec.nodes().forEach(node -> {
			stringBuilder.append("." + node);
		});
		List<String> script = testSpec.script();
		String s = "";
		for (String line : script) {
			s += line + "\n";
		}
		userModel.getAutomator().register(stringBuilder.toString(), s);
	}
}

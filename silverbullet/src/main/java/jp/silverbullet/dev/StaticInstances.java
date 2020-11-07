package jp.silverbullet.dev;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.IOUtils;

import jp.silverbullet.dev.sourcegenerator.PropertySourceGenerator;
import jp.silverbullet.dev.sourcegenerator.RegisterSourceGenerator;
import jp.silverbullet.web.SilverBulletServer;
import jp.silverbullet.web.UserStore;
import jp.silverbullet.web.WebSocketBroadcaster;
import jp.silverbullet.web.auth.PersonalResponse;

public class StaticInstances {
//	public enum ServerMode {
//		DEV,
//		RUNTIME
//	}
//	private ServerMode serverMode;
//	public void setServerMode(String mode) {
//		serverMode = ServerMode.valueOf(mode);
//	}
//
//	public ServerMode getServerMode() {
//		return serverMode;
//	}
	
	private UserStore userStore = new UserStore();
	private BuilderModelHolder builderModelHolder = new BuilderModelHolder() {

		@Override
		protected String getAccessToken(String userid) {
			return userStore.getData().get(userid).getPersonal().getAccess_token();
		}
		
	};
	
	public UserStore getUserStore() {
		return userStore;
	}

	public StaticInstances() {

	}
	
	public String save(String sessionName, String app) {
		String userid = userStore.findBySessionName(sessionName).getPersonal().id;
		return this.builderModelHolder.save(userid, app);
	}


	public void load() {
		for (String userid : this.userStore.getData().keySet()) {
			this.builderModelHolder.load(userid);
		}
	}

	public BuilderModelHolder getBuilderModelHolder() {
		return builderModelHolder;
	}

	public BuilderModelImpl getBuilderModel(String sessionName, String app) {
		String userid = userStore.findBySessionName(sessionName).getPersonal().id;
		return this.builderModelHolder.get(userid, app);
	}

	public BuilderModelImpl getBuilderModelBySessionName(String sessionName, String app, String device) {
		String userid = userStore.findBySessionName(sessionName).getPersonal().id;
		return this.builderModelHolder.getBuilderModel(userid, app, device);
	}

	public BuilderModelImpl getBuilderModelByUserId(String userid, String app, String device) {
		return this.builderModelHolder.getBuilderModel(userid, app, device);
	}
	
	public void generateSource(String sessionName, String app) {
		String info = getBuilderModel(sessionName, app).getSourceInfo();
		String folder = info.split(";")[0];
		String packageName = info.split(";")[1];
		new PropertySourceGenerator(getBuilderModel(sessionName, app).getPropertiesHolder2()).generate(folder, packageName);
		new RegisterSourceGenerator(getBuilderModel(sessionName, app).getRegisterSpecHolder()).
			exportFile(folder, packageName);
	}

	public void newApplication(String sessionName) {
		String userid = userStore.findBySessionName(sessionName).getPersonal().id;
		this.builderModelHolder.newApplication(userid, sessionName);
	}

	public List<String> getApplications(String sessionName) {
		String userid = userStore.findBySessionName(sessionName).getPersonal().id;
		return this.builderModelHolder.getApplications(userid);
	}

	public void createDevice(String userid, String app, String device) {
		this.builderModelHolder.createDevice(userid, app, device);
	}

	public void deleteDevice(String userid, String app, String device) {
		this.builderModelHolder.deleteDevice(userid, app, device);
	}

	public void login(String sessionName, PersonalResponse personal) {
		userStore.put(sessionName, personal);
		if (!builderModelHolder.containsId(personal.id)) {
			builderModelHolder.createNewAccount(personal.id);
		}
	}

	public String getUserID(String sessionName) {
		return this.userStore.findBySessionName(sessionName).getPersonal().id;
	}

	public void sendMessageToDevice(String sessionName, String app, String device, String message) {
		String userid = userStore.findBySessionName(sessionName).getPersonal().id;
		WebSocketBroadcaster.getInstance().sendMessageToDomainModel(userid, device, message);

	}

	public void copyConfigToDefault(String id, String application) {
		String sourceFile = BuilderModelHolder.PERSISTENT_FOLDER + "/" + id + "/" + application + ".zip";
		String targetFolder = BuilderModelHolder.PERSISTENT_FOLDER + "/" + BuilderModelHolder.DEFAULT_USER_SERIAL;
		if (!Files.exists(Paths.get(targetFolder))) {
	 		try {
				Files.createDirectory(Paths.get(targetFolder));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}		
		}

		String targetFile = BuilderModelHolder.PERSISTENT_FOLDER + "/" + BuilderModelHolder.DEFAULT_USER_SERIAL + 
				"/" + BuilderModelHolder.DEFAULT_USER_FILE;
		
		try {
			Files.copy(Paths.get(sourceFile), Paths.get(targetFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createFile(String sessionName, byte bytes[], String filename) {
		String userid = userStore.findBySessionName(sessionName).getPersonal().id;
		String path = BuilderModelHolder.PERSISTENT_FOLDER + "/" + userid + "/" + filename;
		
		try {
			Files.write(Paths.get(path), bytes);
			this.builderModelHolder.load(userid, path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void createUser(PersonalResponse personal) {
		userStore.put(personal);
		getBuilderModelHolder().createNewAccount(personal.id);
		try {
			Files.createDirectories(Paths.get(BuilderModelHolder.PERSISTENT_FOLDER + "/" + personal.id));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



}

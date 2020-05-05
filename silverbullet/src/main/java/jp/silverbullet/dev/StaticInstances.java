package jp.silverbullet.dev;

import java.util.List;

import jp.silverbullet.core.register2.RegisterUpdates;
import jp.silverbullet.dev.sourcegenerator.PropertySourceGenerator;
import jp.silverbullet.dev.sourcegenerator.RegisterSourceGenerator;
import jp.silverbullet.web.UserStore;
import jp.silverbullet.web.WebSocketBroadcaster;
import jp.silverbullet.web.auth.PersonalResponse;

public class StaticInstances {

	private UserStore userStore = new UserStore();
	private BuilderModelHolder builderModelHolder = new BuilderModelHolder();
	
	public UserStore getUserStore() {
		return userStore;
	}

	public StaticInstances() {

	}
	
	public String save(String sessionID, String app) {
		String userid = userStore.findBySessionID(sessionID).getPersonal().id;
		return this.builderModelHolder.save(userid, app);
	}


	public void load() {
		for (String userid : this.userStore.getData().keySet()) {
			this.builderModelHolder.load(userid);
		}
	}

	public BuilderModelImpl getBuilderModel(String sessionID, String app) {
		String userid = userStore.findBySessionID(sessionID).getPersonal().id;
		return this.builderModelHolder.get(userid, app);
	}

	public BuilderModelImpl getBuilderModelBySessionID(String sessionID, String app, String device) {
		String userid = userStore.findBySessionID(sessionID).getPersonal().id;
		return this.builderModelHolder.getBuilderModel(userid, app, device);
	}

	public BuilderModelImpl getBuilderModelByUserId(String userid, String app, String device) {
		return this.builderModelHolder.getBuilderModel(userid, app, device);
	}
	
	public void generateSource(String sessionID, String app) {
		String userid = userStore.findBySessionID(sessionID).getPersonal().id;
		String info = getBuilderModel(userid, app).getSourceInfo();
		String folder = info.split(";")[0];
		String packageName = info.split(";")[1];
		new PropertySourceGenerator(getBuilderModel(userid, app).getPropertiesHolder2()).generate(folder, packageName);
		new RegisterSourceGenerator(getBuilderModel(userid, app).getRegisterSpecHolder()).
			exportFile(folder, packageName);
	}

	public void newApplication(String sessionID) {
		String userid = userStore.findBySessionID(sessionID).getPersonal().id;
		this.builderModelHolder.newApplication(userid, sessionID);
	}


	public List<String> getApplications(String sessionID) {
		String userid = userStore.findBySessionID(sessionID).getPersonal().id;
		return this.builderModelHolder.getApplications(userid);
	}

	public void createDevice(String userid, String app, String device) {
		this.builderModelHolder.createDevice(userid, app, device);
	}

	public void deleteDevice(String userid, String app, String device) {
//		String userid = userStore.findByCookie(cookie).personal.id;
		this.builderModelHolder.deleteDevice(userid, app, device);
	}

	public void login(String sessionID, PersonalResponse personal) {
		//UserStore userStore = SilverBulletServer.getStaticInstance().getUserStore();
		userStore.put(sessionID, personal);
		if (!builderModelHolder.containsId(personal.id)) {
			builderModelHolder.createNewAccount(personal.id);
		}
	}

	public String getUserID(String sessionId) {
		return this.userStore.findBySessionID(sessionId).getPersonal().id;
	}

	public void sendMessageToDevice(String sessionID, String app, String device, String message) {
		String userid = userStore.findBySessionID(sessionID).getPersonal().id;
		WebSocketBroadcaster.getInstance().sendMessageToDomainModel(userid, device, message);

	}


}

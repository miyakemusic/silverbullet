package jp.silverbullet;

import jp.silverbullet.handlers.EasyAccessModel;
import jp.silverbullet.web.BuilderServer;
import jp.silverbullet.web.BuilderServerListener;
import jp.silverbullet.web.WebClientManager;
import obsolute.BuilderModel;
import obsolute.register.RegisterAccess;
import obsolute.register.RegisterMapModel;

public abstract class SilverBulletServer {

	static private BuilderServer webServer;
	static private BuilderModelImpl builderModel;
	static private RegisterMapModel registerMapModel;

	public void start(String port) {
		String filename = getDefaultFilename();
		StaticInstances.getInstance().load(filename);
		builderModel = StaticInstances.getInstance().getBuilderModel();
		builderModel.setUserPath(getUserPath());

//		registerMapModel = StaticInstances.getInstance().getBuilderModel().getRegisterMapModel();
			
		startWebServer(Integer.valueOf(port));
	}

	protected abstract String getDefaultFilename();
	protected abstract String getUserPath();
	protected abstract void onStart(BuilderModelImpl model);
	
	protected void startWebServer(Integer port) {
		new WebClientManager();
		webServer = new BuilderServer(port, new BuilderServerListener() {
			@Override
			public void onStarted() {
				SilverBulletServer.this.onStart(builderModel);
			}
		});
	}

	

}

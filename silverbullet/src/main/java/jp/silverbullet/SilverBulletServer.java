package jp.silverbullet;

import java.nio.file.Files;
import java.nio.file.Paths;

import jp.silverbullet.register.RegisterMapModel;
import jp.silverbullet.web.BuilderServer;
import jp.silverbullet.web.BuilderServerListener;
import jp.silverbullet.web.WebClientManager;

public abstract class SilverBulletServer {

	static private BuilderServer webServer;
	static private BuilderModel builderModel;
	static private RegisterMapModel registerMapModel;

	public void start() {
		builderModel = StaticInstances.getBuilderModel();
		builderModel.setUserPath(getUserPath());
		registerMapModel = StaticInstances.getRegisterMapModel();
		
		String filename = getDefaultFilename();

		StaticInstances.load(filename);
		
		startWebServer();
	}

	protected abstract String getDefaultFilename();
	protected abstract String getUserPath();
	
	protected void startWebServer() {
		new WebClientManager();
		webServer = new BuilderServer(8081, new BuilderServerListener() {
			@Override
			public void onStarted() {
			}
		});
	}

}

package jp.silverbullet;

import jp.silverbullet.handlers.EasyAccessModel;
import jp.silverbullet.handlers.RegisterAccess;

public class SampleMain extends SilverBulletServer {

	@Override
	protected String getDefaultFilename() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getUserPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onStart(EasyAccessModel easyAccess, RegisterAccess registerAccess) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		new SampleMain().start();
	}

}

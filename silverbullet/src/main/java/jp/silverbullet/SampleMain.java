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


	public static void main(String[] args) {
		new SampleMain().start(args[0]);
	}

	@Override
	protected void onStart(BuilderModel model) {
		// TODO Auto-generated method stub
		
	}

}

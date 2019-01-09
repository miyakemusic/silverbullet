package jp.silverbullet;

import java.util.List;

import jp.silverbullet.register2.RegisterAccessor;

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
	protected void onStart(BuilderModelImpl model) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected List<RegisterAccessor> getSimulators() {
		// TODO Auto-generated method stub
		return null;
	}

}

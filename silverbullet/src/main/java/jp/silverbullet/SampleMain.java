package jp.silverbullet;

import java.util.List;

import jp.silverbullet.register2.RegisterAccessor;
import jp.silverbullet.sequncer.UserSequencer;
import jp.silverbullet.web.ui.part2.UiBuilder;

public class SampleMain extends SilverBulletServer {

	@Override
	protected String getDefaultFilename() {
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
	
	@Override
	protected RegisterAccessor getHardwareAccessor(BuilderModelImpl model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UserSequencer> getUserSequencers(BuilderModelImpl model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int getInstanceCount() {
		return 1;
	}

	@Override
	protected UiBuilder getUi() {
		// TODO Auto-generated method stub
		return null;
	}

}

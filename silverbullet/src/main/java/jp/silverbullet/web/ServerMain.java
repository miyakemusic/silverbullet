package jp.silverbullet.web;

import java.util.List;

import jp.silverbullet.core.register2.RegisterAccessor;
import jp.silverbullet.core.sequncer.UserSequencer;
import jp.silverbullet.core.ui.part2.UiBuilder;
import jp.silverbullet.dev.BuilderModelImpl;

public class ServerMain extends SilverBulletServer {

	@Override
	protected String getDefaultFilename() {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) {
		new ServerMain().start(args[0], args[1], args[2]);
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
		return null;
	}

	@Override
	protected List<UserSequencer> getUserSequencers(BuilderModelImpl model) {
//		return Arrays.asList(new WebSequencer(""));
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

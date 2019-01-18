package jp.silverbullet;

import java.util.ArrayList;
import java.util.List;

import jp.silverbullet.register2.RegisterAccessor;
import jp.silverbullet.sequncer.UserSequencer;
import jp.silverbullet.web.BuilderServer;
import jp.silverbullet.web.BuilderServerListener;
import jp.silverbullet.web.WebClientManager;

public abstract class SilverBulletServer {

	private BuilderServer webServer;
	public void start(String port) {
		String filename = getDefaultFilename();
		
		StaticInstances.getInstance().createInstances(getInstanceCount());
		StaticInstances.getInstance().load(filename);

		startWebServer(Integer.valueOf(port));
	}

	protected abstract String getDefaultFilename();
	protected abstract void onStart(BuilderModelImpl model);
	protected abstract List<RegisterAccessor> getSimulators();
	protected abstract String getBaseFolderAndPackage();
	protected abstract RegisterAccessor getHardwareAccessor(BuilderModelImpl model);
	protected abstract List<UserSequencer> getUserSequencers(BuilderModelImpl model);
	protected abstract int getInstanceCount();
	
	protected void startWebServer(Integer port) {
		new WebClientManager();
		webServer = new BuilderServer(port, new BuilderServerListener() {
			@Override
			public void onStarted() {
				StaticInstances.getInstance().getBuilderModels().forEach(builderModel -> {
					onStart(builderModel);
					builderModel.setSimulators(getSimulators());
					builderModel.setSourceInfo(getBaseFolderAndPackage());
					builderModel.setHardwareAccessor(getHardwareAccessor(builderModel));
					getUserSequencers(builderModel).forEach(sequencer -> {
						builderModel.getSequencer().addUserSequencer(sequencer);
					});	
				});				
			}
		});
	}

}

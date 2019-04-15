package jp.silverbullet;

import java.util.List;

import jp.silverbullet.register2.RegisterAccessor;
import jp.silverbullet.sequncer.UserSequencer;
import jp.silverbullet.web.BuilderServer;
import jp.silverbullet.web.BuilderServerListener;
import jp.silverbullet.web.WebClientManager;
import jp.silverbullet.web.ui.part2.Pane;
import jp.silverbullet.web.ui.part2.UiBuilder;

public abstract class SilverBulletServer {

	public void start(String port) {
		String filename = getDefaultFilename();
		
		StaticInstances.getInstance().createInstances(getInstanceCount(), Thread.currentThread().getId());
		StaticInstances.getInstance().load(filename);

		StaticInstances.getInstance().getBuilderModels().forEach(builderModel -> {
			builderModel.setUiBuilder(getUi());
			onStart(builderModel);
			builderModel.setSimulators(getSimulators());
			builderModel.setSourceInfo(getBaseFolderAndPackage());
			builderModel.setHardwareAccessor(getHardwareAccessor(builderModel));
			getUserSequencers(builderModel).forEach(sequencer -> {
				builderModel.getSequencer().addUserSequencer(sequencer);
			});	

		});	
		new WebClientManager();
		
		startWebServer(Integer.valueOf(port));
		
	}

	protected abstract String getDefaultFilename();
	protected abstract void onStart(BuilderModelImpl model);
	protected abstract List<RegisterAccessor> getSimulators();
	protected abstract String getBaseFolderAndPackage();
	protected abstract RegisterAccessor getHardwareAccessor(BuilderModelImpl model);
	protected abstract List<UserSequencer> getUserSequencers(BuilderModelImpl model);
	
	protected UiBuilder getUi() {
		UiBuilder builder = new UiBuilder();
		Pane pane = builder.getRootPane();
		pane.css("width", "800").css("height", "600").css("top", "150px").css("border-style", "dashed").css("border-width", "1px");
		return builder;
	}
	
	protected int getInstanceCount() {
		// currently only one instance is supported
		return 1;
	}
	
	protected void startWebServer(Integer port) {
		
		new BuilderServer(port, new BuilderServerListener() {
			@Override
			public void onStarted() {
			
			}
		});
	}

}

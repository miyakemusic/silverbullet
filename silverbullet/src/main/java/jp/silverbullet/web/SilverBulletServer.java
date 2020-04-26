package jp.silverbullet.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.core.RuntimeListener;
import jp.silverbullet.core.dependency2.ChangedItemValue;
import jp.silverbullet.core.dependency2.DependencyListener;
import jp.silverbullet.core.dependency2.Id;
import jp.silverbullet.core.property2.PropertyDefHolderListener;
import jp.silverbullet.core.register2.BitUpdates;
import jp.silverbullet.core.register2.RegisterAccessor;
import jp.silverbullet.core.register2.RegisterAccessorListener;
import jp.silverbullet.core.register2.RegisterUpdates;
import jp.silverbullet.core.sequncer.SequencerListener;
import jp.silverbullet.core.sequncer.UserSequencer;
import jp.silverbullet.core.sequncer.SystemAccessor.DialogAnswer;
import jp.silverbullet.core.ui.part2.Pane;
import jp.silverbullet.core.ui.part2.UiBuilder;
import jp.silverbullet.core.ui.part2.UiBuilderListener;
import jp.silverbullet.core.ui.part2.WidgetType;
import jp.silverbullet.dev.BuilderModelImpl;
import jp.silverbullet.dev.StaticInstances;
import jp.silverbullet.dev.test.TestRecorderListener;

public abstract class SilverBulletServer {
	protected abstract String getDefaultFilename();
	protected abstract void onStart(BuilderModelImpl model);
	protected abstract List<RegisterAccessor> getSimulators();
	protected abstract RegisterAccessor getHardwareAccessor(BuilderModelImpl model);
	protected abstract List<UserSequencer> getUserSequencers(BuilderModelImpl model);
	
	private static StaticInstances staticInstance = new StaticInstances();
	private static boolean debugEnabled;
	
	public static StaticInstances getStaticInstance() {
		return staticInstance;
	}

	public void start(String port, String protocol) {
		staticInstance.load();

//		BuilderModelImpl model = staticInstance.getBuilderModel();
//		this.onStart(model);
//		model.setSimulators(getSimulators());
//		model.setHardwareAccessor(getHardwareAccessor(model));
//		getUserSequencers(model).forEach(sequencer -> {
//			model.addUserSequencer(sequencer);
//		});	
//		model.setDefaultValues();
		
//		registerWebClientManager();
		
		startWebServer(Integer.valueOf(port), protocol);
		
	}

	private Object syncDialog = new Object();
	private DialogAnswer answerDialog = null;
		
	protected UiBuilder getUi() {
		UiBuilder builder = new UiBuilder();
		Pane pane = builder.getRootPane();
//		pane.css("width", "800").css("height", "600").css("top", "150px").css("border-style", "dashed").css("border-width", "1px");
		return builder;
	}
	
	protected int getInstanceCount() {
		// currently only one instance is supported
		return 1;
	}
	
	protected void startWebServer(Integer port, String protocol) {
		
		new BuilderServer(port, protocol, new BuilderServerListener() {
			@Override
			public void onStarted() {
			
			}
		});
	}
	public static void setDebugEnabled(boolean enabled) {
		debugEnabled = enabled;
	}

}

package jp.silverbullet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.dependency2.ChangedItemValue;
import jp.silverbullet.dependency2.DependencyListener;
import jp.silverbullet.dependency2.Id;
import jp.silverbullet.dependency2.design.RestrictionMatrixElement;
import jp.silverbullet.dependency2.design.RestrictionMatrixListener;
import jp.silverbullet.property2.PropertyDefHolderListener;
import jp.silverbullet.register2.BitUpdates;
import jp.silverbullet.register2.RegisterAccessor;
import jp.silverbullet.register2.RegisterAccessorListener;
import jp.silverbullet.register2.RegisterUpdates;
import jp.silverbullet.sequncer.UserSequencer;
import jp.silverbullet.test.TestRecorderListener;
import jp.silverbullet.web.BuilderServer;
import jp.silverbullet.web.BuilderServerListener;
import jp.silverbullet.web.WebSocketBroadcaster;
import jp.silverbullet.web.WebSocketMessage;
import jp.silverbullet.web.ui.UiLayoutListener;
import jp.silverbullet.web.ui.part2.Pane;
import jp.silverbullet.web.ui.part2.UiBuilder;
import jp.silverbullet.web.ui.part2.UiBuilderListener;
import jp.silverbullet.web.ui.part2.WidgetType;

public abstract class SilverBulletServer {
	protected abstract String getDefaultFilename();
	protected abstract void onStart(BuilderModelImpl model);
	protected abstract List<RegisterAccessor> getSimulators();
	protected abstract String getBaseFolderAndPackage();
	protected abstract RegisterAccessor getHardwareAccessor(BuilderModelImpl model);
	protected abstract List<UserSequencer> getUserSequencers(BuilderModelImpl model);
	
	private static StaticInstances staticInstance = new StaticInstances();
	
	public static StaticInstances getStaticInstance() {
		return staticInstance;
	}

	public void start(String port) {
		String filename = getDefaultFilename();
		
		staticInstance.createInstances(getInstanceCount(), Thread.currentThread().getId());
		staticInstance.load(filename);

		staticInstance.getBuilderModels().forEach(builderModel -> {
			builderModel.setUiBuilder(getUi());
			onStart(builderModel);
			builderModel.setSimulators(getSimulators());
			builderModel.setSourceInfo(getBaseFolderAndPackage());
			builderModel.setHardwareAccessor(getHardwareAccessor(builderModel));
			getUserSequencers(builderModel).forEach(sequencer -> {
				builderModel.getSequencer().addUserSequencer(sequencer);
			});	
			builderModel.setDefaultValues();
		});	

		registerWebClientManager();
		
		startWebServer(Integer.valueOf(port));
		
	}

	private void registerWebClientManager() {

		staticInstance.getBuilderModel().getDependency().addDependencyListener(new DependencyListener() {
			@Override
			public boolean confirm(String history) {
				return true;
			}

			@Override
			public void onResult(Map<String, List<ChangedItemValue>> changedHistory) {
			}

			@Override
			public void onCompleted(String message) {
				WebSocketBroadcaster.getInstance().sendMessageAsync("VALUES", message);
			}

			@Override
			public void onStart(Id id, String value) {
			}

			@Override
			public void onRejected(Id id) {
				WebSocketBroadcaster.getInstance().sendMessageAsync("VALUES", id.toString());
			}		
		});
	
		staticInstance.getBuilderModel().getPropertiesHolder2().addListener(new PropertyDefHolderListener() {
			@Override
			public void onChange(String id, String fieldName, Object value, Object prevValue) {
				try {
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("ID", "Change:" + id));
					WebSocketBroadcaster.getInstance().sendMessage(str);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onAdd(String id) {
				try {
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("ID", "Add:" + id));
					WebSocketBroadcaster.getInstance().sendMessage(str);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onRemove(String id, String replacedId) {
				try {
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("ID", "Remove:" + id + ":" + replacedId));
					WebSocketBroadcaster.getInstance().sendMessage(str);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		});
		
		staticInstance.getBuilderModel().getUiLayoutHolder().addListener(new UiLayoutListener() {
			@Override
			public void onLayoutChange(String div, String currentFilename) {
				try {
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("DESIGN", "layoutChanged:" + div));
					WebSocketBroadcaster.getInstance().sendMessage(str);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		});

		staticInstance.getBuilderModel().getRegisterAccessor().addListener(new RegisterAccessorListener() {
			@Override
			public void onUpdate(Object regName, Object bitName, int value) {
				try {
					RegisterUpdates updates = new RegisterUpdates();
					updates.setName(regName.toString());
					updates.setBits(Arrays.asList(new BitUpdates(bitName.toString(), String.valueOf(value))));
					String val = new ObjectMapper().writeValueAsString(updates);
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("REGVAL", val));
					WebSocketBroadcaster.getInstance().sendMessage(str);
				} catch (JsonGenerationException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onUpdate(Object regName, byte[] image) {				
			}

			@Override
			public void onInterrupt() {
				try {
					RegisterUpdates updates = new RegisterUpdates();
					updates.setName("@Interrupt@");
					String val = new ObjectMapper().writeValueAsString(updates);
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("REGVAL", val));
					WebSocketBroadcaster.getInstance().sendMessage(str);
				} catch (JsonGenerationException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		});
		
		staticInstance.getBuilderModel().getTestRecorder().addListener(new TestRecorderListener() {
			@Override
			public void onTestFinished() {
				try {
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("TEST", "TestFinished"));
					WebSocketBroadcaster.getInstance().sendMessage(str);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onTestStart() {
			}

			@Override
			public void onAdd(String string) {				
			}

			@Override
			public void onUpdate() {				
			}
		});
		
		staticInstance.getBuilderModel().getRestrictionMatrix().addListener(new RestrictionMatrixListener() {
			@Override
			public void onMatrixChanged(RestrictionMatrixElement[][] value) {
				try {
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("DEPDESIGN", "MatrixChanged"));
					WebSocketBroadcaster.getInstance().sendMessage(str);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		});
		
		staticInstance.getBuilderModel().getUiBuilder().addListener(new UiBuilderListener() {
			@Override
			public void onCssUpdate(String widgetId, String key, String value) {
				try {
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("UIDESIGN", "CSS:" + widgetId + "," + key + "," + value));
					WebSocketBroadcaster.getInstance().sendMessage(str);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onTypeUpdate(String widgetId, WidgetType type) {
				try {
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("UIDESIGN", "TYPE:" + widgetId + "," + type.toString()));
					WebSocketBroadcaster.getInstance().sendMessage(str);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onIdChange(String id, String subId) {
				try {
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("UIDESIGN", "ID:" + id + "," + id + "," + subId));
					WebSocketBroadcaster.getInstance().sendMessage(str);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFieldChange(String id) {
				try {
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("UIDESIGN", "FIELD:" + id + "," + id));
					WebSocketBroadcaster.getInstance().sendMessage(str);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onLayoutChange(String id) {
				try {
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("UIDESIGN", "LAYOUT:" + id + "," + id));
					WebSocketBroadcaster.getInstance().sendMessage(str);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		});		
	}
	
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
	
	protected void startWebServer(Integer port) {
		
		new BuilderServer(port, new BuilderServerListener() {
			@Override
			public void onStarted() {
			
			}
		});
	}

}

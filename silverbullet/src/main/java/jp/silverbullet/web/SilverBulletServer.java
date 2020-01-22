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
		String filename = getDefaultFilename();
		
//		staticInstance.createInstances(getInstanceCount());
		staticInstance.load(filename);

		BuilderModelImpl model = staticInstance.getBuilderModel();
		this.onStart(model);
		model.setSimulators(getSimulators());
		model.setHardwareAccessor(getHardwareAccessor(model));
		getUserSequencers(model).forEach(sequencer -> {
			model.addUserSequencer(sequencer);
		});	
		model.setDefaultValues();
		
//		staticInstance.getBuilderModels().forEach(builderModel -> {
//			builderModel.setUiBuilder(getUi());
//			onStart(builderModel);
//			builderModel.setSimulators(getSimulators());
//			builderModel.setHardwareAccessor(getHardwareAccessor(builderModel));
//			getUserSequencers(builderModel).forEach(sequencer -> {
//				builderModel.getSequencer().addUserSequencer(sequencer);
//			});	
//			builderModel.setDefaultValues();
//		});	

		registerWebClientManager();
		
		startWebServer(Integer.valueOf(port), protocol);
		
	}

	private Object syncDialog = new Object();
	private DialogAnswer answerDialog = null;
	
	private void registerWebClientManager() {

		staticInstance.getBuilderModel().addRuntimeListener(new RuntimeListener() {
			@Override
			public DialogAnswer dialog(String message) {
				message = message.replace("\n", "<br>");
				WebSocketBroadcaster.getInstance().sendMessageAsync("MESSAGE", message);
				synchronized(syncDialog) {
					try {
						syncDialog.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				WebSocketBroadcaster.getInstance().sendMessageAsync("MESSAGE", "@CLOSE@");
				return answerDialog;
			}

			@Override
			public void onReply(String messageId, String reply) {
				answerDialog = DialogAnswer.valueOf(reply);
				synchronized(syncDialog) {
					syncDialog.notify();
				}
			}

			@Override
			public void message(String message) {
				WebSocketBroadcaster.getInstance().sendMessageAsync("MESSAGE", message);
			}
		});
		
		List<String> depHistory = new ArrayList<>();
		staticInstance.getBuilderModel().getDependency().addDependencyListener(new DependencyListener() {
			@Override
			public boolean confirm(String history) {
				return true;
			}

			@Override
			public void onResult(Map<String, List<ChangedItemValue>> changedHistory) {
			//	System.out.println(changedHistory);
			}

			@Override
			public void onCompleted(String message) {
				WebSocketBroadcaster.getInstance().sendMessageAsync("VALUES", message);
				if (debugEnabled) {
					WebSocketBroadcaster.getInstance().sendMessageAsync("DEBUG", toHtml(depHistory));
				}
			}

			private String toHtml(List<String> lines) {
				StringBuilder sb = new StringBuilder();
				for (String s : lines) {
					sb.append(s + "<br>"); 
				}
				return sb.toString();
			}

			@Override
			public void onStart(Id id, String value) {
//				depHistory.add("--------start-------------------------- "+ Thread.currentThread().getId());
			}

			@Override
			public void onRejected(Id id, String message) {
				WebSocketBroadcaster.getInstance().sendMessageAsync("VALUES", id.toString());
				WebSocketBroadcaster.getInstance().sendMessageAsync("MESSAGE", message);
				
			}

			@Override
			public void onProgress(List<String> log) {
				depHistory.addAll(log);
				depHistory.add("----------------------------------------------- "+ Thread.currentThread().getId());
			}		
		});
		staticInstance.getBuilderModel().getSequencer().addSequencerListener(new SequencerListener() {

			@Override
			public void onChangedBySystem(String id, String value) {
				depHistory.add("--------------- by STSTEM ----------------- " + Thread.currentThread().getId());
			}

			@Override
			public void onChangedByUser(String id, String value) {
				depHistory.clear();
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

			@Override
			public void onLoad() {
				// TODO Auto-generated method stub
				
			}

		});
		
//		staticInstance.getBuilderModel().getUiLayoutHolder().addListener(new UiLayoutListener() {
//			@Override
//			public void onLayoutChange(String div, String currentFilename) {
//				try {
//					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("DESIGN", "layoutChanged:" + div));
//					WebSocketBroadcaster.getInstance().sendMessage(str);
//				} catch (JsonProcessingException e) {
//					e.printStackTrace();
//				}
//			}
//		});

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
		
//		staticInstance.getBuilderModel().getRestrictionMatrix().addListener(new RestrictionMatrixListener() {
//			@Override
//			public void onMatrixChanged(RestrictionMatrixElement[][] value) {
//				try {
//					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("DEPDESIGN", "MatrixChanged"));
//					WebSocketBroadcaster.getInstance().sendMessage(str);
//				} catch (JsonProcessingException e) {
//					e.printStackTrace();
//				}
//			}
//		});
		
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

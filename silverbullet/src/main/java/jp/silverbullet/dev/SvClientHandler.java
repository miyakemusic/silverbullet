package jp.silverbullet.dev;

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
import jp.silverbullet.core.register2.RegisterAccessorListener;
import jp.silverbullet.core.register2.RegisterUpdates;
import jp.silverbullet.core.sequncer.SequencerListener;
import jp.silverbullet.core.sequncer.SystemAccessor.DialogAnswer;
import jp.silverbullet.core.ui.part2.UiBuilderListener;
import jp.silverbullet.core.ui.part2.WidgetType;
import jp.silverbullet.dev.test.TestRecorderListener;
import jp.silverbullet.web.MessageToDevice;
import jp.silverbullet.web.WebSequencer;
import jp.silverbullet.web.WebSocketBroadcaster;
import jp.silverbullet.web.WebSocketMessage;

public class SvClientHandler {
	private static boolean debugEnabled = false;
	private Object syncDialog = new Object();
	private DialogAnswer answerDialog = null;
	private String userid;
	
	public SvClientHandler(String userid, String device, BuilderModelImpl model) {
		this.userid = userid;
		if (!device.equals(BuilderModelHolder.NO_DEVICE)) {
			model.addUserSequencer(new WebSequencer(userid, device));
		}
		model.addRuntimeListener(new RuntimeListener() {
			@Override
			public DialogAnswer dialog(String message) {
				message = message.replace("\n", "<br>");
				sendMessagePromptAsync(device, message, new ControlObject());
				synchronized(syncDialog) {
					try {
						syncDialog.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				sendMessagePromptAsync(device, "@CLOSE@", new ControlObject());
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
			public void message(String message, ControlObject controls, String messageId) {
				sendMessagePromptAsync(device, message, controls);
			}

			@Override
			public void closeMessage(String messageId) {
				sendMessageClose(device, messageId);
			}
		});
	
		List<String> depHistory = new ArrayList<>();
		model.getDependency().addDependencyListener(new DependencyListener() {
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
//				if (sourceId != null && !device.equals(sourceId.getSource())) {
					sendMessageValueAsync(device, message);
					if (debugEnabled) {
						sendMessageValueAsync("DEBUG@" + device, toHtml(depHistory));
					}
					
					// This is tentative
					WebSocketBroadcaster.getInstance().sendMessageAsync(userid, "TESTRESULT", message);
//				}
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
	//			depHistory.add("--------start-------------------------- "+ Thread.currentThread().getId());
			}
	
			@Override
			public void onRejected(Id id, String message) {
				sendMessageValueAsync("VALUES@" + device, id.toString());
				sendMessageValueAsync("MESSAGE@" + device, message);
				
			}
	
			@Override
			public void onProgress(List<String> log) {
				depHistory.addAll(log);
				depHistory.add("----------------------------------------------- "+ Thread.currentThread().getId());
			}
		
		});
		model.getSequencer().addSequencerListener(new SequencerListener() {
	
			@Override
			public void onChangedBySystem(Id id, String value) {
				depHistory.add("--------------- by STSTEM ----------------- " + Thread.currentThread().getId());
			}
	
			@Override
			public void onChangedByUser(Id id, String value) {
				depHistory.clear();
			}
			
		});
		
		// Property's information
		model.getPropertiesHolder2().addListener(new PropertyDefHolderListener() {
			@Override
			public void onChange(String id, String fieldName, Object value, Object prevValue) {
				try {
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("ID", "Change:" + id));
					sendMessage(str);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
	
			@Override
			public void onAdd(String id) {
				try {
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("ID", "Add:" + id));
					sendMessage(str);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
	
			@Override
			public void onRemove(String id, String replacedId) {
				try {
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("ID", "Remove:" + id + ":" + replacedId));
					sendMessage(str);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
	
			@Override
			public void onLoad() {
			}
	
		});
	
		model.getRegisterAccessor().addListener(new RegisterAccessorListener() {
			@Override
			public void onUpdate(Object regName, Object bitName, int value) {
				try {
					RegisterUpdates updates = new RegisterUpdates();
					updates.setName(regName.toString());
					updates.setBits(Arrays.asList(new BitUpdates(bitName.toString(), String.valueOf(value))));
					String val = new ObjectMapper().writeValueAsString(updates);
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("REGVAL", val));
					sendMessage(str);
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
					updates.setName(RegisterUpdates.INTERRUPT);
					String val = new ObjectMapper().writeValueAsString(updates);
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("REGVAL", val));
					sendMessage(str);
					sendMessageToDevice(device);
				} catch (JsonGenerationException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		});
		
		model.getTestRecorder().addListener(new TestRecorderListener() {
			@Override
			public void onTestFinished() {
				try {
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("TEST", "TestFinished"));
					sendMessage(str);
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
	
	
		model.getUiBuilder().addListener(new UiBuilderListener() {
			@Override
			public void onCssUpdate(String widgetId, String key, String value) {
				try {
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("UIDESIGN", "CSS:" + widgetId + "," + key + "," + value));
					sendMessage(str);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
	
			@Override
			public void onTypeUpdate(String widgetId, WidgetType type) {
				try {
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("UIDESIGN", "TYPE:" + widgetId + "," + type.toString()));
					sendMessage(str);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
	
			@Override
			public void onIdChange(String id, String subId) {
				try {
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("UIDESIGN", "ID:" + id + "," + id + "," + subId));
					sendMessage(str);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
	
			@Override
			public void onFieldChange(String id) {
				try {
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("UIDESIGN", "FIELD:" + id + "," + id));
					sendMessage(str);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
	
			@Override
			public void onLayoutChange(String id) {
				try {
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("UIDESIGN", "LAYOUT:" + id + "," + id));
					sendMessage(str);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		});		
	}
//
//	abstract protected void sendMessageAsync(String device, String message);
//	abstract protected void sendMessage(String str);
//	abstract protected void sendMessageToDevice(String device);
	
	private void sendMessageValueAsync(String device, String message) {
		WebSocketBroadcaster.getInstance().sendMessageAsync(userid, "VALUES@" + device, message);
	}

	private void sendMessagePromptAsync(String device, String html, ControlObject controls) {
		MessageToDevice msg = new MessageToDevice();
		msg.type = MessageToDevice.MESSAGE;
		msg.cls = MessageObject.class.getName();
		
		try {
			String messageId = String.valueOf(System.currentTimeMillis());
			msg.json = new ObjectMapper().writeValueAsString(new MessageObject(html, controls, messageId));
			WebSocketBroadcaster.getInstance().sendMessageToDomainModel(userid, device, new ObjectMapper().writeValueAsString(msg));
			WebSocketBroadcaster.getInstance().sendMessageAsync(userid, "MESSAGE@" + device, msg.json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
	private void sendMessageClose(String device, String messageId) {
		MessageToDevice msg = new MessageToDevice();
		msg.type = MessageToDevice.CLOSEMESSAGE;
		msg.cls = MessageObject.class.getName();
		
		try {
			msg.json = new ObjectMapper().writeValueAsString(new MessageObject(null, null, messageId));
			WebSocketBroadcaster.getInstance().sendMessageToDomainModel(userid, device, new ObjectMapper().writeValueAsString(msg));
			//WebSocketBroadcaster.getInstance().sendMessageAsync(userid, "@CLOSE@" + device, msg.json);
			WebSocketBroadcaster.getInstance().sendMessageAsync(userid, "MESSAGE@" + device, "@CLOSE@");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}		
	}
	
	private void sendMessage(String str) {
		WebSocketBroadcaster.getInstance().sendMessage(userid, str);
	}

	private void sendMessageToDevice(String device) {
		WebSocketBroadcaster.getInstance().sendMessageToDomainModel(userid, device, RegisterUpdates.INTERRUPT);
	}

}

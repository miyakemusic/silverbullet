package jp.silverbullet.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.dependency2.ChangedItemValue;
import jp.silverbullet.dependency2.DependencyListener;
import jp.silverbullet.dependency2.Id;
import jp.silverbullet.property2.PropertDefHolderListener;
import jp.silverbullet.register.RegisterMapListener;
import jp.silverbullet.register.RegisterUpdates;
import jp.silverbullet.test.TestRecorderListener;

public class WebClientManager {

	public WebClientManager() {

		StaticInstances.getInstance().getBuilderModel().getDependency().addDependencyListener(new DependencyListener() {
			@Override
			public boolean confirm(String history) {
				return true;
			}

			@Override
			public void onResult(Map<String, List<ChangedItemValue>> changedHistory) {
			}

			@Override
			public void onCompleted(String message) {
				try {
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("VALUES", message));
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
			public void onStart(Id id, String value) {
			}		
		});
	
		StaticInstances.getInstance().getBuilderModel().getPropertiesHolder2().addListener(new PropertDefHolderListener() {
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
			public void onRemove(String id) {
				try {
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("ID", "Remove:" + id));
					WebSocketBroadcaster.getInstance().sendMessage(str);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		});
		
		StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().addListener(new UiLayoutListener() {
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
			
		StaticInstances.getInstance().getBuilderModel().getRegisterMapModel().addListener(new RegisterMapListener() {
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

			@Override
			public void onUpdate(RegisterUpdates updates) {
				try {
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
			public void onUpdatedByHardware(RegisterUpdates updates) {
				// TODO Auto-generated method stub
				
			}
		});
		
		StaticInstances.getInstance().getBuilderModel().getTestRecorder().addListener(new TestRecorderListener() {
			@Override
			public void onTestFinished() {
				try {
					String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("TEST", "TestFinished"));
					WebSocketBroadcaster.getInstance().sendMessage(str);
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

			@Override
			public void onTestStart() {
			}
		});
	}
}

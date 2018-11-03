package jp.silverbullet.web;

import java.io.IOException;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.dependency.ChangedItemValue;
import jp.silverbullet.dependency.DependencyListener;
import jp.silverbullet.register.RegisterBit;
import jp.silverbullet.register.RegisterMapListener;
import jp.silverbullet.register.RegisterUpdates;
import jp.silverbullet.register.SvRegister;
import jp.silverbullet.test.TestRecorderListener;

public class WebClientManager {

	public WebClientManager() {
		StaticInstances.getInstance().getBuilderModel().getDependency().addDependencyListener(new DependencyListener() {
			@Override
			public boolean confirm(String history) {
				return true;
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
			public void onResult(Map<String, List<ChangedItemValue>> changedHistory) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStart(String id, String value) {
				// TODO Auto-generated method stub
				
			}
		});
		
		StaticInstances.getInstance().getRegisterMapModel().addListener(new RegisterMapListener() {
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

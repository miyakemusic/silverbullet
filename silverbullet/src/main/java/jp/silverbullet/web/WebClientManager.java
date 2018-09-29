package jp.silverbullet.web;

import java.io.IOException;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.dependency.DependencyListener;
import jp.silverbullet.javafx.BuilderFx;
import jp.silverbullet.register.BitSetToIntConverter;
import jp.silverbullet.register.RegisterBit;
import jp.silverbullet.register.RegisterMapListener;
import jp.silverbullet.register.RegisterUpdates;
import jp.silverbullet.register.SvRegister;
import jp.silverbullet.trash.unknown.ChangedItemValue;
import jp.silverbullet.register.BitUpdates;

public class WebClientManager {

	public WebClientManager() {
		StaticInstances.getBuilderModel().getDependency().addDependencyListener(new DependencyListener() {
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
		});
		
		StaticInstances.getRegisterMapModel().addListener(new RegisterMapListener() {
			@Override
			public void onInterrupt() {
			}

			@Override
			public void onDataUpdate(int regIndex, int blockNumber, int value, long address, BitSet bitSet, RegisterUpdates updates) {
				try {
					SvRegister register = BuilderFx.getModel().getBuilderModel().getRegisterProperty().getRegisterByAddress(address);
					String name = register.getName();
					for (RegisterBit bit : register.getBits().getBits()) {
						
					}
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
	}
}

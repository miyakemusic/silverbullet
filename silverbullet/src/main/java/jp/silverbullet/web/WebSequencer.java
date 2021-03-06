package jp.silverbullet.web;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.core.dependency2.ChangedItemValue;
import jp.silverbullet.core.dependency2.Id;
import jp.silverbullet.core.dependency2.RequestRejectedException;
import jp.silverbullet.core.sequncer.SvHandlerModel;
import jp.silverbullet.core.sequncer.UserSequencer;

public class WebSequencer implements UserSequencer{

	private String device;
	private String userid;

	public WebSequencer(String userid, String device) {
		this.userid = userid;
		this.device = device;
	}

	@Override
	public void handle(SvHandlerModel model, Map<String, List<ChangedItemValue>> changed, Id sourceId)
			throws RequestRejectedException {
		if (this.device.equals(sourceId.getSource())) {
			return;
		}
		try {
			String string = new ObjectMapper().writeValueAsString(
					new MessageToDevice(MessageToDevice.PROPERTYUPDATED, ChangesJson.class.getName(), new ObjectMapper().writeValueAsString(new ChangesJson(changed))));
			WebSocketBroadcaster.getInstance().sendMessageToDomainModel(this.userid, this.device, string);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	@Override
	public List<String> targetIds() {
		return null;
		//return Arrays.asList("ID_OTDR_TESTCONTROL");
	}

}

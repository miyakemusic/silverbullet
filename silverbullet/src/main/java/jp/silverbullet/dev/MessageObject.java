package jp.silverbullet.dev;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageObject {

	public String messageId;
	public String html;
	public ControlObject controls;

	public MessageObject() {}
	public MessageObject(String html, ControlObject controls, String messageId) {
		
		this.html = html;
		this.controls = controls;
		this.messageId = messageId;
	}
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

}

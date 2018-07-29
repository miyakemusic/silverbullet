package jp.silverbullet.web;

public class WebSocketMessage {
	private String type;
	private String value;
	
	public WebSocketMessage() {}
	
	public WebSocketMessage(String type2, String value2) {
		this.type = type2;
		this.value = value2;
	}
	
	public String getType() {
		return type;
	}
	public String getValue() {
		return value;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
}

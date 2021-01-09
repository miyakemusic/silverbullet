package jp.silverbullet.web;

public class MessageToDevice {
	
	public static final String FILEREADY = "FILEREADY";
	public static final String PROPERTYUPDATED = "PROPERTYUPDATED";
	public static final String MESSAGE = "MESSAGE";
	public static final String CLOSEMESSAGE = "CLOSEMESSAGE";
	
	public MessageToDevice() {}
	public MessageToDevice(String type, String cls, String json) {
		this.type = type;
		this.cls = cls;
		this.json = json;
	}
	
	public String cls;
	public String type;
	public String json;
}

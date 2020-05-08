package jp.silverbullet.web;

public class MessageToDevice {
	
	public static final String FILEREADY = "FileReady";
	public static final String PROPERTYUPDATED = "PropertyUpdated";
	
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

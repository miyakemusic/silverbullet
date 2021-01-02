package jp.silverbullet.dev;

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

}

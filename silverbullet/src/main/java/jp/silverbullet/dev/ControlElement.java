package jp.silverbullet.dev;

public class ControlElement {
	public ControlElement(Type type, String title, String id) {
		this.type = type;
		this.title = title;
		this.id = id;
	}
	
	public ControlElement(){}
	
	public enum Type {
		Button,
		TextInput,
		List,
		Boolean
	};
	
	public Type type;
	public String title;
	public String id;
	
}
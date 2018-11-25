package jp.silverbullet.dependency2;

public class GenericLink {
	private String from;
	private String to;
	private String type;
	
	public GenericLink(String from2, String to2, String type2) {
		this.from = from2;
		this.to = to2;
		this.type = type2;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public String getType() {
		return type;
	}

	
}

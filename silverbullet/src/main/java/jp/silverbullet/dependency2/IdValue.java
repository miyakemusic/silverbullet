package jp.silverbullet.dependency2;

public class IdValue {

	private Id id;
	private String value;

	public IdValue(String id, int index, String value) {
		this.id = new Id(id, index);
		this.value = value;
	}

	public IdValue(String id, String value) {
		this.id = new Id(id, 0);
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public Id getId() {
		return id;
	}
	
}

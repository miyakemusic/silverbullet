package jp.silverbullet.test;

public class TestItem {

	private String type;
	private String value;
	private String id;

	public TestItem() {}
	public TestItem(String type, String id, String value) {
		this.type = type;
		this.id = id;
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	public String getId() {
		return id;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return type + ":" + id + "=" + value;
	}

}

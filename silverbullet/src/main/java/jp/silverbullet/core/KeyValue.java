package jp.silverbullet.core;

public class KeyValue {
	private String key;
	private String value;
	private String value2;
	
	public KeyValue() {}
	public KeyValue(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public KeyValue(String key, String value, String value2) {
		this.key = key;
		this.value = value;
		this.value2 = value2;
	}
	
	public String getKey() {
		return key;
	}
	public String getValue() {
		return value;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getValue2() {
		return value2;
	}
	public void setValue2(String value2) {
		this.value2 = value2;
	}
	
	
}

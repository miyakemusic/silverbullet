package jp.silverbullet.test;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jp.silverbullet.dependency2.IdUtility;

public class TestItem {

	public static final String TYPE_PROPERTY = "PROPERTY";
	public static final String TYPE_REGISTER = "REGISTER";
	public static final String TYPE_CONTROL = "CONTROL";
	public static final String FILE = "file:";
	public static final String INTERRPT = "*INTERRPT*";
	public static final String TYPE_PROPERTY_TEST = "PROPERTY_TEST";
	public static final String TYPE_REGISTER_TEST = "REGISTER_TEST";
	public static final String WAIT = "WAIT";
	
	private long serial;
	private String target = "";
	private String value = "";
	private String type = "";
	private String expected = "";
	
	public TestItem() {}
	public TestItem(String type, String settingTarget, String settingValue, String expected) {
		this.target = settingTarget;
		this.value = settingValue;
		this.expected = expected;
		this.type = type;
	}
	public TestItem(String type, String settingTarget, String settingValue) {
		this.target = settingTarget;
		this.value = settingValue;
		this.type = type;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTarget() {
		return target;
	}
	public String getValue() {
		return value;
	}
	public void setTarget(String settingTarget) {
		this.target = settingTarget;
	}
	public void setValue(String settingValue) {
		this.value = settingValue;
	}
	public String getExpected() {
		return expected;
	}
	public void setExpected(String expected) {
		this.expected = expected;
	}
	
	@JsonIgnore
	public boolean isFile() {
		return this.value.startsWith(FILE);
	}
	
	public String blockFilename() {
		return this.value.split(":")[1];
	}
	
	@JsonIgnore
	public boolean isInterrupt() {
		return this.target.equals(INTERRPT);
	}

	public String bitValue() {
		return this.value;
	}
	public long getSerial() {
		return serial;
	}
	public void setSerial(long serial) {
		this.serial = serial;
	}
	@Override
	public String toString() {
		return this.type + "." + this.target + "." + this.expected + "." + this.value;
	}
	public void changeId(String prevId, String newId) {
		this.value = replaceOption(this.value, prevId, newId);//IdChanger.replaceId(this.value, prevId, newId);
		this.expected = replaceOption(this.expected, prevId, newId);
		this.target = replaceId(this.target, prevId, newId);
	}
	private String replaceId(String target2, String prevId, String newId) {
		if (target2.equals(prevId)) {
			return newId;
		}
		return target2;
	}
	private String replaceOption(String text, String prevId, String newId) {
		if (IdUtility.isValidOption(prevId, text)) {
			return text.replace(prevId, newId);
		}
		return text;
	}

}

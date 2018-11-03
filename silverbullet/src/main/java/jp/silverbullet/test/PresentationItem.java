package jp.silverbullet.test;

public class PresentationItem {

	private int number;
	private long serial;
	private String target;
	private String value;
	private String expected;
	private String result;
	private String passFail;
	private String time;
	private String type;
	
	
	public PresentationItem(long serial, int number,String type, String target, String value, String expected, String result, String passFail,
			String time) {
		super();
		this.serial = serial;
		this.number = number;
		this.type = type;
		this.target = target;
		this.value = value;
		this.expected = expected;
		this.result = result;
		this.passFail = passFail;
		this.time = time;
	}
	public int getNumber() {
		return number;
	}
	public String getTarget() {
		return target;
	}
	public String getValue() {
		return value;
	}
	public String getExpected() {
		return expected;
	}
	public String getResult() {
		return result;
	}
	public String getPassFail() {
		return passFail;
	}
	public String getTime() {
		return time;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void setExpected(String expected) {
		this.expected = expected;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public void setPassFail(String passFail) {
		this.passFail = passFail;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getSerial() {
		return serial;
	}
	public void setSerial(long serial) {
		this.serial = serial;
	}
	
	
}

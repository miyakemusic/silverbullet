package jp.silverbullet.web;

public class DeviceName {

	private String deviceName;
	private String serialNo;

	public DeviceName(String key) {
		String[] tmp = key.split("\\.");
		this.deviceName = tmp[0];
		this.serialNo = tmp[1];
	}

	public DeviceName() {
		// TODO Auto-generated constructor stub
	}

	public String generate(String deviceName, String serialNo) {
		return deviceName + "." + serialNo;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public String getSerialNo() {
		return serialNo;
	}

}

package jp.silverbullet.web;

public class DeviceProperty {

	private String deviceName;
	private String serialNo;
	private String applicationName;
	public DeviceProperty() {}
	public DeviceProperty(String deviceName, String serialNo, String applicationName) {
		this.deviceName = deviceName;
		this.serialNo = serialNo;
		this.applicationName = applicationName;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public String getApplicationName() {
		return applicationName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

}

package jp.silverbullet.web;

public class DeviceProperty {

	private String deviceName;
	private String applicationName;
	public DeviceProperty() {}
	public DeviceProperty(String deviceName, String applicationName) {
		this.deviceName = deviceName;
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

}

package jp.silverbullet.testspec;

public class PortStatus {

	public PortStatus() {}
	public PortStatus(String id, PortStateEnum state) {
		this.portId = id;
		this.portState = state;
	}
	public String portId;
	public PortStateEnum portState;
}

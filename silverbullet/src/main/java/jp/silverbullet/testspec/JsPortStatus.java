package jp.silverbullet.testspec;

public class JsPortStatus {

	public JsPortStatus() {}
	public JsPortStatus(String id, PortStateEnum state, boolean onGoing2) {
		this.portId = id;
		this.portState = state;
		this.onGoing = onGoing2;
	}
	public String portId;
	public PortStateEnum portState;
	public boolean onGoing = true;
}

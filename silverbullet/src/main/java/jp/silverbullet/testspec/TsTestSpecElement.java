package jp.silverbullet.testspec;

public class TsTestSpecElement {
	public TsTestSpecElement() {}
	public TsTestSpecElement(String nodeId, String nodeName, String portId, String portDirection, String testSide, String portName, String testMethod) {
		this.nodeId = nodeId;
		this.nodeName = nodeName;
		this.portId = portId;
		this.portDirection = portDirection;
		this.testSide = testSide;
		this.portName = portName;
		this.testMethod = testMethod;		
	}
	
	public String nodeId;
	public String nodeName;
	public String portDirection;
	public String testSide;
	public String portName;
	public String testMethod;
	public String portId;
}
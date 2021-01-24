package jp.silverbullet.testspec;

public class TsTestSpecElement {
	public TsTestSpecElement() {}
	public TsTestSpecElement(String nodeName, String portDirection, String testSide, String portName, String testMethod) {
		this.nodeName = nodeName;
		this.portDirection = portDirection;
		this.testSide = testSide;
		this.portName = portName;
		this.testMethod = testMethod;		
	}
	public String nodeName;
	public String portDirection;
	public String testSide;
	public String portName;
	public String testMethod;
}
package jp.silverbullet.test;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TableData {
	private String test = "";
	private String result = "";
	private String dependency = "";
	private String register = "";
	private String passFail = "";
	public String getTest() {
		return test;
	}
	public void setTest(String test) {
		this.test = test;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getPassFail() {
		return passFail;
	}
	public void setPassFail(String passFail) {
		this.passFail = passFail;
	}
	public String getDependency() {
		return dependency;
	}
	public void setDependency(String dependency) {
		this.dependency = dependency;
	}
	public String getRegister() {
		return register;
	}
	public void setRegister(String register) {
		this.register = register;
	}
	public void resetResult() {
		this.register = "";
		this.result = "";
		this.dependency = "";
	}
	
}

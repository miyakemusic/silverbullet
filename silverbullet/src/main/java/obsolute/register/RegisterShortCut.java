package obsolute.register;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RegisterShortCut {

	private String regName;
	private String bitName;
	private boolean interrupt;
	public RegisterShortCut() {}
	public RegisterShortCut(String regName, String bitName) {
		this.regName = regName;
		this.bitName = bitName;
		this.interrupt = true;
	}
	public String getRegName() {
		return regName;
	}
	public String getBitName() {
		return bitName;
	}
	public boolean isInterrupt() {
		return interrupt;
	}
	public void setRegName(String regName) {
		this.regName = regName;
	}
	public void setBitName(String bitName) {
		this.bitName = bitName;
	}
	public void setInterrupt(boolean intrrupt) {
		this.interrupt = intrrupt;
	}

}

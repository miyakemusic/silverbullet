package jp.silverbullet.register2;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class RegisterShortCutHolder {
	private List<RegisterShortCut> shortcuts = new ArrayList<>();
	
	public void add(String regName, String bitName) {
		this.shortcuts.add(new RegisterShortCut(regName, bitName));
	}

	public void setShortcuts(List<RegisterShortCut> shortcuts) {
		this.shortcuts = shortcuts;
	}

	public List<RegisterShortCut> getShortcuts() {
		return shortcuts;
	}

	public void updateCheck(String regName, String bitName, Boolean value) {
		for (RegisterShortCut reg : this.shortcuts) {
			if (reg.getRegName().equals(regName) && reg.getBitName().equals(bitName)) {
				reg.setInterrupt(value);
				break;
			}
		}
	}

	public boolean isInterruptEnabled(String regName, String bitName) {
		for (RegisterShortCut reg : this.shortcuts) {
			if (reg.getRegName().equals(regName) && reg.getBitName().equals(bitName)) {
				return reg.isInterrupt();
			}
		}
		return false;
	}

}

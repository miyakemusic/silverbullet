package jp.silverbullet.register;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class RegisterProperty {
	private List<SvRegister> registers = new ArrayList<SvRegister>();
	
	public RegisterProperty(){}
	
	public SvRegister addRegister(String name, String address, String description) {
		SvRegister register = new SvRegister(name, description, address);
		this.registers.add(register);
		return register;
	}

	public List<SvRegister> getRegisters() {
		return registers;
	}

	public void setRegisters(List<SvRegister> registers) {
		this.registers = registers;
	}

	public void remove(SvRegister svRegister) {
		this.registers.remove(svRegister);
	}
	
	
}

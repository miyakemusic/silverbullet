package jp.silverbullet.register;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class RegisterProperty {
	private List<SvRegister> registers = new ArrayList<SvRegister>();
	private int registerWidth = 32;
	
	public RegisterProperty(){}
	
	public SvRegister addRegister(String name, String address, String description) {
		SvRegister register = new SvRegister(name, description, address);
		this.registers.add(register);
		sort();
		return register;
	}
	
	public void sort() {
		Collections.sort(this.registers, new AddressComparator());
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

	public void remove(int row) {
		this.registers.remove(row);
	}
	
	public void addAll(List<SvRegister> registers2) {
		for (SvRegister reg : registers2) {
			this.registers.add(reg);
		}
	}

	public SvRegister getRegisterByName(String regName) {
		for (SvRegister register : this.registers) {
			if (register.getName().equals(regName)) {
				return register;
			}
		}
		return null;
	}

	public SvRegister getRegisterByAddress(long address) {
		for (SvRegister register : this.registers) {
			if (register.getDecAddress() == address) {
				return register;
			}
		}
		return null;
	}

	public int getLastDecAddess() {
		if (this.registers.size() == 0) {
			return 0;
		}
		return this.registers.get(this.registers.size()-1).getDecAddress();
	}

	public int getRegisterWidth() {
		return this.registerWidth ;
	}

}

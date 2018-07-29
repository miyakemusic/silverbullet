package jp.silverbullet.register.json;

import java.util.ArrayList;
import java.util.List;

import jp.silverbullet.register.SvRegister;

public class SvRegisterJsonHolder {
	private List<SvRegisterJson> registers = new ArrayList<>();

	public void addRegister(SvRegisterJson register) {
		this.registers.add(register);
	}

	public List<SvRegisterJson> getRegisters() {
		return registers;
	}

	public void setRegisters(List<SvRegisterJson> registers) {
		this.registers = registers;
	}
	
}

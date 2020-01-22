package jp.silverbullet.web.register.json;

import java.util.ArrayList;
import java.util.List;

import jp.silverbullet.core.register2.RegisterSpecHolder;
import jp.silverbullet.core.register2.SvRegister;

public class SvRegisterJsonHolder {
	private List<SvRegisterJson> registers = new ArrayList<>();
	public int regSize = 0;
	
	public SvRegisterJsonHolder(RegisterSpecHolder holder) {
		for (Long address : holder.getRegisters().keySet()) {
			this.addRegister(this.convertRegister(holder.getRegisters().get(address)));			
		}
		this.regSize = holder.getRegSize();
	}

	public SvRegisterJsonHolder() {
		// TODO Auto-generated constructor stub
	}

	public void addRegister(SvRegisterJson register) {
		this.registers.add(register);
	}

	public List<SvRegisterJson> getRegisters() {
		return registers;
	}

	public void setRegisters(List<SvRegisterJson> registers) {
		this.registers = registers;
	}
	
	private SvRegisterJson convertRegister(SvRegister register) {
		SvRegisterJson ret = new SvRegisterJson();
		ret.setAddress(register.getAddress());
		ret.setDescription(register.getDescription());
		ret.setName(register.getName());
		ret.addAll(register.getBits().getBits());
		return ret;
	}
}

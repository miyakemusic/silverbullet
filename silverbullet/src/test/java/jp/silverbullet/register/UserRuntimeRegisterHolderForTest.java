package jp.silverbullet.register;

import jp.silverbullet.core.register2.RegisterAccessor;
import jp.silverbullet.core.register2.RuntimeRegister;
import jp.silverbullet.core.register2.RuntimeRegisterHolder;

public class UserRuntimeRegisterHolderForTest extends RuntimeRegisterHolder {
	
	public UserRuntimeRegisterHolderForTest(RegisterAccessor registerAccessor) {
		super(registerAccessor);
	}

	public enum Register {
		Reg1, Reg2, Reg3
	}
	public enum Reg1 {
		bit1, bit2
	}

	public enum Reg2 {
		bit3, bit4
	}

	public RuntimeRegister<Reg1> reg1 = new RuntimeRegister<>("Reg1", accessor);
	public RuntimeRegister<Reg2> reg2 = new RuntimeRegister<>("Reg2", accessor);
}

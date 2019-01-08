package jp.silverbullet.register;
import jp.silverbullet.register2.RegisterAccessor;
import jp.silverbullet.register2.RuntimeRegister;
import jp.silverbullet.register2.RuntimeRegisterHolder;
public class RegisterSource extends RuntimeRegisterHolder {
	public RegisterSource(RegisterAccessor registerAccessor) {
		super(registerAccessor);
	}
	public enum Register {
		REG1, 
		REG2, 
	}
	public enum REG1 {
		BIT2, 
		BIT1, 
	}
	public RuntimeRegister<REG1> reg1 = new RuntimeRegister<>(Register.REG1, accessor);
	public enum REG2 {
		BIT_4, 
		BIT_3, 
	}
	public RuntimeRegister<REG2> reg2 = new RuntimeRegister<>(Register.REG2, accessor);
}

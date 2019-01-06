package jp.silverbullet.register;
import jp.silverbullet.register2.RegisterAccessor;
import jp.silverbullet.register2.RuntimeRegister;
import jp.silverbullet.register2.RuntimeRegisterHolder;
public class RegisterSource extends RuntimeRegisterHolder {
	public RegisterSource(RegisterAccessor registerAccessor) {
		super(registerAccessor);
	}
	public enum Register {
		Reg1, 
		Reg2, 
	}
	public enum Reg1 {
		Bit2, 
		Bit1, 
	}
	public RuntimeRegister<Reg1> reg1 = new RuntimeRegister<>(Register.Reg1, accessor);
	public enum Reg2 {
		Bit_4, 
		Bit_3, 
	}
	public RuntimeRegister<Reg2> reg2 = new RuntimeRegister<>(Register.Reg2, accessor);
}

package jp.silverbullet.core.register2;

public interface SvRegisterInterface {

	int getRegSize();

	boolean conflictsName(String name, SvRegister svRegister);

}

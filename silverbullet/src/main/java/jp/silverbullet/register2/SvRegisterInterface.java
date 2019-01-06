package jp.silverbullet.register2;

public interface SvRegisterInterface {

	int getRegisterWidth();

	boolean conflictsName(String name, SvRegister svRegister);

}

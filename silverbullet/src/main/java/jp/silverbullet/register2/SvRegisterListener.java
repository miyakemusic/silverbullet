package jp.silverbullet.register2;

import jp.silverbullet.register.SvRegister;

public interface SvRegisterListener {
	void onAddressChange(SvRegister svRegister, String address, String prev);

}

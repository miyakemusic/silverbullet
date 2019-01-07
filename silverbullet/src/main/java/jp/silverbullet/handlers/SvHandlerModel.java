package jp.silverbullet.handlers;

import jp.silverbullet.register2.RegisterAccessor;

public interface SvHandlerModel {

	String getUserApplicationPath();

	EasyAccessInterface getEasyAccessInterface();
	RegisterAccessor getRegisterAccessor();
}

package jp.silverbullet.sequncer;

import jp.silverbullet.register2.RegisterAccessor;

public interface SvHandlerModel {

//	String getUserApplicationPath();

	EasyAccessInterface getEasyAccessInterface();
	RegisterAccessor getRegisterAccessor();
	SystemAccessor getSystem();
}

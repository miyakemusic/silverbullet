package jp.silverbullet.sequncer;

import jp.silverbullet.register2.RegisterAccessor;

public interface SvHandlerModel {
	EasyAccessInterface getEasyAccessInterface();
	RegisterAccessor getRegisterAccessor();
	SystemAccessor getSystem();
}

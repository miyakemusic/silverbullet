package jp.silverbullet.core.sequncer;

import jp.silverbullet.core.register2.RegisterAccessor;

public interface SvHandlerModel {
	EasyAccessInterface getEasyAccessInterface();
	RegisterAccessor getRegisterAccessor();
	SystemAccessor getSystem();
}

package jp.silverbullet.handlers;

import jp.silverbullet.dependency2.RequestRejectedException;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.register2.RegisterAccessor;

public interface SvHandlerModel {
//
//	RuntimeProperty getProperty(String id);
//
//	void requestChange(String id, String value) throws RequestRejectedException;
//	void requestChange(String id, int index, String value) throws RequestRejectedException;
	String getUserApplicationPath();

	EasyAccessInterface getEasyAccessInterface();
	RegisterAccessor getRegisterAccessor();
}

package jp.silverbullet.handlers;

import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.RequestRejectedException;

public interface SvHandlerModel {

	SvProperty getProperty(String id);

	void requestChange(String id, String value) throws RequestRejectedException;

	String getUserApplicationPath();

	EasyAccessModel getEasyAccessModel();

	RegisterAccess getRegisterAccess();
}

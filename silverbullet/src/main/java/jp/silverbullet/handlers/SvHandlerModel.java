package jp.silverbullet.handlers;

import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.RequestRejectedException;

public interface SvHandlerModel extends EasyAccessInterface {

	@Override
	SvProperty getProperty(String id);

	@Override
	void requestChange(String id, String value) throws RequestRejectedException;

	String getUserApplicationPath();

	RegisterAccess getRegisterAccess();
}

package jp.silverbullet.handlers;

import jp.silverbullet.dependency2.RequestRejectedException;
import jp.silverbullet.property.SvProperty;

public interface SvHandlerModel extends EasyAccessInterface {

	@Override
	SvProperty getProperty(String id);

	@Override
	void requestChange(String id, String value) throws RequestRejectedException;

	String getUserApplicationPath();

	RegisterAccess getRegisterAccess();
}

package jp.silverbullet.handlers;

import jp.silverbullet.dependency2.RequestRejectedException;
import jp.silverbullet.property2.RuntimeProperty;

public interface SvHandlerModel extends EasyAccessInterface {

	@Override
	RuntimeProperty getProperty(String id);

	@Override
	void requestChange(String id, String value) throws RequestRejectedException;

	String getUserApplicationPath();

	RegisterAccess getRegisterAccess();
}

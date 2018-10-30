package jp.silverbullet.handlers;

import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.RequestRejectedException;

public interface EasyAccessInterface {

	SvProperty getProperty(String id);

	void requestChange(String id, String value) throws RequestRejectedException;

}

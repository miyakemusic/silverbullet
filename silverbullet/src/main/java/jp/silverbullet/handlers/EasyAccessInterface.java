package jp.silverbullet.handlers;

import jp.silverbullet.dependency2.RequestRejectedException;
import jp.silverbullet.property.SvProperty;

public interface EasyAccessInterface {

	SvProperty getProperty(String id);

	void requestChange(String id, String value) throws RequestRejectedException;
	void requestChange(String id, int index, String value) throws RequestRejectedException;
}

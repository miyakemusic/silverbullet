package jp.silverbullet.handlers;

import jp.silverbullet.dependency2.RequestRejectedException;
import jp.silverbullet.property.SvProperty;
import jp.silverbullet.property2.RuntimeProperty;

public interface EasyAccessInterface {

	RuntimeProperty getProperty(String id);

	void requestChange(String id, String value) throws RequestRejectedException;
	void requestChange(String id, int index, String value) throws RequestRejectedException;
}

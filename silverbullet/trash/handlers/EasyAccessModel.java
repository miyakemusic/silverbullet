package jp.silverbullet.handlers;

import jp.silverbullet.dependency2.RequestRejectedException;
import jp.silverbullet.property2.RuntimeProperty;

public interface EasyAccessModel {
	void requestChange(String id, int index, String value) throws RequestRejectedException;	
	void requestChange(String id, String value);
	RuntimeProperty getProperty(String id);
}

package jp.silverbullet.core.sequncer;

import jp.silverbullet.core.dependency2.RequestRejectedException;

public interface EasyAccessInterface {	
	void requestChange(String id, String value) throws RequestRejectedException;
	void requestChange(String id, int index, String value) throws RequestRejectedException;
	void requestChange(String id, Object blobData, String name) throws RequestRejectedException;
	
	String getCurrentValue(String id);
	String getSelectedListTitle(String id);
	
}

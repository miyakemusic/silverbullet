package jp.silverbullet.sequncer;

import jp.silverbullet.dependency2.RequestRejectedException;
import jp.silverbullet.property2.ChartContent;
import jp.silverbullet.property2.RuntimeProperty;

public interface EasyAccessInterface {

	void requestChange(String id, String value) throws RequestRejectedException;
	void requestChange(String id, int index, String value) throws RequestRejectedException;
	void requestChange(String id, Object blobData, String name) throws RequestRejectedException;
	
	RuntimeProperty getProperty(String id);
	
}

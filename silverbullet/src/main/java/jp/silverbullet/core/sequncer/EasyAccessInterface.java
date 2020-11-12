package jp.silverbullet.core.sequncer;

import jp.silverbullet.core.dependency2.Id;
import jp.silverbullet.core.dependency2.RequestRejectedException;

abstract public class EasyAccessInterface {	
	abstract public void requestChange(Id id, String value) throws RequestRejectedException;
	abstract public void requestChange(Id id, Object blobData, String name) throws RequestRejectedException;
	
	public void requestChange(String id, String value) throws RequestRejectedException {
		requestChange(new Id(id), value);
	}
	public void requestChange(String id, Object blobData, String name) throws RequestRejectedException {
		requestChange(new Id(id), blobData, name);
	}
	
	abstract public String getCurrentValue(String id);
	abstract public String getSelectedListTitle(String id);
	
}

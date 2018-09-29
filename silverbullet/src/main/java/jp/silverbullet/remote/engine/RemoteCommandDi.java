package jp.silverbullet.remote.engine;

import java.util.LinkedList;

import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.RequestRejectedException;
import jp.silverbullet.remote.SvTexHolder;

public interface RemoteCommandDi {
	LinkedList<RemoteError> getErrors();

	void waitComplete();

	void clearSyncCondition();

	SvTexHolder getTexHolder();

	void addError(int i, String string);

	SvProperty getProperty(String id);

	void setAsyncCondition(String asyncCompleteCondition);

	void requestChange(String id, String value) throws RequestRejectedException;

	SyncController getSyncController();

}

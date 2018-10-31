package jp.silverbullet.test;

import jp.silverbullet.dependency.RequestRejectedException;

public interface TestRecorderInterface {

	void saveParameters(String string);

	void requestChange(String id, String value) throws RequestRejectedException;

	void setRegisterValue(String id, String value);

}

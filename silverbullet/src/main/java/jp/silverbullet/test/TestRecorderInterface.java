package jp.silverbullet.test;

import java.util.List;

import jp.silverbullet.dependency2.RequestRejectedException;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.register2.RegisterAccessor;
import jp.silverbullet.register2.RegisterController;

public interface TestRecorderInterface {

	void saveParameters(String string);

	void requestChange(String id, String value) throws RequestRejectedException;

	List<RuntimeProperty> getProperties();

	RuntimeProperty getProperty(String target);

	long getRegisterValue(String regName, String bitName);

	RegisterController getRegisterController();

}

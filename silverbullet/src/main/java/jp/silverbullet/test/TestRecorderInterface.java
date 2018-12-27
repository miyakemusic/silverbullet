package jp.silverbullet.test;

import java.util.List;

import jp.silverbullet.dependency2.RequestRejectedException;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.register.SvSimulator;

public interface TestRecorderInterface {

	void saveParameters(String string);

	void requestChange(String id, String value) throws RequestRejectedException;

	SvSimulator createSimulator();

	long getAddress(String id);

	List<RuntimeProperty> getProperties();

	RuntimeProperty getProperty(String target);


}

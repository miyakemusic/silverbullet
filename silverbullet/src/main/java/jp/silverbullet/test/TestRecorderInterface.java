package jp.silverbullet.test;

import java.util.List;

import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.RequestRejectedException;
import jp.silverbullet.register.SvSimulator;

public interface TestRecorderInterface {

	void saveParameters(String string);

	void requestChange(String id, String value) throws RequestRejectedException;

	SvSimulator createSimulator();

	long getAddress(String id);

	List<SvProperty> getProperties();

	SvProperty getProperty(String target);


}

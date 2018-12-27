package jp.silverbullet.handlers;

import jp.silverbullet.property2.RuntimeProperty;

public interface EasyAccessModel {

	void requestChange(String id, String value);

	RuntimeProperty getProperty(String id);

}

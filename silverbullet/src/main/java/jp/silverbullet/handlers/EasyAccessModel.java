package jp.silverbullet.handlers;

import jp.silverbullet.property.SvProperty;

public interface EasyAccessModel {

	void requestChange(String id, String value);

	SvProperty getProperty(String id);

}

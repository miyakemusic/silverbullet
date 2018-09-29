package jp.silverbullet.dependency;

import jp.silverbullet.SvProperty;

public interface DepPropertyStore {

	SvProperty getProperty(String id);

	void add(SvProperty createListProperty);

}

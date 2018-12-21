package jp.silverbullet.dependency2;

import jp.silverbullet.property.SvProperty;

public interface DepPropertyStore {

	SvProperty getProperty(String id);

	void add(SvProperty createListProperty);

}

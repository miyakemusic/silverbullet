package jp.silverbullet.dependency.speceditor3;

import jp.silverbullet.SvProperty;

public interface DepPropertyStore {

	SvProperty getProperty(String id);

	void add(SvProperty createListProperty);

}

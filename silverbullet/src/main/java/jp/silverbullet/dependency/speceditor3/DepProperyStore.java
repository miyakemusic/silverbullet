package jp.silverbullet.dependency.speceditor3;

import jp.silverbullet.SvProperty;

public interface DepProperyStore {

	SvProperty getProperty(String id);

	void add(SvProperty createListProperty);

}

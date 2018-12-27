package jp.silverbullet.dependency2;

import jp.silverbullet.property2.RuntimeProperty;

public interface DepPropertyStore {

	RuntimeProperty getProperty(String id);

	void add(RuntimeProperty property);

}

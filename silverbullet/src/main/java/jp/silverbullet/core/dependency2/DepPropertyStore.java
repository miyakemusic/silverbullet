package jp.silverbullet.core.dependency2;

import jp.silverbullet.core.property2.RuntimeProperty;

public interface DepPropertyStore {

	RuntimeProperty getProperty(String id);

	void add(RuntimeProperty property);

}

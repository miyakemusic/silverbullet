package jp.silverbullet.core;

import jp.silverbullet.core.property2.RuntimeProperty;

public interface PropertyGetter {

	RuntimeProperty getProperty(String id);
	RuntimeProperty getProperty(String id, int index);
}

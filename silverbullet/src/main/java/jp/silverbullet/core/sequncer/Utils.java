package jp.silverbullet.core.sequncer;

import java.util.List;
import java.util.Map;

import jp.silverbullet.core.dependency2.ChangedItemValue;
import jp.silverbullet.core.dependency2.DependencySpec;
import jp.silverbullet.core.property2.RuntimeProperty;

public class Utils {
	public static synchronized boolean compareValue(Map<String, List<ChangedItemValue>> changed, String id,
			String value) {
		for (String id2 : changed.keySet()) {
			if (!RuntimeProperty.convertSimpleId(id2).equals(id)) {
				continue;
			}
			List<ChangedItemValue> changes = changed.get(id2);
			for (ChangedItemValue v : changes) {
				if (v.getElement().equals(DependencySpec.Value)) {
					if (v.getValue().equals(value)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}

package jp.silverbullet.dependency2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.silverbullet.core.PropertyGetter;
import jp.silverbullet.core.property2.RuntimeProperty;
import jp.silverbullet.dependency.speceditor3.RuntimePropertyFactory;

public class PropertyStoreForTest implements PropertyGetter {
	private RuntimePropertyFactory factory = new RuntimePropertyFactory();
	private Map<String, RuntimeProperty> props = new HashMap<>();

	@Override
	public RuntimeProperty getProperty(String id) {
		if (!id.contains(RuntimeProperty.INDEXSIGN)) {
			id = RuntimeProperty.createIdText(id, 0);
		}
		return props.get(id);
	}

	public void add(RuntimeProperty property) {
		props.put(RuntimeProperty.createIdText(property.getId(), property.getIndex()), property);
	}

	public void addListProperty(String id, List<String> asList, String defaultId) {
		add(this.factory.getListProperty(id, asList, defaultId));
	}

	public void addDoubleProperty(String id, double defaultValue, String unit, double min, double max, int decimal) {
		add(this.factory.getDoubleProperty(id, defaultValue, unit, min, max, decimal));
	}

	@Override
	public RuntimeProperty getProperty(String id, int index) {
		return props.get(RuntimeProperty.createIdText(id, index));
	}

	public void addBooleanProperty(String id, boolean b) {
		add(this.factory.getBooleanProperty(id, b));
	}

}

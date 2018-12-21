package jp.silverbullet.dependency2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.silverbullet.dependency.speceditor3.SvPropertyFactory;
import jp.silverbullet.property.SvProperty;

public class PropertyStoreForTest implements DepPropertyStore {
	private SvPropertyFactory factory = new SvPropertyFactory();
	private Map<String, SvProperty> props = new HashMap<>();

	@Override
	public SvProperty getProperty(String id) {
		if (!id.contains("@")) {
			id += "@0";
		}
		return props.get(id);
	}

	@Override
	public void add(SvProperty property) {
		props.put(property.getId() + "@" + property.getIndex(), property);
	}

	public void addListProperty(String id, List<String> asList, String defaultId) {
		add(this.factory.getListProperty(id, asList, defaultId));
	}

	public void addDoubleProperty(String id, double defaultValue, String unit, double min, double max, int decimal) {
		add(this.factory.getDoubleProperty(id, defaultValue, unit, min, max, decimal));
	}

}

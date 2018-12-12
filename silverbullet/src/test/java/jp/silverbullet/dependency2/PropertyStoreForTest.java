package jp.silverbullet.dependency2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.DepPropertyStore;
import jp.silverbullet.dependency.speceditor3.SvPropertyFactory;

public class PropertyStoreForTest implements DepPropertyStore {
	private SvPropertyFactory factory = new SvPropertyFactory();
	private Map<String, SvProperty> props = new HashMap<>();

	@Override
	public SvProperty getProperty(String id) {
		return props.get(id);
	}

	@Override
	public void add(SvProperty property) {
		props.put(property.getId(), property);
	}

	public void addListProperty(String id, List<String> asList, String defaultId) {
		add(this.factory.getListProperty(id, asList, defaultId));
	}

}

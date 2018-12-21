package jp.silverbullet.property2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.silverbullet.JsonPersistent;

public class PropertyHolder2 {

	private Properties properties = new Properties();
	
	private PropertyDefListener2 listener = new PropertyDefListener2() {

		@Override
		public void onIdChanged(String newId, String oldId) {
			PropertyDef2 prop = properties.get(oldId);
			properties.remove(oldId);
			addProperty(prop);
		}
	};
	
	public void addProperty(PropertyDef2 propertyDef) {
		propertyDef.addListener(listener);
		this.properties.put(propertyDef.getId(), propertyDef);
	}

	public Collection<PropertyDef2> getProperties() {
		return this.properties.values();
//		List<PropertyDef2> ret = new ArrayList<>();
//		for (PropertyDef2 prop :  this.properties.values()) {
//			ret.add(prop);
//		}
//		return ret;
	}

	public PropertyDef2 get(String id) {
		return this.properties.get(id);
	}

	public void delete(String id) {
		PropertyDef2 prop = this.properties.get(id);
		prop.removeListener(listener);
		this.properties.remove(id);
	}

	public void createClone(String id, String newId) {
		PropertyDef2 prop = this.properties.get(id).clone();
		prop.setId(newId);
		this.addProperty(prop);
	}

	public Set<String> getAllIds(PropertyType2 valueOf) {
		return this.properties.keySet();
	}

	public List<String> getTypes() {
		List<String> ret = new ArrayList<>();
		for (PropertyType2 type : PropertyType2.values()) {
			ret.add(type.toString());
		}
		return ret;
	}

	public void load(String filename) {
		this.properties = new JsonPersistent().loadJson(Properties.class, filename);
	}
	
	public void save(String filename) {
		new JsonPersistent().saveJson(this.properties, filename);
	}
}

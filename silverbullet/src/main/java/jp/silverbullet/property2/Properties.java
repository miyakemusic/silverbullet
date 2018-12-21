package jp.silverbullet.property2;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Properties {
	private Map<String, PropertyDef2> properties = new LinkedHashMap<>();

	public PropertyDef2 get(String id) {
		return properties.get(id);
	}

	public void remove(String id) {
		this.properties.remove(id);
	}

	public void put(String id, PropertyDef2 value) {
		this.properties.put(id, value);
	}

	public Collection<PropertyDef2> values() {
		return this.properties.values();
	}

	public Set<String> keySet() {
		return this.properties.keySet();
	}

	public Map<String, PropertyDef2> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, PropertyDef2> properties) {
		this.properties = properties;
	}
	
}

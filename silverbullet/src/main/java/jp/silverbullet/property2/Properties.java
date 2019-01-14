package jp.silverbullet.property2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Properties {
	private Map<String, PropertyDef2> properties = new LinkedHashMap<>();

	public PropertyDef2 get(String id) {
		return properties.get(id);
	}

	public String remove(String id) {
		int index = new ArrayList<>(this.properties.keySet()).indexOf(id);
		this.properties.get(id).clearListeners();
		this.properties.remove(id);
		if (index > properties.size()-1) {
			index--;
		}
		if (this.properties.size() == 0) {
			return "";
		}
		else {
			return new ArrayList<>(this.properties.keySet()).get(index);
		}
		
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

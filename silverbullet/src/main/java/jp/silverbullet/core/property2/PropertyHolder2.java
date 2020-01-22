package jp.silverbullet.core.property2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.silverbullet.core.JsonPersistent;

public class PropertyHolder2 {

	private Properties properties = new Properties();
	
	private PropertyDefListener2 listener = new PropertyDefListener2() {

		@Override
		public void onIdChanged(String newId, String oldId) {
			changeId(newId, oldId);
			
//			PropertyDef2 prop = properties.get(oldId);
//			remove(oldId);
//			addProperty(prop);
		}

		@Override
		public void onOptionAdded(String id, String optionId, String title2, String comment2) {
			fireOptionChange(id);
		}

		@Override
		public void onParamChange(String id, Object value, Object prev, String fieldName) {
			fireParameterChange(id, fieldName, value, prev);
		}

		@Override
		public void onTypeChange(String id, PropertyType2 value) {
			fireParameterChange(id, PropertyDef2.TYPE, value, null);
		}

		@Override
		public void onOptionRemove(String id, String optionId) {
			fireOptionChange(id);
		}
	};

	private Set<PropertyDefHolderListener> listeners = new HashSet<>();
	
	private void changeId(String newId, String oldId) {
		properties.changeId(newId, oldId);
		this.fireIdChanged(newId, oldId);
	}
	
	private void fireIdChanged(String newId, String oldId) {
		this.listeners.forEach(listener -> listener.onChange(newId, PropertyDef2.ID, newId, oldId));
	}

	public void addProperty(PropertyDef2 propertyDef) {
		propertyDef.addListener(listener);
		this.properties.put(propertyDef.getId(), propertyDef);
		firePropertyAdd(propertyDef.getId());
	}

	private void firePropertyAdd(String id) {
		this.listeners.forEach(listener -> listener.onAdd(id));
	}

	protected void fireOptionChange(String id) {
		this.listeners.forEach(listener -> listener.onChange(id, "option", null, null));
	}

	protected void fireParameterChange(String id, String fieldName, Object value, Object prev) {
		this.listeners.forEach(listener -> listener.onChange(id, fieldName, value, prev));
	}

	public Collection<PropertyDef2> getProperties() {
		return this.properties.values();
	}

	public PropertyDef2 get(String id) {
		return this.properties.get(id);
	}

	public void remove(String id) {
		String replacedId = this.properties.remove(id);
		fireOnRemove(id, replacedId);
	}

	private void fireOnRemove(String id, String replacedId) {
		this.listeners.forEach(listener -> listener.onRemove(id, replacedId));
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
		try {
			this.properties = new JsonPersistent().loadJson(Properties.class, filename);
			this.properties.getProperties().forEach((key, value) -> value.addListener(listener));
			this.listeners.forEach(listener -> listener.onLoad());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void save(String filename) {
		try {
			new JsonPersistent().saveJson(this.properties, filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addListener(PropertyDefHolderListener propertDefHolderListener) {
		this.listeners .add(propertDefHolderListener);
	}
	
	public void removeListener(PropertyDefHolderListener propertDefHolderListener) {
		this.listeners .remove(propertDefHolderListener);
	}

	public void addOption(String id, String optionId) throws Exception {
		this.get(id).option(optionId, "", "");
	}

}

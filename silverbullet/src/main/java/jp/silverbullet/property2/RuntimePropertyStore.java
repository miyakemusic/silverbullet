package jp.silverbullet.property2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jp.silverbullet.JsonPersistent;
import jp.silverbullet.dependency2.IdValue;

public class RuntimePropertyStore {

	
	private RuntimeProperties runtimeProperties = new RuntimeProperties();
	private PropertyHolder2 holder;
	
	public RuntimePropertyStore(PropertyHolder2 propertyHolder) {
		this.holder = propertyHolder;
		propertyHolder.getProperties().forEach(property -> {
			addProperty(property);
		});
		
		propertyHolder.addListener(new PropertDefHolderListener() {
			@Override
			public void onChange(String id, String field, Object value, Object prevValue) {
				if (field.equals(PropertyDef2.ARRAY_SIZE)) {
					updateSize(id, 
							Double.valueOf(value.toString()).intValue(), 
							Double.valueOf(prevValue.toString()).intValue());
				}
			}

			@Override
			public void onAdd(String id) {
				addProperty(propertyHolder.get(id));
			}

			@Override
			public void onRemove(String id) {
				for (Iterator<String> i = runtimeProperties.keySet().iterator(); i.hasNext();) {
					  if (i.next().split(RuntimeProperty.INDEXSIGN)[0].equals(id)) {
					    i.remove();
					  }
				}
			}
		});
	}

	protected void updateSize(String id, Integer size, Integer prevSize) {
		PropertyDef2 def = this.holder.get(id);
		if (prevSize > size) {
			for (int i = size; i < prevSize; i++) {
				this.runtimeProperties.remove(id, i);
			}
		}
		else if (prevSize < size){
			for (int i = prevSize; i < size; i++) {
				this.addProperty(def);
			}
		}
	}

	private void addProperty(PropertyDef2 property) {
		for (int i = 0; i < property.getArraySize(); i++) {
			runtimeProperties.put(RuntimeProperty.createIdText(property.getId(), i), new RuntimeProperty(property));
		}
	}

	public RuntimeProperty get(String id) {
		if (id.contains(RuntimeProperty.INDEXSIGN)) {
			return this.runtimeProperties.get(id);
		}
		return this.runtimeProperties.get(RuntimeProperty.createIdText(id, 0));
	}

	public RuntimeProperty get(String id, int index) {
		return this.runtimeProperties.get(RuntimeProperty.createIdText(id, index));
	}
	
	public List<RuntimeProperty> getAllProperties() {
		return new ArrayList<RuntimeProperty>(this.runtimeProperties.values());
	}

	public List<RuntimeProperty> getAllProperties(PropertyType2 type) {
		List<RuntimeProperty> ret = new ArrayList<>();
		
		this.runtimeProperties.getRuntimeProperties().forEach((id, value) -> {
			if (value.getType().equals(type) || type.equals(PropertyType2.NotSpecified)) {
				ret.add(value);
			}
		});
		return ret;
	}

	public List<String> getIds(PropertyType2 type) {
		List<String> ret = new ArrayList<>();
		this.runtimeProperties.getRuntimeProperties().forEach((id, value) -> {
			if (value.getType().equals(type) || type.equals(PropertyType2.NotSpecified)) {
				ret.add(id);
			}
		});
		return ret;
	}

	public void save(String filename) {
		IdValues idValues = new IdValues();
		this.runtimeProperties.getRuntimeProperties().forEach((id, value)->{
			idValues.idValue.add(new IdValue(id, value.getCurrentValue()));
		});
		new JsonPersistent().saveJson(idValues, filename);
	}

	public IdValues load(String filename) {
		IdValues loaded = new JsonPersistent().loadJson(IdValues.class, filename);
		return loaded;
	}
	
	public void resetMask() {
		this.runtimeProperties.getRuntimeProperties().forEach((id, prop) -> {
			prop.clearOptionMask();
		});
	}
}

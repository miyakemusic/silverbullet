package jp.silverbullet.property2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RuntimePropertyStore {

	private Map<String, RuntimeProperty> runtimeProperties = new LinkedHashMap<>();
	
	public RuntimePropertyStore(PropertyHolder2 propertyHolder) {
		propertyHolder.getProperties().forEach(property -> {
			addProperty(property);
		});
		
		propertyHolder.addListener(new PropertDefHolderListener() {
			@Override
			public void onChange(String id) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAdd(String id) {
				addProperty(propertyHolder.get(id));
			}

			@Override
			public void onRemove(String id) {
				for (Iterator<String> i = runtimeProperties.keySet().iterator(); i.hasNext();) {
					  if (i.next().split("#")[0].equals(id)) {
					    i.remove();
					  }
				}
			}
		});
	}

	private void addProperty(PropertyDef2 property) {
		for (int i = 0; i < property.getArraySize(); i++) {
			runtimeProperties.put(property.getId() + "#" + i, new RuntimeProperty(property));
		}
	}

	public RuntimeProperty get(String id) {
		if (!id.contains("#")) {
			id += "#0";
		}
		return this.runtimeProperties.get(id);
	}

	public List<RuntimeProperty> getAllProperties() {
		return new ArrayList<RuntimeProperty>(this.runtimeProperties.values());
	}

	public List<RuntimeProperty> getAllProperties(String type) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getIds(String type) {
		// TODO Auto-generated method stub
		return null;
	}

	public void save(String filename) {
		// TODO Auto-generated method stub
		
	}

	public void resetMask() {
		// TODO Auto-generated method stub
		
	}
}

package jp.silverbullet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.silverbullet.property.ListDetailElement;
import jp.silverbullet.property.PropertyDef;
import jp.silverbullet.property.PropertyHolder;
import jp.silverbullet.property.PropertyHolderListener;

public class SvPropertyStore {
	private Map<String, SvProperty> map = new HashMap<String, SvProperty>();
	private List<String> types = new ArrayList<String>();
	private PropertyHolder propertiesHolder;
	
	public SvPropertyStore(PropertyHolder propertiesHolder) {		
		registerProperties(propertiesHolder);
	}
	private void registerProperties(PropertyHolder propertiesHolder) {
		this.propertiesHolder = propertiesHolder;
		for (PropertyDef prop : propertiesHolder.getProperties()) {
			addProperty(prop);
		}
		propertiesHolder.addPropertyHolderListener(new PropertyHolderListener() {
			@Override
			public void onAdded(PropertyDef newProperty) {
				map.put(newProperty.getId(), new SvProperty(newProperty));
			}

			@Override
			public void onRemoved(PropertyDef property) {
				map.remove(property.getId());
			}

			@Override
			public void onPropertyUpdated(PropertyDef propertyDef) {
	
			}

			@Override
			public void onIdChanged(String oldId, String newId) {
				map.put(newId, map.get(oldId));
				map.remove(oldId);
			}
		});
	}
	private void addProperty(PropertyDef prop) {
		stripString(prop);
		
		for (int i = 0; i < prop.getSize(); i++) {
			map.put(prop.getId() + "@" + i, new SvProperty(prop, i));
		}
		if (!types.contains(prop.getType())) {
			types.add(prop.getType());
		}
	}
	
	public SvProperty getProperty(String id) {
		SvProperty ret = this.map.get(id + "@" + "0");
		if (ret == null) {
			System.out.println("Cannot find " + id);
//			addProperty(this.propertiesHolder.getProperty(id));
		}
		return ret;
	}
	
	private void stripString(PropertyDef prop) {
		try {
		prop.setTitle(stripTr(prop.getTitle()));
		for (ListDetailElement e : prop.getListDetail()) {
			e.setTitle(stripTr(e.getTitle()));
		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private String stripTr(String title) {
		if (title == null) {
			return "";
		}
		if (title.startsWith("tr(")) {
			return title.substring(4, title.length() - 2);
		}
		else if (title.startsWith("\"")) {
			return title.substring(1, title.length() - 1);
		}
		return title;
	}
	public List<SvProperty> getAllProperties() {
		return new ArrayList<SvProperty>(this.map.values());
	}

	public List<String> getAllTypes() {
		return this.types;
	}
	public List<SvProperty> getAllProperties(String type) {
		List<SvProperty> ret = new ArrayList<SvProperty>();
		for (SvProperty prop : this.map.values()) {
			if (prop.getType().equals(type)) {
				ret.add(prop);
			}
		}
		return ret;
	}
	public List<String> getIds(String type) {
		List<String> ret = new ArrayList<String>();
		for (SvProperty prop : this.map.values()) {
			if (prop.getType().equals(type)) {
				ret.add(prop.getId());
			}
		}
		return ret;
	}
	public List<String> getAllIds() {
		List<String> ret = new ArrayList<String>();
		for (SvProperty prop : this.map.values()) {
			ret.add(prop.getId());
		}
		return ret;
	}
	public List<SvProperty> getProperties(List<String> ids) {
		List<SvProperty> ret = new ArrayList<SvProperty>();
		for (String id : ids) {
			ret.add(this.map.get(id));
		}
		return ret;
	}
	
	public void importProperties(PropertyHolder propertiesHolder) {
		registerProperties(propertiesHolder);
	}

}

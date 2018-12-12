package jp.silverbullet;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.silverbullet.property.ListDetailElement;
import jp.silverbullet.property.PropertyDef;
import jp.silverbullet.property.PropertyHolder;
import jp.silverbullet.property.PropertyHolderListener;

public class SvPropertyStore {
	private Map<String, SvProperty> map = new LinkedHashMap<String, SvProperty>();
	private List<String> types = new ArrayList<String>();
	public SvPropertyStore(PropertyHolder propertiesHolder) {		
		registerProperties(propertiesHolder);
	}
	private void registerProperties(PropertyHolder propertiesHolder) {
		for (PropertyDef prop : propertiesHolder.getProperties()) {
			addProperty(prop);
		}
		propertiesHolder.addPropertyHolderListener(new PropertyHolderListener() {
			@Override
			public void onAdded(PropertyDef newProperty) {
				addProperty(newProperty);
				//map.put(newProperty.getId(), new SvProperty(newProperty));
			}

			@Override
			public void onRemoved(PropertyDef property) {
				removeProperty(property.getId());
			}

			@Override
			public void onPropertyUpdated(PropertyDef propertyDef) {
	
			}

			@Override
			public boolean onIdChanged(String oldId, String newId) {
				return changePropertyId(oldId, newId);
			}
		});
	}
	
	private boolean changePropertyId(String oldId, String newId) {
		if (oldId.equals(newId)) {
			return false;
		}
		SvProperty tmp = this.getProperty(oldId);
		this.addProperty(tmp.getProperty());
		removeProperty(oldId);
		return true;
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
	
	private void removeProperty(String id) {
		List<String> removed = new ArrayList<String>();
		for (String key : this.map.keySet()) {
			if (key.split("@")[0].equals(id)) {
				removed.add(key);
			}
		}
		for (String key : removed) {
			this.map.remove(key);
		}
	}
	public SvProperty getProperty(String id) {
		if (!id.contains("@")) {
			id = id + "@" + "0";
		}
		SvProperty ret = this.map.get(id);
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

	public void save(String filename) {
		List<String> lines = new ArrayList<>();
		for (String id : this.map.keySet()) {
			SvProperty property = this.map.get(id);
			lines.add("<" + id + ">" + property.getPersistentData() + "</" + id + ">");
		}
		try {
			Files.deleteIfExists(Paths.get(filename));
			Files.write(Paths.get(filename), lines,
                Charset.forName("UTF-8"), StandardOpenOption.CREATE);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void load(String filename) {
		try {
			List<String> lines = Files.readAllLines(Paths.get(filename));
			
			for (String line : lines) { 
				String[] tmp = line.split("[<>]");
				String id = tmp[1];
				String value = tmp[2];
				this.map.get(id).setPersistentData(value);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void resetMask() {
		for (SvProperty prop : this.map.values()) {
			prop.resetMask();
		}
	}
}

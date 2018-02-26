package jp.silverbullet.property;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PropertyHolder {
	private PropertyType types = new PropertyType();
	private List<PropertyDef> properties = new ArrayList<PropertyDef>();
	private Set<PropertyHolderListener> listeners = new HashSet<>();
	private PropertyDefListener listener = new PropertyDefListener() {
		@Override
		public void onChanged(PropertyDef propertyDef) {
			if (!types.containsType(propertyDef.getType())) {
				types.getDefinitions().put(propertyDef.getType(), new StringArray());
			}
			fireOnPropertyChanged(propertyDef);
		}

		@Override
		public void onIdChanged(String oldId, String newId) {
			fireOnIdChanged(oldId, newId);
		}
	};
	
	public void addPropertyHolderListener(PropertyHolderListener listener) {
		this.listeners.add(listener);
	}
	
	protected void fireOnIdChanged(String oldId, String newId) {
		for (PropertyHolderListener listener : listeners) {
			listener.onIdChanged(oldId, newId);
		}
	}

	protected void fireOnPropertyChanged(PropertyDef propertyDef) {
		for (PropertyHolderListener listener : listeners) {
			listener.onPropertyUpdated(propertyDef);
		}
	}

	public PropertyType getTypes() {
		return types;
	}
	public void setTypes(PropertyType types) {
		this.types = types;
	}
	public List<PropertyDef> getProperties() {
		return properties;
	}
	public void setProperties(List<PropertyDef> properties) {
		this.properties = properties;
	}
	public List<String> getAllTypes() {
		return new ArrayList<String>(this.types.getDefinitions().keySet());
	}
		
	public void addProperty(PropertyDef newProperty) {
//		newProperty.setArgumentDef(this.getTypes().getArguments(newProperty.getType()));
		newProperty.setArgumentDef(argDef);
		newProperty.initArgumentValues();
		newProperty.addPropertyDefListener(listener);
		
		this.properties.add(newProperty);
		fireOnAdded(newProperty);
	}
	
	public void addPropertyAfter(PropertyDef newProperty, PropertyDef property) {
		int index = this.properties.indexOf(property);
		newProperty.setArgumentDef(argDef);
		newProperty.initArgumentValues();
		newProperty.addPropertyDefListener(listener);
		
		this.properties.add(index+1, newProperty);
		fireOnAdded(newProperty);
	}

	private ArgumentDefInterface argDef = new ArgumentDefInterface() {

		@Override
		public int indexOf(String type, String key) {
			return types.getArguments(type).indexOf(key);
		}

		@Override
		public List<String> get(String type) {
			return types.getArguments(type);
		}
		
	};

	protected void fireOnAdded(PropertyDef newProperty) {
		for (PropertyHolderListener listener : listeners) {
			listener.onAdded(newProperty);
		}
	}
	public void remove(PropertyDef property) {
		this.properties.remove(property);
		property.removePropertyDefListener(listener);
		fireOnRemove(property);
	}

	protected void fireOnRemove(PropertyDef property) {
		for (PropertyHolderListener listener : listeners) {
			listener.onRemoved(property);
		}
	}
	public void initialize() {
		for (PropertyDef property : this.properties) {
			try {
	//			property.setArgumentDef(this.types.getDefinitions().get(property.getType()).list);
				property.setArgumentDef(argDef);
				property.addPropertyDefListener(listener);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void replaceText(String current, String newtext) {
		for (PropertyDef property : this.properties) {
			List<String> newOthers = new ArrayList<String>();
			for (String s : property.getOthers()) {
				newOthers.add(s.replace(current, newtext));
			}
			property.setOthers(newOthers);
		}
	}

	public void removeAll(List<PropertyDef> properties) {
		this.properties.removeAll(properties);
	}

	public void removePropertyHolderListener(PropertyHolderListener listener2) {
		this.listeners.remove(listener2);
	}

}

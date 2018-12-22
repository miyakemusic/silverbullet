package jp.silverbullet.property2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;



public class PropertyDef2 implements Cloneable {

	public static final String True = "true";
	public static final String False = "false";
	
	@TableColumn(targetType={PropertyType2.NotSpecified}, Presentation="ID", Width=200)
	private String id = "";
	
	@TableColumn(targetType={PropertyType2.NotSpecified}, Presentation="Type", Width=70)
	private PropertyType2 type = PropertyType2.NotSpecified;
	
	@TableColumn(targetType={PropertyType2.NotSpecified}, Presentation="Presentation", Width=200)
	private String title = "";
	
	@TableColumn(targetType={PropertyType2.Boolean, PropertyType2.Numeric, PropertyType2.Text}, Presentation="DefaultValue", Width=200)
	private String defaultValue = "";
	
	@TableColumn(targetType={PropertyType2.List}, Presentation="DefaultID", Width=200)
	private String defaultId = "";
	
	@TableColumn(targetType={PropertyType2.NotSpecified}, Presentation="Group", Width=100)
	private String group = "";
	
	@TableColumn(targetType={PropertyType2.NotSpecified}, Presentation="Size", Width=30)
	private int arraySize = 1;
	
//	@TableColumn(targetType={PropertyType2.List}, Presentation="Options")
	private Map<String, ListDetailElement> options = new LinkedHashMap<>();
	
	@TableColumn(targetType={PropertyType2.Numeric}, Presentation="Min", Width=50)
	private double min = 0.0;
	
	@TableColumn(targetType={PropertyType2.Numeric}, Presentation="Max", Width=50)
	private double max = 0.0;
	
	@TableColumn(targetType={PropertyType2.Numeric}, Presentation="Decimals", Width=50)
	private int decimals = 0;
	
	@TableColumn(targetType={PropertyType2.Text}, Presentation="MaxLength", Width=50)
	private int maxLength = 9999;
	
	@TableColumn(targetType={PropertyType2.NotSpecified}, Presentation="Unit", Width=80)
	private String unit = "";
	
	@TableColumn(targetType={PropertyType2.NotSpecified}, Presentation="Persistent", Width=50)
	private Boolean persistent = true;
	
	@TableColumn(targetType={PropertyType2.NotSpecified}, Presentation="Comment", Width=200)
	private String comment = "";
	
	private Set<PropertyDefListener2> listeners = new HashSet<>();

	public PropertyDef2() {}
	
	public PropertyDef2(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}

	public void setId(String id2) {
		if (this.id != null && this.id.equals(id2)) {
			return;
		}
		String oldId = this.id;
		this.id = id2;

		if (this.getType() != null && this.getType().equals(PropertyType2.List)) {
			Map<String, ListDetailElement> tmp = this.options;			
			this.options = new LinkedHashMap<String, ListDetailElement>();

			for (String optionId : tmp.keySet()) {
				ListDetailElement e = tmp.get(optionId);
				String newOptionId = optionId.replace(oldId, id);
				e.setId(newOptionId);
				this.options.put(newOptionId, e);
			}
			this.defaultId = this.defaultId.replace(oldId, id);
		}
		
		fireIdChanged(id, oldId);
	}

	private void fireIdChanged(String newId, String oldId) {
		this.listeners.forEach(listener -> listener.onIdChanged(newId, oldId));
	}

	public PropertyDef2 option(String optionId, String title, String comment) throws Exception {
		if (!optionId.startsWith(this.id)) {
			throw new Exception("Invalid Option ID name");
		}
		options.put(optionId, new ListDetailElement(optionId, title, comment));
		
		List<String> mapkey = new ArrayList<String>(this.options.keySet());
        Collections.sort(mapkey);

        Map<String, ListDetailElement> newOptions = new LinkedHashMap<>();
        for (String key : mapkey) {
        	newOptions.put(key, options.get(key));
        }
        this.options = newOptions;
        
        if (this.defaultId.isEmpty()) {
        	this.defaultId = optionId;
        }
        fireOptionAdded(optionId, title, comment);
		return this;
	}

	private void fireOptionAdded(String optionId, String title2, String comment2) {
		for (PropertyDefListener2 listener : this.listeners) {
			listener.onOptionAdded(id, optionId, title2, comment2);
		}
	}

	public PropertyDef2 defaultValue(String value) {
		this.defaultValue = value;
		fireChanged(this.defaultValue, "DefaultValue");
		return this;
	}

	private void fireChanged(String value, String fieldName) {
		this.listeners.forEach(listener -> listener.onParamChange(id, value, fieldName));
	}

	public PropertyDef2 setType(PropertyType2 type) {
		this.type = type;
		fireChanged(this.type);
		return this;
	}

	private void fireChanged(PropertyType2 type2) {
		this.listeners.forEach(listener -> listener.onTypeChange(id, type2));
	}

	@JsonIgnore
	public Collection<ListDetailElement> getOptionValues() {
		return this.options.values();
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public PropertyDef2 min(double min) {
		this.min = min;
		fireChanged(this.min, "Min");
		return this;
	}

	private void fireChanged(double value, String name) {
		for (PropertyDefListener2 listener : this.listeners) {
			listener.onParamChange(id, value, name);
		}
	}

	public PropertyDef2 max(double max) {
		this.max = max;
		fireChanged(this.max, "Max");
		return this;
	}

	public PropertyDef2 unit(String unit) {
		this.unit = unit;
		fireChanged(this.unit, "Unit");
		return this;
	}

	public PropertyDef2 decimals(int i) {
		this.decimals= i;
		fireChanged(this.decimals, "Decimals");
		return this;
	}

	public PropertyDef2 defaultValue(double d) {
		this.defaultValue = String.valueOf(d);
		fireChanged(this.defaultValue, "DefaultValue");
		return this;
	}

	public String getUnit() {
		return this.unit;
	}

	public int getDecimals() {
		return this.decimals;
	}

	public double getMin() {
		return this.min;
	}

	public double getMax() {
		return this.max;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength(maxLength);
	}

	public PropertyType2 getType() {
		return type;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		fireChanged(this.defaultValue, "DefaultValue");
	}

	public void setMin(double min) {
		this.min(min);
	}

	public void setMax(double max) {
		this.max(max);
	}

	public void setUnit(String unit) {
		this.unit(unit);
	}

	public void setDecimals(int decimals) {
		this.decimals(decimals);
	}

	public PropertyDef2 maxLength(int length) {
		this.maxLength = length;
		fireChanged(this.maxLength, "MaxLength");
		return this;
	}

	@JsonIgnore
	public void setAllOptions(List<ListDetailElement> listDetail) {
		for (ListDetailElement e: listDetail) {
			this.options.put(e.getId(), e);
		}
	}

	public void deleteOption(String optionId) {
		this.options.remove(optionId);
		fireOptionRemoved(optionId);
	}

	private void fireOptionRemoved(String optionId) {
		this.listeners.forEach(listener -> listener.onOptionRemove(id, optionId));
	}

	public ListDetailElement getOption(int index) {
		return this.options.get(new ArrayList<String>(this.options.keySet()).get(index));
	}

	public int getArraySize() {
		return arraySize;
	}

	public void setArraySize(int arraySize) {
		this.arraySize(arraySize);
	}

	private PropertyDef2 arraySize(int arraySize2) {
		this.arraySize = arraySize2;
		this.fireChanged(arraySize2, "ArraySize");
		return this;
	}

	public void changeOptionId(String oldId, String newId) {
		ListDetailElement e = this.options.get(oldId);
		this.deleteOption(oldId);
		e.setId(newId);
		this.options.put(newId, e);
	}

	public void addListener(PropertyDefListener2 listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(PropertyDefListener2 listener) {
		this.listeners.remove(listener);
	}

	@Override
	protected PropertyDef2 clone() {
		try {
			PropertyDef2 ret = (PropertyDef2)super.clone();
			ret.options = new LinkedHashMap<String, ListDetailElement>();
			for (String optionId : this.options.keySet()) {
				ListDetailElement e = this.options.get(optionId).clone();
				ret.options.put(optionId, e);
			}
			
			return ret;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@JsonIgnore
	public ListDetailElement getOption(String optionId) {
		return this.options.get(optionId);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title(title);
	}

	public Boolean getPersistent() {
		return persistent;
	}

	public void setPersistent(Boolean persistent) {
		this.persistent(persistent);
	}

	public PropertyDef2 persistent(Boolean persistent) {
		this.persistent = persistent;
		this.fireChanged(persistent.toString(), "Persistent");
		return this;
	}
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getDefaultId() {
		return defaultId;
	}

	public void setDefaultId(String id) {
		this.defaultId(id);
	}

	public void setOptions(Map<String, ListDetailElement> options) {
		this.options = options;
	}

	public PropertyDef2 defaultId(String id) {
		this.defaultId = id;
		this.fireChanged(id, "defaultId");
		return this;
	}

	public Map<String, ListDetailElement> getOptions() {
		return options;
	}

	public PropertyDef2 title(String string) {
		this.title = string;
		this.listeners.forEach(listener -> listener.onParamChange(id, string, "Title"));
		return this;
	}

	public void clearListeners() {
		this.listeners.clear();
	}

	
}

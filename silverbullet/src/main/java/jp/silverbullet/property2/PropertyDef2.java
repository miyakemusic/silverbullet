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

	public static final String TITLE = "Title";
	public static final String PERSISTENT = "Persistent";
	public static final String ARRAY_SIZE = "ArraySize";
	public static final String MAX_LENGTH = "MaxLength";
	public static final String DECIMALS = "Decimals";
	public static final String UNIT = "Unit";
	public static final String MAX = "Max";
	public static final String MIN = "Min";
	public static final String DEFAULT_VALUE = "DefaultValue";
	public static final String DEFAULT_ID = "DefaultID";
	public static final String True = "true";
	public static final String False = "false";
	
	public static final String ID = "ID";
	public static final String TYPE = "Type";
	public static final String PRESENTATION = "Presentation";
	public static final String GROUP = "Group";
	public static final String COMMENT = "Comment";
	
	@TableColumn(targetType={PropertyType2.NotSpecified}, Presentation=ID, Width=200)
	private String id = "";
	
	@TableColumn(targetType={PropertyType2.NotSpecified}, Presentation=TYPE, Width=70)
	private PropertyType2 type = PropertyType2.NotSpecified;
	
	@TableColumn(targetType={PropertyType2.NotSpecified}, Presentation=PRESENTATION, Width=200)
	private String title = "";
	
	@TableColumn(targetType={PropertyType2.Boolean, PropertyType2.Numeric, PropertyType2.Text}, Presentation=DEFAULT_VALUE, Width=200)
	private String defaultValue = "";
	
	@TableColumn(targetType={PropertyType2.List}, Presentation=DEFAULT_ID, Width=200)
	private String defaultId = "";
	
	@TableColumn(targetType={PropertyType2.NotSpecified}, Presentation=GROUP, Width=100)
	private String group = "";
	
	@TableColumn(targetType={PropertyType2.NotSpecified}, Presentation=ARRAY_SIZE, Width=30)
	private int arraySize = 1;
	
	private Map<String, ListDetailElement> options = new LinkedHashMap<>();
	
	@TableColumn(targetType={PropertyType2.Numeric}, Presentation=MIN, Width=50)
	private double min = 0.0;
	
	@TableColumn(targetType={PropertyType2.Numeric}, Presentation=MAX, Width=50)
	private double max = 0.0;
	
	@TableColumn(targetType={PropertyType2.Numeric}, Presentation=DECIMALS, Width=50)
	private int decimals = 0;
	
	@TableColumn(targetType={PropertyType2.Text}, Presentation=MAX_LENGTH, Width=50)
	private int maxLength = 9999;
	
	@TableColumn(targetType={PropertyType2.NotSpecified}, Presentation=UNIT, Width=80)
	private String unit = "";
	
	@TableColumn(targetType={PropertyType2.NotSpecified}, Presentation=PERSISTENT, Width=50)
	private Boolean persistent = true;
	
	@TableColumn(targetType={PropertyType2.NotSpecified}, Presentation=COMMENT, Width=200)
	private String comment = "";
	
	private Set<PropertyDefListener2> listeners = new HashSet<>();
	
//	@JsonIgnore
//	private int index;

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
			throw new Exception("Invalid Option ID name " + optionId);
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
		String prev = this.defaultValue;
		this.defaultValue = value;
		fireChanged(this.defaultValue, prev, DEFAULT_VALUE);
		return this;
	}

	private void fireChanged(String value, String prevValue, String fieldName) {
		this.listeners.forEach(listener -> listener.onParamChange(id, value, prevValue, fieldName));
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

	@JsonIgnore
	public PropertyDef2 min(double min) {
		double prev = this.max;
		this.min = min;
		fireChanged(this.min, prev, MIN);
		return this;
	}

	private void fireChanged(double value, double prev, String name) {
		for (PropertyDefListener2 listener : this.listeners) {
			listener.onParamChange(id, value, prev, name);
		}
	}

	@JsonIgnore
	public PropertyDef2 max(double max) {
		double prev = this.max;
		this.max = max;
		fireChanged(this.max, prev, MAX);
		return this;
	}

	public PropertyDef2 unit(String unit) {
		String prev = this.unit;
		this.unit = unit;
		fireChanged(this.unit, prev, UNIT);
		return this;
	}

	@JsonIgnore
	public PropertyDef2 decimals(int i) {
		int prev = this.decimals;
		this.decimals= i;
		fireChanged(this.decimals, prev, DECIMALS);
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

	@JsonIgnore
	public PropertyDef2 defaultValue(double d) {
		String prev = this.defaultValue;
		this.defaultValue = String.valueOf(d);
		fireChanged(this.defaultValue, prev, DEFAULT_VALUE);
		return this;
	}
	
	public void setDefaultValue(String defaultValue) {
		this.defaultValue(defaultValue);
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

	@JsonIgnore
	public PropertyDef2 maxLength(int length) {
		int prev = this.maxLength;
		this.maxLength = length;
		fireChanged(this.maxLength, prev, MAX_LENGTH);
		return this;
	}

	@JsonIgnore
	public void setAllOptions(List<ListDetailElement> listDetail) {
		for (ListDetailElement e: listDetail) {
			this.options.put(e.getId(), e);
		}
	}

	public void removeOption(String optionId) {
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

	@JsonIgnore
	public PropertyDef2 arraySize(int arraySize2) {
		int prev = this.arraySize;
		this.arraySize = arraySize2;
		this.fireChanged(arraySize2, prev, ARRAY_SIZE);
		return this;
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

	@JsonIgnore
	public PropertyDef2 persistent(Boolean persistent) {
		Boolean prev = this.persistent;
		this.persistent = persistent;
		this.fireChanged(persistent.toString(), prev.toString(), PERSISTENT);
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
		return this;
	}

	public Map<String, ListDetailElement> getOptions() {
		return options;
	}

	@JsonIgnore
	public PropertyDef2 title(String string) {
		String prev = this.title;
		this.title = string;
		this.listeners.forEach(listener -> listener.onParamChange(id, string, prev, TITLE));
		return this;
	}

	public void clearListeners() {
		this.listeners.clear();
	}

	@JsonIgnore
	public boolean isList() {
		return this.getType().equals(PropertyType2.List);
	}

	@JsonIgnore
	public boolean isNumeric() {
		return this.getType().equals(PropertyType2.Numeric);
	}

//	@JsonIgnore
//	public int getIndex() {
//		return this.index;
//	}

	@JsonIgnore
	public List<String> getOptionIds() {
		return new ArrayList<String>(this.options.keySet());
	}

	public boolean isPersistent() {
		return this.persistent;
	}

//	public void changeOptionId(String oldId, String newId) {
//		ListDetailElement e = this.options.get(oldId);
//		this.removeOption(oldId);
//		e.setId(newId);
//		this.options.put(newId, e);
//	}
	
	public void updateOptionId(String oldId, String newId) {
		ListDetailElement e = this.getOption(oldId);
		this.removeOption(oldId);
		try {
			this.option(newId, e.getTitle(), e.getComment());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	@JsonIgnore
	public boolean isText() {
		return this.getType().equals(PropertyType2.Text);
	}

	@JsonIgnore
	public boolean isTable() {
		return this.getType().equals(PropertyType2.Table);
	}

	@JsonIgnore
	public boolean isBoolean() {
		return this.getType().equals(PropertyType2.Boolean);
	}

	
}

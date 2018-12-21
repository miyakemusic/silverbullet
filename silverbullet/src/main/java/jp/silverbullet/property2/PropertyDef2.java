package jp.silverbullet.property2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.silverbullet.property.ListDetailElement;



public class PropertyDef2 implements Cloneable {

	public static final String True = "true";
	public static final String False = "false";
	
	@TableColumn(targetType={PropertyType2.NotSpecified}, Presentation="ID")
	private String id = "";
	
	@TableColumn(targetType={PropertyType2.NotSpecified}, Presentation="Type")
	private PropertyType2 type = PropertyType2.NotSpecified;
	
	@TableColumn(targetType={PropertyType2.NotSpecified}, Presentation="Default")
	private String defaultValue = "";
	
	@TableColumn(targetType={PropertyType2.NotSpecified}, Presentation="Size")
	private int arraySize = 1;
	
//	@TableColumn(targetType={PropertyType2.List}, Presentation="Options")
	private Map<String, ListDetailElement> options = new LinkedHashMap<>();
	
	@TableColumn(targetType={PropertyType2.Numeric}, Presentation="Min")
	private double min = 0.0;
	
	@TableColumn(targetType={PropertyType2.Numeric}, Presentation="Max")
	private double max = 0.0;
	
	@TableColumn(targetType={PropertyType2.Numeric}, Presentation="Decimals")
	private int decimals = 0;
	
	@TableColumn(targetType={PropertyType2.Text}, Presentation="MaxLength")
	private int maxLength = 9999;
	
	@TableColumn(targetType={PropertyType2.NotSpecified}, Presentation="Unit")
	private String unit = "";
	
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
		
		fireIdChanged(id, oldId);
		
		if (this.getType() != null && this.getType().equals(PropertyType2.List)) {
			Map<String, ListDetailElement> tmp = this.options;			
			this.options = new LinkedHashMap<String, ListDetailElement>();

			for (String optionId : tmp.keySet()) {
				ListDetailElement e = tmp.get(optionId);
				String newOptionId = optionId.replace(oldId, id);
				e.setId(newOptionId);
				this.options.put(newOptionId, e);
			}
			this.defaultValue = this.defaultValue.replace(oldId, id);
		}
	}

	private void fireIdChanged(String newId, String oldId) {
		for (PropertyDefListener2 listener : this.listeners) {
			listener.onIdChanged(newId, oldId);
		}
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
		return this;
	}

	public PropertyDef2 defaultValue(String value) {
		this.defaultValue = value;
		return this;
	}

	public PropertyDef2 setType(PropertyType2 type) {
		this.type = type;
		return this;
	}

	public Collection<ListDetailElement> getOptions() {
		return this.options.values();
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public PropertyDef2 min(double min) {
		this.min = min;
		return this;
	}

	public PropertyDef2 max(double max) {
		this.max = max;
		return this;
	}

	public PropertyDef2 unit(String unit) {
		this.unit = unit;
		return this;
	}

	public PropertyDef2 decimals(int i) {
		this.decimals= i;
		return this;
	}

	public PropertyDef2 defaultValue(double d) {
		this.defaultValue = String.valueOf(d);
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
		this.maxLength = maxLength;
	}

	public PropertyType2 getType() {
		return type;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public void setDecimals(int decimals) {
		this.decimals = decimals;
	}

	public PropertyDef2 maxLength(int length) {
		this.maxLength = length;
		return this;
	}

	public void setOptions(List<ListDetailElement> listDetail) {
		for (ListDetailElement e: listDetail) {
			this.options.put(e.getId(), e);
		}
	}

	public void deleteOption(String optionId) {
		this.options.remove(optionId);
	}

	public ListDetailElement getOption(int index) {
		return this.options.get(new ArrayList<String>(this.options.keySet()).get(index));
	}

	public int getArraySize() {
		return arraySize;
	}

	public void setArraySize(int arraySize) {
		this.arraySize = arraySize;
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

	public ListDetailElement getOption(String optionId) {
		return this.options.get(optionId);
	}

	
}

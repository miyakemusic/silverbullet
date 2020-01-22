package jp.silverbullet.core.property2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jp.silverbullet.core.dependency2.DependencySpec;
import jp.silverbullet.core.property2.RuntimePropertyListener.Flag;

public class RuntimeProperty implements Cloneable {
	private String currentValue = "";
	
	private String customTitle = null;
	
	public static final String INDEXSIGN = "#";
	
	private PropertyDef2 property;
	private String prevValue = "";
	private Set<RuntimePropertyListener> listeners = new HashSet<RuntimePropertyListener>();
	private Map<String, Boolean> listMask = new HashMap<String, Boolean>();
	private boolean enabled = true;
	
	@JsonIgnore	private double currentMin;
	@JsonIgnore private double currentMax;
	private int index;

	private boolean forceChange = false;
	
	public RuntimeProperty() {}
	
	public RuntimeProperty(PropertyDef2 property2) {
		this.property = property2;
		
		this.resetValue();
	}
	
	private String formatValue(String value) {
		if (value.isEmpty()) {
			value = "0";
		}
		Double v = Double.valueOf(value);
		return String.format("%."+  this.property.getDecimals()  + "f", v);
	}

	public String getCurrentValue() {
		return this.currentValue;
	}
	
	@JsonIgnore
	public String getId() {
		return this.property.getId();
	}

	@JsonIgnore
	public boolean isList() {
		return this.property.isList();
	}

	@JsonIgnore
	public boolean isNumericProperty() {
		return this.property.isNumeric();
	}
	
	private String formatNumeric(double value) {
		return String.format("%." + this.property.getDecimals() + "f", value);
	}
	
	@Override
	public RuntimeProperty clone()  {
		try {
			RuntimeProperty ret = (RuntimeProperty)super.clone();
			ret.listeners = new HashSet<RuntimePropertyListener>();
			ret.listMask = new HashMap<String, Boolean>(this.listMask);
			return ret;
		} catch (CloneNotSupportedException e) {
			
		}
		return null;
	}

	@JsonIgnore
	public String getMin() {
		//return formatNumeric(this.property.getMin());
		return formatNumeric(this.currentMin);
	}

	@JsonIgnore
	public String getMax() {
	//	return formatNumeric(this.property.getMax());
		return formatNumeric(this.currentMax);
	}
	
	@JsonIgnore
	public List<ListDetailElement> getDefOptions() {
		return new ArrayList<ListDetailElement>(this.property.getOptions().values());
	}
	
	public void setCurrentValue(String val) {
		if (!forceChange  && this.currentValue.equals(val)) {
			return;
		}
		
		this.prevValue = this.currentValue;
		
		if (this.isNumericProperty()) {
			this.currentValue = this.formatNumeric(Double.valueOf(val));
		}
		else {
			this.currentValue = val;
		}

		this.listeners.forEach(listener -> listener.onValueChange(property.getId(), this.index, this.currentValue));
	
	}
	
	public void addListener(RuntimePropertyListener SvPropertyListener) {
		this.listeners.add(SvPropertyListener);
	}
	
	public void removeListener(RuntimePropertyListener SvPropertyListener) {
		this.listeners.remove(SvPropertyListener);
	}
	
	@JsonIgnore
	public List<String> getListIds() {
		return this.property.getOptionIds();
	}

	public void enableOption(String id, boolean b) {
		this.listMask.put(id, !b);
		this.listeners.forEach(listener -> listener.onListMaskChange(getId(), getIndex(), id, !b));
	}

	public boolean isOptionDisabled(String optionId) {
		if (!this.listMask.containsKey(optionId)) {
			return false;
		}
		return this.listMask.get(optionId);
	}

	public boolean isOptionDisabled(int index2) {
		String optionId = this.property.getOptionIds().get(index2);
		return this.isOptionDisabled(optionId);
	}
	
	public void setEnabled(boolean enabled2) {
		if (!forceChange  && this.enabled == enabled2) {
			return;
		}
		this.enabled = enabled2;
		this.listeners.forEach(listener -> listener.onEnableChange(getId(), getIndex(), this.enabled));
	}

	public void setMin(String min) {
		double minv = Double.valueOf(min);
		//if (!forceChange  && this.property.getMin() == minv) {
		if (!forceChange  && this.currentMin == minv) {
			return;
		}
		this.currentMin = minv;
//		this.property.setMin(minv);
		this.listeners.forEach(listener -> listener.onFlagChange(getId(), getIndex(), Flag.MIN));
	}

	public void setMax(String max) {
		double maxv = Double.valueOf(max);
		//if (!forceChange  && this.property.getMax() == maxv) {
		if (!forceChange  && this.currentMax == maxv) {
			return;
		}
		this.currentMax = maxv;
//		this.property.setMax(maxv);
		this.listeners.forEach(listener -> listener.onFlagChange(getId(), getIndex(), Flag.MAX));
	}

	public void setSize(Integer size) {
		if (!forceChange  && this.property.getArraySize() == size) {
			return;
		}
		property.setArraySize(size);
		this.listeners.forEach(listener -> listener.onFlagChange(getId(), getIndex(), Flag.SIZE));
	}

	@JsonIgnore
	public boolean isEnabled() {
		return this.enabled;
	}

	@JsonIgnore
	public Integer getSize() {
		return this.property.getArraySize();
	}

	@JsonIgnore
	public Map<String, Boolean> getListMask() {
		return this.listMask;
	}

	public void setListMask(Map<String, Boolean> listMask2) {
		this.listMask = listMask2;
	}

	@JsonIgnore
	public String getTitle() {
		if (this.customTitle != null) {
			return this.customTitle;
		}
		return this.property.getTitle();
	}

	public int getIndex() {
		return this.index;
	}

	@JsonIgnore
	public String getUnit() {
		return this.property.getUnit();
	}
	
	@JsonIgnore
	public List<ListDetailElement> getAvailableListDetail() {
		List<ListDetailElement> ret = new ArrayList<ListDetailElement>();
		this.property.getOptions().forEach((optionId, value) -> {
			if (!this.isOptionDisabled(optionId)) {
				ret.add(value);
			}
		});
		return ret;
	}

	@JsonIgnore
	public boolean isChartProperty() {
		return this.property.getType().equals(PropertyType2.Chart);
	}

	@JsonIgnore
	public String getSelectedListTitle() {
		ListDetailElement e = this.property.getOption(this.currentValue);
		
		if (e != null) {
			return e.getTitle();
		}
		return "";
	}

	@JsonIgnore
	public List<String> getOptionIds() {
		return this.property.getOptionIds();
	}
	
	@JsonIgnore
	public PropertyType2 getType() {
		return this.property.getType();
	}

	@JsonIgnore
	public boolean isText() {
		return this.getType().equals(PropertyType2.Text);
	}

	@JsonIgnore
	public boolean isBoolean() {
		return this.getType().equals(PropertyType2.Boolean);
	}

	@JsonIgnore
	public boolean isAction() {
		return this.getType().equals(PropertyType2.Action);	}

	@JsonIgnore
	public boolean isTable() {
		return this.getType().equals(PropertyType2.Table);
	}

	@JsonIgnore
	public int getDecimals() {
		return this.property.getDecimals();
	}

	@JsonIgnore
	public boolean isPersistent() {
		return this.property.isPersistent();
	}

	public static String createIdText(String id, int index2) {
		return id + INDEXSIGN + String.valueOf(index2);
	}

	@JsonIgnore
	public void setTitle(String string) {
		if (!forceChange  && string.equals(this.customTitle)) {
			return;
		}
		this.customTitle = string;
		this.listeners.forEach(listener -> listener.onTitleChange(getId(), getIndex(), this.currentValue));
	}

	@JsonIgnore
	public void setUnit(String string) {
		if (!forceChange  && string.equals(this.property.getUnit())) {
			return;
		}
		this.property.setUnit(string);
		this.listeners.forEach(listener -> listener.onFlagChange(getId(), getIndex(), RuntimePropertyListener.Flag.UNIT));
	}

	public void clearOptionMask() {
		this.listMask.clear();
	}

	public void setForceChange(boolean forceChange2) {
		this.forceChange = forceChange2;
	}

	public void resetValue() {
		this.clearOptionMask();
		if (this.getType().equals(PropertyType2.List)) {
			this.setCurrentValue(this.property.getDefaultId());
		}
		else if (this.getType().equals(PropertyType2.Action)) {
			this.setCurrentValue(DependencySpec.True);
		}
		else if (this.property.getType().equals(PropertyType2.Numeric)) {
			this.currentValue = formatValue(property.getDefaultValue());
		}
		else {
			this.setCurrentValue(this.property.getDefaultValue());
		}
		
		this.currentMax = this.property.getMax();
		this.currentMin = this.property.getMin();
	}

	public String getOptionTitle(String optionId) {
		return this.property.getOption(optionId).getTitle();
	}


}

package jp.silverbullet.property2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.silverbullet.SvPropertyListener;

public class RuntimeProperty implements Cloneable {
	private PropertyDef2 property;
	private String currentValue = "";
	private String prevValue = "";
	private Set<SvPropertyListener> listeners = new HashSet<SvPropertyListener>();
	private Map<String, Boolean> listMask = new HashMap<String, Boolean>();
	
	private boolean enabled = true;
	
	public RuntimeProperty(PropertyDef2 property2) {
		this.property = property2;
		
		if (this.property.getType().equals(PropertyType2.List)) {
			this.currentValue = property.getDefaultId();
		}
		else if (this.property.getType().equals(PropertyType2.Numeric)) {
			this.currentValue = formatValue(property.getDefaultValue());
		}
		else {
			this.currentValue = property.getDefaultValue();
		}
		
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

	public String getId() {
		return this.property.getId();
	}

	public boolean isListProperty() {
		return this.property.isList();
	}

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
			ret.listeners = new HashSet<SvPropertyListener>();
			ret.listMask = new HashMap<String, Boolean>();
			return ret;
		} catch (CloneNotSupportedException e) {
			
		}
		return null;
	}

	public String getMin() {
		return formatNumeric(this.property.getMin());
	}

	public String getMax() {
		return formatNumeric(this.property.getMax());
	}

	public void setCurrentValue(String val) {
		if (this.currentValue.equals(val)) {
			return;
		}
		
		this.prevValue = this.currentValue;
		
		if (this.isNumericProperty()) {
			this.currentValue = this.formatNumeric(Double.valueOf(val));
		}
		else {
			this.currentValue = val;
		}

		this.listeners.forEach(listener -> listener.onValueChanged(property.getId(), property.getIndex(), this.currentValue));
	}
	
	public void addListener(SvPropertyListener SvPropertyListener) {
		this.listeners.add(SvPropertyListener);
	}
	
	public void removeListener(SvPropertyListener SvPropertyListener) {
		this.listeners.remove(SvPropertyListener);
	}

	public List<String> getListIds() {
		return this.property.getListIds();
	}

	public void disableOption(String id, boolean b) {
		// TODO Auto-generated method stub
		
	}

	public boolean isOptionDisabled(String currentValue2) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setEnabled(boolean equalsIgnoreCase) {
		// TODO Auto-generated method stub
		
	}

	public void setMin(String min) {
		// TODO Auto-generated method stub
		
	}

	public void setMax(String max) {
		// TODO Auto-generated method stub
		
	}

	public void setSize(Integer valueOf) {
		// TODO Auto-generated method stub
		
	}

	public boolean isOptionEnabled(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public Integer getSize() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getListMask() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setListMask(Object listMask2) {
		// TODO Auto-generated method stub
		
	}

	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getUnit() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ListDetailElement> getAvailableListDetail() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isChartProperty() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getSelectedListTitle() {
		// TODO Auto-generated method stub
		return null;
	}
}

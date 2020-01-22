package jp.silverbullet.web.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.core.property2.ChartContent;
import jp.silverbullet.core.property2.ListDetailElement;
import jp.silverbullet.core.property2.PropertyType2;
import jp.silverbullet.core.property2.RuntimeProperty;

public class JsProperty {
	private String id;
	private String title;
	private String unit;
	private List<ListDetailElement> elements = new ArrayList<>();
	private String currentValue;
	private boolean enabled;
	private String currentSelectionId;
	private PropertyType2 type;
	private List<String> disabledOption = new ArrayList<>();
	private String min;
	private String max;
	private int decimals;
	
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public String getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}
	public String getUnit() {
		return unit;
	}
	public List<ListDetailElement> getElements() {
		return elements;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public void setElements(List<ListDetailElement> elements) {
		this.elements = elements;
	}
	public String getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}
	public String getCurrentSelectionId() {
		return currentSelectionId;
	}
	public void setCurrentSelectionId(String currentSelectionId) {
		this.currentSelectionId = currentSelectionId;
	}
	public void setType(PropertyType2 type) {
		this.type = type;
	}
	public PropertyType2 getType() {
		return type;
	}
	public void addDisabledOption(String eid) {
		this.disabledOption.add(eid);
	}
	public List<String> getDisabledOption() {
		return disabledOption;
	}
	public void setMin(String min) {
		this.min = min;
	}
	public void setMax(String max) {
		this.max = max;
	}
	public String getMin() {
		return min;
	}
	public String getMax() {
		return max;
	}
	public void setDecimals(int decimals) {
		this.decimals = decimals;
	}
	public int getDecimals() {
		return decimals;
	}
	
}

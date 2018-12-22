package jp.silverbullet.web.ui;

import java.util.ArrayList;
import java.util.List;

import jp.silverbullet.property2.ListDetailElement;

public class JsProperty {
	private String id;
	private String title;
	private String unit;
	private List<ListDetailElement> elements = new ArrayList<>();
	private String currentValue;
	private boolean enabled;
	private String currentSelectionId;
	
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
	
}

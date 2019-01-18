package jp.silverbullet.web.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.property2.ChartContent;
import jp.silverbullet.property2.ListDetailElement;
import jp.silverbullet.property2.RuntimeProperty;

public class JsProperty {
	private String id;
	private String title;
	private String unit;
	private List<ListDetailElement> elements = new ArrayList<>();
	private String currentValue;
	private boolean enabled;
	private String currentSelectionId;
	
	public JsProperty(RuntimeProperty property, String ext) {
		JsProperty ret = this;//new JsProperty();
		ret.setId(property.getId());
		ret.setTitle(property.getTitle());
		ret.setUnit(property.getUnit());
		ret.setElements(property.getAvailableListDetail());
		ret.setEnabled(property.isEnabled());
		
		if (property.isChartProperty()) {
			if (ext == null) {
				ret.setCurrentValue("REQUEST_AGAIN");
			}
			else {
				try {
					if (property.getCurrentValue().isEmpty()) {
						return;
					}
					ChartContent chartContent = new ObjectMapper().readValue(property.getCurrentValue(), ChartContent.class);
					int point = Integer.valueOf(ext);
					int allSize = chartContent.getY().length;
					double step = (double)allSize / (double)point;
					String[] y = new String[point];
					for (int i = 0; i < point; i++) {
						y[i] = chartContent.getY()[(int)((double)i*step)];
					}
					chartContent.setY(y);
					ret.setCurrentValue(new ObjectMapper().writeValueAsString(chartContent));
				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		else if (property.isList()) {
			ret.setCurrentValue(property.getSelectedListTitle());
			ret.setCurrentSelectionId(property.getCurrentValue());
		}
		else {
			ret.setCurrentValue(property.getCurrentValue());
		}
	}
	
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

	public static List<JsProperty> convert(List<RuntimeProperty> allProperties) {
		List<JsProperty> ret = new ArrayList<>();
		allProperties.forEach(prop -> ret.add(new JsProperty(prop, "")));
		return ret;
	}
	
}

package jp.silverbullet.web.ui.part2;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jp.silverbullet.web.KeyValue;
import jp.silverbullet.web.ui.part2.UiBuilder.PropertyField;

public class WidgetBase {

	public WidgetType type = WidgetType.StaticText;
	public int x = -1;
	public int y = -1;
	public String id = "";
	public String subId = "";
	public PropertyField field = PropertyField.NONE;
	public List<KeyValue> css = new ArrayList<>();
	public String text;
	public String widgetId;
	
	public WidgetBase() {}
	
	public WidgetBase(WidgetType type) {
		this.type = type;
	}
	
	public WidgetBase(WidgetType type, String id) {
		this.type = type;
		this.id = id;
	}
	
	public WidgetBase(WidgetType type, String id, PropertyField field) {
		this.type = type;
		this.id = id;
		this.field = field;
	}

	public WidgetBase(WidgetType type, String id, String subId, PropertyField field) {
		this.type = type;
		this.id = id;
		this.subId = subId;
		this.field = field;
	}
	
	public WidgetBase position(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public WidgetBase css(String property, String value) {
		this.css.add(new KeyValue(property, value));
		return this;
	}

	public WidgetBase field(PropertyField value) {
		this.field = value;
		return this;
	}

	public WidgetBase optionId(String optionId) {
		this.subId = optionId;
		return this;
	}

	public WidgetBase text(String text) {
		this.text = text;
		return this;
	}
	
}

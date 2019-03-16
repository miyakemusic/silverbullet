package jp.silverbullet.web.ui.part2;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	
	@JsonIgnore
	public String widgetId;
	
	@JsonIgnore
	protected UiBuilderListener listener;
	
	public WidgetBase() {}
	
	public WidgetBase(WidgetType type) {
		this.type = type;
	}
	
	public WidgetBase(WidgetType type, String id) {
		this.type = type;
		this.id = id;
	}
	
	public WidgetBase(WidgetType type, String id, PropertyField field, UiBuilderListener listener) {
		this.type = type;
		this.id = id;
		this.field = field;
		this.listener = listener;
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

	public WidgetBase css(String key, String value) {
		boolean updated = false;
		for (KeyValue kv : this.css) {
			if (kv.getKey().equals(key)) {
				kv.setValue(value);
				updated = true;
				break;
			}
		}
		if (!updated) {
			this.css.add(new KeyValue(key, value));
		}
		if (this.listener != null) {
			this.listener.onCssUpdate(this.widgetId, key, value);
		}
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

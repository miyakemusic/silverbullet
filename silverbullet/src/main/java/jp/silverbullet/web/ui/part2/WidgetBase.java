package jp.silverbullet.web.ui.part2;

import java.util.ArrayList;
import java.util.List;

import jp.silverbullet.web.KeyValue;
import jp.silverbullet.web.ui.part2.UiBuilder.ProprtyField;

public class WidgetBase {

	public int width = -1;
	public int height = -1;
	public WidgetType type;
	public int x = -1;
	public int y = -1;
	public String id;
	public String subId;
	public ProprtyField field;
	public List<KeyValue> css = new ArrayList<>();
	
	public WidgetBase() {}
	
	public WidgetBase(WidgetType type) {
		this.type = type;
	}
	
	public WidgetBase(WidgetType type, String id) {
		this.type = type;
		this.id = id;
	}
	
	public WidgetBase(WidgetType type, String id, ProprtyField field) {
		this.type = type;
		this.id = id;
		this.field = field;
	}

	public WidgetBase(WidgetType type, String id, String subId, ProprtyField field) {
		this.type = type;
		this.id = id;
		this.subId = subId;
		this.field = field;
	}
	
	public WidgetBase size(int width, int heigth) {
		this.width = width;
		this.height = heigth;
		return this;
	}
	
	public WidgetBase position(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}
	public void css(String property, String value) {
		this.css.add(new KeyValue(property, value));
	}
}

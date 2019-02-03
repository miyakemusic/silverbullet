package jp.silverbullet.web.ui.part2;

import java.util.ArrayList;
import java.util.List;

import jp.silverbullet.web.KeyValue;
import jp.silverbullet.web.ui.part2.UiBuilder.ProprtyElement;

public class WidgetBase {

	public int width;
	public int height;
	public WidgetType type;
	public int x;
	public int y;
	public String id;
	public ProprtyElement field;
	public List<KeyValue> css = new ArrayList<>();
	
	public WidgetBase() {}
	public WidgetBase(WidgetType type, String id, ProprtyElement field) {
		this.type = type;
		this.id = id;
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

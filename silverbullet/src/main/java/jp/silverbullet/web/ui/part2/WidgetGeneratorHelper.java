package jp.silverbullet.web.ui.part2;

import java.util.ArrayList;
import java.util.List;

import jp.silverbullet.property2.PropertyDef2;
import jp.silverbullet.property2.PropertyHolder2;

public class WidgetGeneratorHelper {

	private PropertyHolder2 propertiesHolder;
	
	public WidgetGeneratorHelper(PropertyHolder2 propertiesHolder2) {
		this.propertiesHolder = propertiesHolder2;
	}

	public WidgetBase generateToggleButton(String id, Pane pane) {
		WidgetHolder widgetHolder = new WidgetHolder();
		
		PropertyDef2 prop =  this.propertiesHolder.get(id);
		for (String  eid : prop.getOptions().keySet()) {
			widgetHolder.add(pane.createToggleButton(id, eid));
		}
		
		return widgetHolder;
	}

}


class WidgetHolder extends WidgetBase {
	private List<WidgetBase> widgets = new ArrayList<>();
	
	public void add(WidgetBase widget) {
		this.widgets.add(widget);
	}

	@Override
	public WidgetBase size(int width, int heigth) {
		for (WidgetBase widget : this.widgets) {
			widget.size(width, heigth);
		}
		return this;
	}

	@Override
	public WidgetBase css(String property, String value) {
		for (WidgetBase widget : this.widgets) {
			widget.css(property, value);
		}
		return this;
	}
	
}
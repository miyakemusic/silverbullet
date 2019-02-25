package jp.silverbullet.web.ui.part2;

import java.util.ArrayList;
import java.util.List;

import jp.silverbullet.property2.PropertyDef2;
import jp.silverbullet.property2.PropertyHolder2;
import jp.silverbullet.web.ui.part2.UiBuilder.Layout;
import jp.silverbullet.web.ui.part2.UiBuilder.PropertyField;

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

	public void generateTitledSetting(String id, Pane pane) {
		Pane subPane = pane.createPane(Layout.HORIZONTAL);
		subPane.css("border-width", "1px").css("border-color", "lightgray").css("border-style", "solid").css("padding", "5px");
		subPane.createLabel(id, PropertyField.TITLE);
		subPane.createStaticText(":");
		PropertyDef2 prop = this.propertiesHolder.get(id);
		
		if (prop.isList()) {
			subPane.createComboBox(id);
		}
		else if (prop.isNumeric()) {
			subPane.createTextField(id, PropertyField.VALUE);
			subPane.createLabel(id, PropertyField.UNIT);
		}
		else if (prop.isBoolean()) {
			subPane.createButton(id);
		}
		else {
			subPane.createLabel(id, PropertyField.VALUE);
		}
	}

}


class WidgetHolder extends WidgetBase {
	private List<WidgetBase> widgets = new ArrayList<>();
	
	public void add(WidgetBase widget) {
		this.widgets.add(widget);
	}

	@Override
	public WidgetBase css(String property, String value) {
		for (WidgetBase widget : this.widgets) {
			widget.css(property, value);
		}
		return this;
	}
	
}
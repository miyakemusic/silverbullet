package jp.silverbullet.web.ui.part2;

import jp.silverbullet.web.ui.part2.UiBuilder.PropertyField;

public class ComboBox extends WidgetBase {

	public ComboBox() {}
	
	public ComboBox(String id, PropertyField field) {
		super(WidgetType.ComboBox, id, field);
	}

}

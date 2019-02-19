package jp.silverbullet.web.ui.part2;

import jp.silverbullet.web.ui.part2.UiBuilder.PropertyField;

public class CheckBox extends WidgetBase {
	public CheckBox(String id) {
		super(WidgetType.CheckBox, id, PropertyField.VALUE);
	}

}

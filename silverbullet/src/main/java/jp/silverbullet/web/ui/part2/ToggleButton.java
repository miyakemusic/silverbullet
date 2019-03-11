package jp.silverbullet.web.ui.part2;

import jp.silverbullet.web.ui.part2.UiBuilder.PropertyField;

public class ToggleButton extends WidgetBase {

	public ToggleButton() {}
	public ToggleButton(String id, String elementId) {
		super(WidgetType.ToggleButton, id, elementId, PropertyField.VALUE);
	}

}

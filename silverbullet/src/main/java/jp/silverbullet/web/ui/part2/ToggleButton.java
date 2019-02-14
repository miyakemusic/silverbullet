package jp.silverbullet.web.ui.part2;

import jp.silverbullet.web.ui.part2.UiBuilder.ProprtyField;

public class ToggleButton extends WidgetBase {

	public ToggleButton(String id, String elementId) {
		super(WidgetType.ToggleButton, id, elementId, ProprtyField.VALUE);
	}

}

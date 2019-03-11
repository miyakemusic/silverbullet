package jp.silverbullet.web.ui.part2;

import jp.silverbullet.web.ui.part2.UiBuilder.PropertyField;

public class Label extends WidgetBase {

	public Label() {}
	
	public Label(String id, PropertyField field) {
		super(WidgetType.Label, id, field);
	}

}

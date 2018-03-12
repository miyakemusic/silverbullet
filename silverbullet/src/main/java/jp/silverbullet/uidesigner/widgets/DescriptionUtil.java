package jp.silverbullet.uidesigner.widgets;

import javafx.scene.control.Control;

public class DescriptionUtil {
	static public void applyDescription(Control node, Description description) {
		if (description.isDefined(Description.WIDTH)) {
			node.setMaxWidth(Double.valueOf(description.getValue(Description.WIDTH)));
		}
		if (description.isDefined(Description.HEIGHT)) {
			node.setMaxHeight(Double.valueOf(description.getValue(Description.HEIGHT)));
		}

	}

	static public void applyListIcon(Control node, String value, Description description) {
		if (description.isDefined(Description.LIST_ICONS)) {
			node.setStyle(node.getStyle() + ";-fx-graphic:" + description.getSubValue(Description.LIST_ICONS, value));
		}
	}
}

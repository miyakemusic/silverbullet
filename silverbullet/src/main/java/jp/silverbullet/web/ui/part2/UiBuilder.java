package jp.silverbullet.web.ui.part2;

import java.util.ArrayList;
import java.util.List;

import jp.silverbullet.property2.PropertyHolder2;

public class UiBuilder {
	public List<Pane> panes = new ArrayList<>();
	private PropertyHolder2 propertiesHolder;
	
	public enum ProprtyField {
		VALUE, TITLE, UNIT, STATICTEXT, NONE
	}

	public enum Layout {
		VERTICAL, HORIZONTAL, ABSOLUTE
	}

	public UiBuilder() {

	}

	public Pane createPane(Layout layout) {
		Pane pane = new Pane(layout);
		this.panes.add(pane);
		return pane;
	}

	
}

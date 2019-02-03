package jp.silverbullet.web.ui.part2;

import java.util.ArrayList;
import java.util.List;

public class UiBuilder {
	public List<Pane> panes = new ArrayList<>();
	
	public enum ProprtyElement {
		VALUE, TITLE, UNIT, STATICTEXT, NONE
	}

	public enum Layout {
		VERTICAL, HORIZONTAL, ABSOLUTE
	}

	public Pane createPane(Layout layout) {
		Pane pane = new Pane(layout);
		this.panes.add(pane);
		return pane;
	}

}

package jp.silverbullet.web.ui.part2;

import java.util.ArrayList;
import java.util.List;

import jp.silverbullet.web.ui.part2.UiBuilder.Layout;
import jp.silverbullet.web.ui.part2.UiBuilder.ProprtyField;

public class TabPane extends WidgetBase {
	public List<Pane> panes = new ArrayList<>();
	
	public TabPane() {
		super(WidgetType.TabPane, "", ProprtyField.NONE);
	}
	
	public TabPane(String id) {
		super(WidgetType.TabPane, id, ProprtyField.VALUE);
	}
	
	public Pane createPane(Layout layout) {
		Pane pane = new Pane(layout);
		this.panes.add(pane);
		return pane;
	}

	public Pane createPane(String caption, ProprtyField field, Layout layout) {
		Pane pane = new Pane(caption, field, layout);
		this.panes.add(pane);
		return pane;
	}

}

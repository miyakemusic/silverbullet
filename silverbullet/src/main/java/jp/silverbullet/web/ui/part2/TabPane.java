package jp.silverbullet.web.ui.part2;

import java.util.ArrayList;
import java.util.List;

import jp.silverbullet.web.ui.part2.UiBuilder.Layout;
import jp.silverbullet.web.ui.part2.UiBuilder.ProprtyElement;

public class TabPane extends WidgetBase {
	public List<Pane> panes = new ArrayList<>();
	
	public TabPane() {
		super(WidgetType.TabPane, "", ProprtyElement.NONE);
	}
	
	public TabPane(String id) {
		super(WidgetType.TabPane, id, ProprtyElement.VALUE);
	}
	
	public Pane createPane(Layout layout) {
		Pane pane = new Pane(layout);
		this.panes.add(pane);
		return pane;
	}

	public Pane createPane(String caption, ProprtyElement field, Layout layout) {
		Pane pane = new Pane(caption, field, layout);
		this.panes.add(pane);
		return pane;
	}

}

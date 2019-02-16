package jp.silverbullet.web.ui.part2;

import jp.silverbullet.web.ui.part2.UiBuilder.Layout;

public class Tab extends Pane {

	public String title;
	public int tabIndex;

	public Tab(Layout layout, int tabIndex) {
		super(WidgetType.Tab, layout);
		this.tabIndex = tabIndex;
	}

	public Tab id(String id, String subId) {
		this.id = id;
		this.subId = subId;
		return this;
	}

	public Tab title(String title) {
		this.title = title;
		return this;
	}

}

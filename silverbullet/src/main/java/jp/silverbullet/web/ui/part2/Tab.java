package jp.silverbullet.web.ui.part2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Tab extends Pane {

	public String title;
	public int tabIndex;

	public Tab() {}
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

package jp.silverbullet.web.ui.part2;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;


@XmlRootElement
public class UiBuilder {
	private WidgetIdManager widgetIdManager = null;

	public Pane pane = null;
	private UiBuilderListener listener;
	public enum PropertyField {
		VALUE, TITLE, UNIT, STATICTEXT, NONE
	}
	
	public UiBuilder() {
		widgetIdManager = new WidgetIdManager();
		pane = new Pane(widgetIdManager).type(WidgetType.Pane).layout(Layout.VERTICAL).css("position", "fixed");
	}

	public Pane getRootPane() {
		return pane;
	}

	public void nameAll() {
		if (this.widgetIdManager == null) {
			widgetIdManager = new WidgetIdManager();
		}
		this.pane.applyWidgetId(widgetIdManager);
	}

	public Pane getWidget(String divid) {
		return widgetIdManager.get(divid);
	}

	public void addListener(UiBuilderListener uiBuilderListener) {
		this.listener = uiBuilderListener;
		pane.setListener(uiBuilderListener);
	}
	
	@JsonIgnore
	public UiBuilderListener getListener() {
		return this.listener;
	}

	public Pane getParentOf(String divid) {
		return pane.getParent(divid);
	}
}

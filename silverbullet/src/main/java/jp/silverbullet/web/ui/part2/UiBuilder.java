package jp.silverbullet.web.ui.part2;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UiBuilder {
	private WidgetIdManager widgetIdManager = new WidgetIdManager();
	public List<Pane> panes = new ArrayList<>();
	public enum PropertyField {
		VALUE, TITLE, UNIT, STATICTEXT, NONE
	}

	
	public UiBuilder() {

	}

	public Pane createPane(Layout layout) {
		Pane pane = new Pane(layout, widgetIdManager);
		this.panes.add(pane);
		return pane;
	}

	public void nameAll() {
		for (Pane pane : panes) {
			pane.applyWidgetId(widgetIdManager);
		}
	}

	public WidgetBase getWidget(String divid) {
		return widgetIdManager.get(divid);
	}
}

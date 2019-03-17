package jp.silverbullet.web.ui.part2;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;


@XmlRootElement
public class UiBuilder {
	private WidgetIdManager widgetIdManager = new WidgetIdManager();
	
	public List<Pane> panes = new ArrayList<>();

	private UiBuilderListener listener;
	public enum PropertyField {
		VALUE, TITLE, UNIT, STATICTEXT, NONE
	}

	
	public UiBuilder() {

	}

	public Pane createPane(Layout layout) {
		Pane pane = new Pane(layout, widgetIdManager, listener);
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

	public void addListener(UiBuilderListener uiBuilderListener) {
		this.listener = uiBuilderListener;
		for (Pane pane : panes) {
			pane.setListener(uiBuilderListener);
		}
	}
	
	@JsonIgnore
	public UiBuilderListener getListener() {
		return this.listener;
	}
}

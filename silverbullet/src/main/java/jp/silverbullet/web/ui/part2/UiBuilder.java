package jp.silverbullet.web.ui.part2;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;


@XmlRootElement
public class UiBuilder {
	private WidgetIdManager widgetIdManager = new WidgetIdManager();
	
//	public List<Pane> panes = new ArrayList<>();
	private Pane pane = null;
	private UiBuilderListener listener;
	public enum PropertyField {
		VALUE, TITLE, UNIT, STATICTEXT, NONE
	}

	
	public UiBuilder() {
		pane = new Pane(widgetIdManager).type(WidgetType.Pane).layout(Layout.VERTICAL);
	}

	public Pane getRootPane() {
		return pane;
	}

	public void nameAll() {
		pane.applyWidgetId(widgetIdManager);
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
}

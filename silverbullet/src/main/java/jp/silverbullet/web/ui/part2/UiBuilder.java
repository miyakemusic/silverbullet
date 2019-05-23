package jp.silverbullet.web.ui.part2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;


@XmlRootElement
public class UiBuilder {
	private WidgetIdManager widgetIdManager = null;

	//public Pane pane = null;
	public Map<String, Pane> panes = new HashMap<>();
	
	private UiBuilderListener listener;
	public enum PropertyField {
		VALUE, TITLE, UNIT, STATICTEXT, NONE
	}
	
	public UiBuilder() {
		widgetIdManager = new WidgetIdManager();

		createRoot();
	}

	public void createRoot() {
		Pane pane = new Pane(widgetIdManager).type(WidgetType.Pane).layout(Layout.VERTICAL)
				.css("position", "absolute")
				.css("width", "800").css("height", "600")
				.css("border-style", "dashed").css("border-width", "1px");	
		
		pane.setListener(this.listener);
		
		while(true) {
			String candName = "Undefined" + (int)(Math.random()*10);
			boolean ok = true;
			for (String name : this.panes.keySet()) {
				if (name.equals(candName)) {
					ok = false;
					break;
				}
			}
			if (ok) {
				this.panes.put(candName, pane);
				break;
			}
		}
		
	}
	public Pane getRootPane() {
		return panes.values().iterator().next();
	}


	public Pane getRootPane(String root, boolean link) {
		Pane ret = null;
		if (root == null || root.isEmpty()) {
			ret = this.getRootPane();
		}
		else {
			ret = panes.get(root);
		}
		
		if (link) {
			ret = new LinkResolver(panes).resolve(ret);
		}
		
		return ret;
	}
	
	public void nameAll() {
		if (this.widgetIdManager == null) {
			widgetIdManager = new WidgetIdManager();
		}
		
		this.panes.values().forEach(pane -> pane.applyWidgetId(widgetIdManager));
	}

	public Pane getWidget(String divid) {
		return widgetIdManager.get(divid);
	}

	public void addListener(UiBuilderListener uiBuilderListener) {
		this.listener = uiBuilderListener;
		this.panes.values().forEach(pane -> pane.setListener(uiBuilderListener));
	}
	
	@JsonIgnore
	public UiBuilderListener getListener() {
		return this.listener;
	}

	public Pane getParentOf(String divid) {
		for (Pane pane : this.panes.values()) {
			Pane parent = pane.getParent(divid);
			if (parent != null) {
				return parent;
			}
		}
		return null;
		//return pane.getParent(divid);
	}
	
	public List<String> getRootList() {
		List<String> ret = new ArrayList<String>(this.panes.keySet());
		Collections.sort(ret);
		return ret;
	}

	public void changeRootName(String oldName, String newName) {
		Pane pane = this.panes.get(oldName);
		this.panes.put(newName, pane);
		this.panes.remove(oldName);
	}

}

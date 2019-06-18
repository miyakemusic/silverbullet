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
		VALUE, TITLE, UNIT, MIN, MAX, STATICTEXT, NONE
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
		
		updateVolatileInfo(ret);
		
		if (link) {
			ret = new LinkResolver(panes).resolve(ret);
		}
		
		return ret;
	}
	
	private void updateVolatileInfo(Pane pane) {
		// decides dialog size
		new PaneWalkThrough() {
			@Override
			protected boolean handle(Pane widget, Pane parent2) {
				widget.volatileInfo.clear();
				if (widget.type.equals(WidgetType.Dialog)) {
					if (widget.optional.startsWith("$CONTENT")) {
						String linkId = widget.optional.split("=")[1];
						Pane content = getPaneByName(linkId, false);

						widget.volatileInfo.add("width="  + String.valueOf((Integer.valueOf(content.css("width")) + 100)));
						widget.volatileInfo.add("height="  + String.valueOf((Integer.valueOf(content.css("height")) + 150)));
					}
				}

				return true;
			}
		}.walkThrough(pane, null);
		

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

	public Pane getPaneByName(String name, boolean link) {
		for (Pane pane : this.panes.values()) {
			Pane ret = pane.findLink(name);
			
			if (ret != null) {
				if (link) {
					ret = new LinkResolver(panes).resolve(ret);
				}
				return ret;
			}
		}
		return null;
	}

	public List<String> getFieldTypes() {
		List<String> ret = new ArrayList<>();
		for (PropertyField field : PropertyField.values()) {
			ret.add(field.toString());
		}
		
		return ret;
	}

}

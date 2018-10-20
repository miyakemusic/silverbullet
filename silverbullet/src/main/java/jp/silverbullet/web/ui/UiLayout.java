package jp.silverbullet.web.ui;

import java.util.List;
import java.util.Map;

import jp.silverbullet.SvProperty;
import jp.silverbullet.web.WebSocketBroadcaster;

public class UiLayout {

	public JsWidget root;
	private PropertyGetter propertyGetter;
	public UiLayout() {
		root = createRoot();
	}
	
	private void fireEvent() {
		WebSocketBroadcaster.getInstance().sendMessage("layoutChanged");
	}

	public void setPropertyGetter(PropertyGetter propertyGetter) {
		this.propertyGetter = propertyGetter;
	}

	private JsWidget createRoot() {
		JsWidget root = new JsWidget();
		root.setWidgetType(JsWidget.PANEL);
		root.setWidth("800");
		root.setHeight("400");
		root.setLayout(JsWidget.ABSOLUTELAYOUT);
		return root;
	}
	
	private JsWidget createPanel() {
		JsWidget widget = new JsWidget();
		widget.setWidgetType(JsWidget.PANEL);
		return widget;
	}
	
	public JsWidget getRoot() {
		return root;
	}

	public void addWidget(String div, List<String> ids) {
		int unique = extractUnique(div);
		JsWidget panel = null;
		panel = getDiv(unique);
		
		for (String id : ids) {
			SvProperty property = propertyGetter.getProperty(id);
			String type = property.getType();
			JsWidget widget = new JsWidget();
			widget.setId(id);
			
			if (type.equals(SvProperty.DOUBLE_PROPERTY) || type.equals(SvProperty.TEXT_PROPERTY) || type.equals(SvProperty.LONG_PROPERTY)) {
				widget.setWidgetType(JsWidget.TEXTFIELD);
			}
			else if (type.equals(SvProperty.LIST_PROPERTY)) {
				if (property.getListDetail().size() < 3) {
					widget.setWidgetType(JsWidget.RADIOBUTTON);
				}
				else {
					widget.setWidgetType(JsWidget.COMBOBOX);
				}
			}
			else if (type.equals(SvProperty.BOOLEAN_PROPERTY)) {
				widget.setWidgetType(JsWidget.CHECKBOX);
			}
			else if (type.equals(SvProperty.ACTION_PROPERTY)) {
				widget.setWidgetType(JsWidget.ACTIONBUTTON);
			}
			else if (type.equals(SvProperty.CHART_PROPERTY)) {
				widget.setWidgetType(JsWidget.CHART);
			}	
			else if (type.equals(SvProperty.TABLE_PROPERTY)) {
				widget.setWidgetType(JsWidget.TABLE);
			}	
			panel.addChild(widget);
		}
		fireEvent();
	}
	
	private JsWidget getDiv(int unique) {
		JsWidget panel;
		if (unique == root.getUnique()) {
			panel  = root;
		}
		else {
			panel =  findDiv(root, unique);
		}
		return panel;
	}
	
	private JsWidget findDiv(JsWidget parent, int unique) {
		for (JsWidget jsWidget : parent.getChildren()) {
			if (jsWidget.getUnique() == unique) {
				return jsWidget;
			}
			JsWidget ret = findDiv(jsWidget, unique);
			if (ret != null) {
				return ret;
			}
		}
		return null;
	}
	
	private int extractUnique(String div) {
		String[] tmp = div.split("-");
		int unique = Integer.valueOf(tmp[tmp.length-1]);
		return unique;
	}
	
	public void move(String div, String x, String y) {
		int unique = extractUnique(div);
		JsWidget widget = this.getDiv(unique);
		widget.setLeft(x);
		widget.setTop(y);
		
		fireEvent();
	}
	
	public void resize(String div, String width, String height) {
		int unique = extractUnique(div);
		JsWidget widget = this.getDiv(unique);
		widget.setWidth(width);
		widget.setHeight(height);
		
		fireEvent();
	}

	public void addPanel(String div) {
		int unique = extractUnique(div);
		JsWidget panel = createPanel();
		panel.setWidth("300");
		panel.setHeight("200");
		this.getDiv(unique).addChild(panel);
	}

	public void addTab(String div) {
		int unique = extractUnique(div);
		JsWidget panel = new JsWidget();
		panel.setWidgetType(JsWidget.TAB);
		panel.setWidth("300");
		panel.setHeight("200");
		this.getDiv(unique).addChild(panel);
	}
	
//	public void setLayout(String div, String layout) {
//		JsWidget panel = getWidget(div);
//		panel.setLayout(layout);
//		this.fireEvent();
//	}

	private JsWidget getWidget(String div) {
		int unique = extractUnique(div);
		JsWidget panel =  this.getDiv(unique);
		return panel;
	}

	public void remove(String div) {
		removeDiv(root, extractUnique(div));
		fireEvent();
	}
	
	private boolean removeDiv(JsWidget parent, int unique) {
		for (JsWidget jsWidget : parent.getChildren()) {
			if (jsWidget.getUnique() == unique) {
				parent.getChildren().remove(jsWidget);
				return true;
			}
			boolean ret = removeDiv(jsWidget, unique);
			if (ret == true) {
				return true;
			}
		}
		return false;
	}

//	public void setWidgetType(String div, String widgetType) {
//		JsWidget panel = getWidget(div);
//		panel.setWidgetType(widgetType);
//		this.fireEvent();
//	}

//	public void setSyle(String div, String style) {
//		JsWidget panel = getWidget(div);
//		panel.setStyleClass(style);
//		this.fireEvent();
//	}

//	public void setCss(String div, String css) {
//		JsWidget panel = getWidget(div);
//		panel.setCss(css);
//		this.fireEvent();	
//	}

	public void setId(String div, String id) {
		JsWidget panel = getWidget(div);
		panel.setId(id);
		this.fireEvent();
	}

	public void addDialog(String div, String id) {
		int unique = extractUnique(div);
		JsWidget panel = getDiv(unique);
		JsWidget dialog = new JsWidget();
		dialog.getCustom().put("id", id);
		dialog.setWidgetType(JsWidget.GUI_DIALOG);
		panel.addChild(dialog);
		this.fireEvent();
	}
	
	private JsWidget findPanel(JsWidget parent, String id) {
		for (JsWidget jsWidget : parent.getChildren()) {
			String gui_id = jsWidget.getCustom().get(CustomProperties.GUI_ID);
			if (gui_id != null && gui_id.equals(id)) {
				return jsWidget;
			}
			JsWidget ret = findPanel(jsWidget, id);
			if (ret != null) {
				return ret;
			}
		}
		return null;
	}
	public JsWidget getSubTree(String id) {
		if (this.root.getCustom(CustomProperties.GUI_ID).equals(id)) {
			return root;
		}
		return this.findPanel(this.root, id);
	}

	public void clear() {
		this.root = createRoot();
	}

//	public void setPresentation(String div, String presentation) {
//		JsWidget panel = getWidget(div);
//		panel.setPresentation(presentation);
//		this.fireEvent();
//	}

//	public void setFontSize(String div, String fontSize) {
//		JsWidget panel = getWidget(div);
//		panel.setFontsize(fontSize);
//		this.fireEvent();
//	}
	
	public void setCustom(String div, Map<String, String> custom) {
		JsWidget panel = getWidget(div);
		panel.setCustom(custom);
		this.fireEvent();
	}

	public void cutPaste(String newBaseDiv, String itemDiv) {
		JsWidget item = this.getWidget(itemDiv);
		this.remove(itemDiv);
		this.getWidget(newBaseDiv).addChild(item);
		this.fireEvent();
	}

	public void setCustomElement(String div, String customId, String customValue) {
		JsWidget panel = getWidget(div);
		panel.getCustom().put(customId, customValue);
		this.fireEvent();
	}


	public void changeId(String prevId, String newId) {
		new WalkThrough(this.getRoot())	{
			@Override
			protected void handle(JsWidget jsWidget) {
				if (jsWidget.getId().equals(prevId)) {
					jsWidget.setId(newId);
				}
			}
		};
	}

	public void updateProperty(String div, String propertyType, String value) {
		JsWidget panel = getWidget(div);
		panel.setField(propertyType, value);
		this.fireEvent();
	}
}

abstract class WalkThrough {
	public WalkThrough(JsWidget parent) {
		walk(parent);
	}
	
	public void walk(JsWidget parent) {
		for (JsWidget jsWidget : parent.getChildren()) {
			handle(jsWidget);
			walk(jsWidget);
		}			
	}
	abstract protected void handle(JsWidget jsWidget);
}

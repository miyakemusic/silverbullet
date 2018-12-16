package jp.silverbullet.web.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.SvProperty;
import jp.silverbullet.web.WebSocketBroadcaster;
import jp.silverbullet.web.WebSocketMessage;

public class UiLayout {

	public JsWidget root;
	private PropertyGetter propertyGetter;
	public UiLayout() {
		root = createRoot();
	}

	public UiLayout(PropertyGetter propertyGetter2) {
		root = createRoot();
		this.propertyGetter = propertyGetter2;
	}

	private void fireEvent() {
		try {
			String str = new ObjectMapper().writeValueAsString(new WebSocketMessage("DESIGN", "layoutChanged"));
			WebSocketBroadcaster.getInstance().sendMessage(str);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void setPropertyGetter(PropertyGetter propertyGetter) {
		this.propertyGetter = propertyGetter;
	}

	private JsWidget createRoot() {
		JsWidget root = new JsWidget();
		root.setWidgetType(JsWidget.ROOT);
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

	public void addRegisterShortcut(String div, String register) {
		int unique = extractUnique(div);
		JsWidget panel = new JsWidget();
		panel.setWidgetType(JsWidget.REGISTERSHORTCUT);
		panel.getCustom().put(CustomProperties.REGISTER_SHORTCUT, register);
		panel.setWidth("100");
		panel.setHeight("50");
		this.getDiv(unique).addChild(panel);
	}

	public JsWidget getWidget(String div) {
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
		if (this.root.getCustomElement(CustomProperties.GUI_ID).equals(id)) {
			return root;
		}
		return this.findPanel(this.root, id);
	}

	public void clear() {
		this.root = createRoot();
	}

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

	public void copyPaste(String newBaseDiv, String itemDiv) {
		JsWidget item = this.getWidget(itemDiv);
		this.getWidget(newBaseDiv).addChild(item.clone());
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
		panel.setField(propertyType, value, String.class);
		this.fireEvent();
	}

	public void updateBooleanProperty(String div, String propertyType, Boolean value) {
		JsWidget panel = getWidget(div);
		panel.setField(propertyType, value, Boolean.class);
		this.fireEvent();
	}

	public void addArray(String div) {
		JsWidget widget = getWidget(div);
		if (widget.getWidgetType().equals(JsWidget.PANEL)) {
			String id = widget.getChildren().get(0).getId();
			int size = this.propertyGetter.getProperty(id).getProperty().getSize();
			JsWidget parent = new UiParent(widget).getParent();
			

			for (int i = 1; i < size; i++) {
				JsWidget clonePanel = widget.clone();
				clonePanel.setCustomElemnt(CustomProperties.COPIED, "true");
				for (JsWidget child : clonePanel.getChildren()) {
					child.setIndex(i);
				}
				parent.addChild(clonePanel);
			}
		}
		else {
			String id = widget.getId();
			JsWidget parent = new UiParent(widget).getParent();
			for (int i = 1; i < this.propertyGetter.getProperty(id).getProperty().getSize(); i++) {
				JsWidget clone = widget.clone();
				clone.setIndex(i);
				parent.addChild(clone);
			}
		}
	}
	
	public List<Integer> buildDynamicArray() {
		List<Integer> changed = new ArrayList<>();
		new WalkThrough(this.getRoot()) {
			@Override
			protected void handle(JsWidget jsWidget) {
				if (!jsWidget.getWidgetType().equals(JsWidget.PANEL)) {
					return;
				}

				if (jsWidget.getCustomElement(CustomProperties.ARRAY).equals("true")) {
					// clear copied widgets
					Iterator<JsWidget> it = jsWidget.getChildren().iterator();
					while(it.hasNext()) {
						JsWidget child = it.next();
						if (child.getCustomElement(CustomProperties.COPIED).equals("true")) {
							it.remove();
						}
					}
					
					// copy widgets
					JsWidget masterPanel = jsWidget.getChildren().get(0).clone();
					SvProperty property = propertyGetter.getProperty(masterPanel.getChildren().get(0).getId());
					int size = property.getProperty().getSize();
					for (int i = 1; i < size; i++) {
						JsWidget child = masterPanel.clone();
						child.setIndex(i);
						child.setCustomElemnt(CustomProperties.COPIED, "true");
						jsWidget.addChild(child);
					}
					
					changed.add(jsWidget.getUnique());
				}
			}
		};
		return changed;
	}
	
	class UiParent {
		private JsWidget parent = null;
		public UiParent(JsWidget child) {
			
			new WalkThrough(getRoot()) {
				@Override
				protected void handle(JsWidget jsWidget) {
					if (jsWidget.getChildren().contains(child)) {
						parent = jsWidget;
						return;
					}
				}
			};	
		}
		public JsWidget getParent() {
			return parent;
		}
		
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

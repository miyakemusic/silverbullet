package jp.silverbullet.web.ui;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.web.UiLayoutListener;

public class UiLayout {

	public JsWidget root;
	private Set<JsWidget> dynamicWidgets = new HashSet<>();
	
	private PropertyGetter propertyGetter;
	private Set<UiLayoutListener> listeners = new HashSet<>();
	public UiLayout() {
		root = createRoot();
	}

	public UiLayout(PropertyGetter propertyGetter2) {
		root = createRoot();
		this.propertyGetter = propertyGetter2;
	}

	public void fireLayoutChange(String div) {	
		this.listeners.forEach(listener -> listener.onLayoutChange(div, ""));
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
			RuntimeProperty property = propertyGetter.getProperty(id);
//			PropertyType2 type = property.getType();
			JsWidget widget = new JsWidget();
			widget.setId(id);
			
			if (property.isNumericProperty() || property.isText()) {
				widget.setWidgetType(JsWidget.TEXTFIELD);
			}
			else if (property.isList()) {
				if (property.getOptionIds().size() < 3) {
					widget.setWidgetType(JsWidget.RADIOBUTTON);
				}
				else {
					widget.setWidgetType(JsWidget.COMBOBOX);
				}
			}
			else if (property.isBoolean()) {
				widget.setWidgetType(JsWidget.CHECKBOX);
			}
			else if (property.isAction()) {
				widget.setWidgetType(JsWidget.ACTIONBUTTON);
			}
			else if (property.isChartProperty()) {
				widget.setWidgetType(JsWidget.CHART);
			}	
			else if (property.isTable()) {
				widget.setWidgetType(JsWidget.TABLE);
			}	
			panel.addChild(widget);
		}
		fireLayoutChange(div);
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
		
		fireLayoutChange(div);
	}
	
	public void resize(String div, String width, String height) {
		int unique = extractUnique(div);
		JsWidget widget = this.getDiv(unique);
		widget.setWidth(width);
		widget.setHeight(height);
	
		fireLayoutChange(div);
//		fireLayoutChange(String.valueOf(widget.getParentDiv()));
	}

	public JsWidget addPanel(String div) {
		int unique = extractUnique(div);
		JsWidget panel = createPanel();
		panel.setWidth("300");
		panel.setHeight("200");
		panel.setLayout(JsWidget.VERTICALLAYOUT);
		this.getDiv(unique).addChild(panel);
		
		this.fireLayoutChange(div);
		return panel;
	}

	public void addTab(String div) {
		int unique = extractUnique(div);
		JsWidget panel = new JsWidget();
		panel.setWidgetType(JsWidget.TAB);
		panel.setWidth("300");
		panel.setHeight("200");
		this.getDiv(unique).addChild(panel);
		this.fireLayoutChange(div);
	}

	public void addRegisterShortcut(String div, String register) {
		int unique = extractUnique(div);
		JsWidget panel = new JsWidget();
		panel.setWidgetType(JsWidget.REGISTERSHORTCUT);
		panel.getCustom().put(CustomProperties.REGISTER_SHORTCUT, register);
		panel.setWidth("100");
		panel.setHeight("50");
		this.getDiv(unique).addChild(panel);
		
		this.fireLayoutChange(div);
	}

	public JsWidget getWidget(String div) {
		int unique = extractUnique(div);
		JsWidget panel =  this.getDiv(unique);
		return panel;
	}

	public void remove(String div) {
		removeDiv(root, extractUnique(div));
//		fireLayoutChange(div);
	}
	
	private boolean removeDiv(JsWidget parent, int unique) {
		for (JsWidget jsWidget : parent.getChildren()) {
			if (jsWidget.getUnique() == unique) {
				parent.getChildren().remove(jsWidget);
				this.fireLayoutChange(parent.getUniqueText());
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
		this.fireLayoutChange(div);
	}

	public JsWidget addDialog(String div) {
		int unique = extractUnique(div);
		JsWidget panel = getDiv(unique);
		JsWidget dialog = new JsWidget();
		//dialog.getCustom().put("id", id);
//		dialog.setCustomElemnt(CustomProperties.TARGET_GUI_ID, id);
		dialog.setWidgetType(JsWidget.GUI_DIALOG);
		panel.addChild(dialog);
		this.fireLayoutChange(div);
		return dialog;
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
		this.fireLayoutChange(div);
	}

	public void cutPaste(String newBaseDiv, String itemDiv) {
		JsWidget item = this.getWidget(itemDiv);
		this.remove(itemDiv);
		this.getWidget(newBaseDiv).addChild(item);
		this.fireLayoutChange(newBaseDiv);
		this.fireLayoutChange(itemDiv);
	}

	public void copyPaste(String newBaseDiv, String itemDiv) {
		JsWidget item = this.getWidget(itemDiv);
		this.getWidget(newBaseDiv).addChild(item.clone());
		this.fireLayoutChange(newBaseDiv);
		this.fireLayoutChange(itemDiv);
	}
	
	public void setCustomElement(String div, String customId, String customValue) {
		JsWidget panel = getWidget(div);
		//panel.getCustom().put(customId, customValue);
		panel.setCustomElemnt(customId, customValue);
		
		if (customId.equals(CustomProperties.ARRAY)) {
			if (customValue.equals(CustomProperties.True)) {
				this.dynamicWidgets.add(panel);
			}
			else {
				this.dynamicWidgets.remove(panel);
			}
		}
		this.fireLayoutChange(div);
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
		this.fireLayoutChange(div);
	}

	public void updateBooleanProperty(String div, String propertyType, Boolean value) {
		JsWidget panel = getWidget(div);
		panel.setField(propertyType, value, Boolean.class);
		this.fireLayoutChange(div);
	}

	public void addArray(String div) {
		JsWidget widget = getWidget(div);
		if (widget.getWidgetType().equals(JsWidget.PANEL)) {
			String id = widget.getChildren().get(0).getId();
			int size = this.propertyGetter.getProperty(id).getSize();
			JsWidget parent = getWidget(String.valueOf(widget.getParentDiv()));
			

			for (int i = 1; i < size; i++) {
				JsWidget clonePanel = widget.clone();
				clonePanel.setCustomElemnt(CustomProperties.COPIED, CustomProperties.True);
				for (JsWidget child : clonePanel.getChildren()) {
					child.setIndex(i);
				}
				parent.addChild(clonePanel);
			}
		}
		else {
			String id = widget.getId();
			JsWidget parent = getWidget(String.valueOf(widget.getParentDiv()));
			for (int i = 1; i < this.propertyGetter.getProperty(id).getSize(); i++) {
				JsWidget clone = widget.clone();
				clone.setIndex(i);
				parent.addChild(clone);
			}
		}
	}
	
	public void collectDynamicArrays() {
		new WalkThrough(this.getRoot()) {
			@Override
			protected void handle(JsWidget jsWidget) {
				if (!jsWidget.getWidgetType().equals(JsWidget.PANEL)) {
					return;
				}

				if (jsWidget.getCustomElement(CustomProperties.ARRAY).equals(CustomProperties.True)) {
					dynamicWidgets.add(jsWidget);
				}
			}
		};
	}

	
	private String findChildId(JsWidget child) {
		String ret = "";
		if (child.getId().isEmpty()) {
			if (child.getChildren().size() > 0) {
				ret = findChildId(child.getChildren().get(0));
			}
		}
		else {
			ret = child.getId();
		}
		return ret;
	}
	

	
	public void doAutoDynamicPanel() {
		for (JsWidget jsWidget : this.dynamicWidgets) {
			// count current widgets
			JsWidget masterPanel = jsWidget.getChildren().get(0);
			
			String id = findChildId(masterPanel);
			if (id.isEmpty()) {
				continue;
			}

			RuntimeProperty property = propertyGetter.getProperty(id);
			int size = property.getSize();
			
			if (jsWidget.getChildren().size() == size) {
				return;
			}
			// clear copied widgets
			Iterator<JsWidget> it = jsWidget.getChildren().iterator();
			while(it.hasNext()) {
				JsWidget child = it.next();
				if (child.getCustomElement(CustomProperties.COPIED).equals(CustomProperties.True)) {
					it.remove();
				}
			}
			
			// copy widgets
			for (int i = 1; i < size; i++) {
				JsWidget child = masterPanel.clone();
				child.setIndex(i);
				child.setCustomElemnt(CustomProperties.COPIED, CustomProperties.True);
				jsWidget.addChild(child);
			}
			this.fireLayoutChange(String.valueOf(jsWidget.getUnique()));
		}
	}

	public void addListener(UiLayoutListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(UiLayoutListener listener) {
		this.listeners.remove(listener);
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

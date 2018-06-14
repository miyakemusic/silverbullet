package jp.silverbullet.web.ui;

import java.util.List;

import jp.silverbullet.BuilderFx;
import jp.silverbullet.SvProperty;

public class LayoutDemo {
	private static LayoutDemo instance;
	public JsWidget root;
	public LayoutDemo() {
		root = new JsWidget();
		root.setWidgetType(JsWidget.PANEL);
		root.setWidth("800");
		root.setHeight("400");
		
		JsWidget panel2 = createPanel();
		root.addChild(panel2);
		panel2.setWidth("400");
		panel2.setHeight("200");

		JsWidget panel3 = createPanel();
		panel2.addChild(panel3);
		panel3.setWidth("200");
		panel3.setHeight("100");
		
		root.addChild(createWidget("ID_BAND", JsWidget.COMBOBOX));
		root.addChild(createWidget("ID_STARTWAVELENGTH", JsWidget.TEXTFIELD));
		root.addChild(createWidget("ID_STOPWAVELENGTH", JsWidget.TEXTFIELD));
		
		JsWidget panel = createPanel();
		root.addChild(panel);
		panel.addChild(createWidget("ID_CENTERWAVELENGTH", JsWidget.TEXTFIELD));
		panel.addChild(createWidget("ID_SPANWAVELENGTH", JsWidget.TEXTFIELD));
		

	}
	private JsWidget createPanel() {
		JsWidget widget = new JsWidget();
		widget.setWidgetType(JsWidget.PANEL);
		return widget;
	}
	private JsWidget createWidget(String id, String widgetType) {
		JsWidget widget = new JsWidget();
		widget.setId(id);
		widget.setWidgetType(widgetType);
		return widget;
	}
	public JsWidget getRoot() {
		return root;
	}
	public static LayoutDemo getInstance() {
		if (instance == null) {
			instance = new LayoutDemo();
		}
		return instance;
	}

	public void addWidget(String div, List<String> ids) {
		int unique = extractUnique(div);
		JsWidget panel = null;
		panel = getDiv(div, unique);
		
		for (String id : ids) {
			SvProperty property = BuilderFx.getModel().getBuilderModel().getProperty(id);
			String type = property.getType();
			JsWidget widget = new JsWidget();
			widget.setId(id);
			
			if (type.equals(SvProperty.DOUBLE_PROPERTY) || type.equals(SvProperty.TEXT_PROPERTY)) {
				widget.setWidgetType(JsWidget.TEXTFIELD);
			}
			else if (type.equals(SvProperty.LIST_PROPERTY)) {
				widget.setWidgetType(JsWidget.COMBOBOX);
			}
			else if (type.equals(SvProperty.BOOLEAN_PROPERTY)) {
				widget.setWidgetType(JsWidget.TEXTFIELD);
			}
			else if (type.equals(SvProperty.ACTION_PROPERTY)) {
				widget.setWidgetType(JsWidget.BUTTON);
			}
			
			panel.addChild(widget);
		}
	}
	private JsWidget getDiv(String div, int unique) {
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
		int unique = Integer.valueOf(div.split("-")[1]);
		return unique;
	}
	public void move(String div, String x, String y) {
		int unique = extractUnique(div);
		this.getDiv(div, unique).setLeft(x);
		this.getDiv(div, unique).setTop(y);
	}
	public void resize(String div, String width, String height) {
		int unique = extractUnique(div);
		this.getDiv(div, unique).setWidth(width);
		this.getDiv(div, unique).setHeight(height);
	}
	
}

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
		root.setWidth(800);
		root.setHeight(400);
		
		JsWidget panel2 = createPanel();
		root.addChild(panel2);
		panel2.setWidth(400);
		panel2.setHeight(200);

		JsWidget panel3 = createPanel();
		panel2.addChild(panel3);
		panel3.setWidth(200);
		panel3.setHeight(100);
		
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
		if (unique == root.getUnique()) {
			panel  = root;
		}
		else {
			panel =  findDiv(root, div);
		}
		
		for (String id : ids) {
			String type = BuilderFx.getModel().getBuilderModel().getProperty(id).getType();
			JsWidget widget = new JsWidget();
			widget.setId(id);
			
			if (type.equals(SvProperty.DOUBLE_PROPERTY)) {
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
	
	private JsWidget findDiv(JsWidget parent, String div) {
		int unique = extractUnique(div);
		for (JsWidget jsWidget : parent.getChildren()) {
			if (jsWidget.getUnique() == unique) {
				return jsWidget;
			}
			JsWidget ret = findDiv(jsWidget, div);
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
	
}

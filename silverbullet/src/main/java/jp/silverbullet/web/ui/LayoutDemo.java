package jp.silverbullet.web.ui;

public class LayoutDemo {
	private static LayoutDemo instance;
	public JsWidget root;
	public LayoutDemo() {
		root = new JsWidget();
		root.setWidgetType(JsWidget.Panel);
		root.setWidth(800);
		root.setHeight(400);
		
		root.addChild(createWidth("ID_BAND", JsWidget.COMBOBOX));
		root.addChild(createWidth("ID_STARTWAVELENGTH", JsWidget.TEXTFIELD));
		root.addChild(createWidth("ID_STOPWAVELENGTH", JsWidget.TEXTFIELD));
	}
	private JsWidget createWidth(String id, String widgetType) {
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
	
}

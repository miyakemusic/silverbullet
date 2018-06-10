package jp.silverbullet.web.ui;

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
	
}

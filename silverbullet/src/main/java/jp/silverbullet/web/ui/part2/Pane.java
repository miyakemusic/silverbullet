package jp.silverbullet.web.ui.part2;

import java.util.ArrayList;
import java.util.List;

import jp.silverbullet.property2.PropertyHolder2;
import jp.silverbullet.web.ui.part2.UiBuilder.Layout;
import jp.silverbullet.web.ui.part2.UiBuilder.ProprtyField;

public class Pane extends WidgetBase {

	public Layout layout = Layout.VERTICAL;
	public List<WidgetBase> widgets = new ArrayList<>();
	public String caption;
	public ProprtyField field;

	public Pane(Layout layout) {
		super(WidgetType.Pane, "", ProprtyField.NONE);
		this.layout  = layout;
	}

	public Pane(String caption, ProprtyField field, Layout layout2) {
		this.caption = caption;
		this.field = field;
		this.layout = layout2;
	}

	public Pane(WidgetType type, Layout layout2) {
		super(type);
		this.layout = layout2;
	}

	public ComboBox createComboBox(String id) {
		ComboBox widget = new ComboBox(id, UiBuilder.ProprtyField.VALUE);
		this.widgets.add(widget);
		applyLayout(widget);
		return widget;
	}

	public Pane createPane(Layout layout) {
		Pane pane = new Pane(layout);
		this.widgets.add(pane);
		applyLayout(pane);
		return pane;
	}

	public Label createLabel(String id, ProprtyField field) {
		Label text = new Label(id, field);
		this.widgets.add(text);
		applyLayout(text);
		return text;
	}

	public TextField createTextField(String id, ProprtyField field) {
		TextField textField = new TextField(id, field);
		this.widgets.add(textField);
		applyLayout(textField);
		return textField;
	}

	public TabPane createTabPane() {
		TabPane tabPane = new TabPane();
		this.widgets.add(tabPane);
		applyLayout(tabPane);
		return tabPane;
	}

	public CheckBox createCheckBox(String id) {
		CheckBox checkBox = new CheckBox(id);
		this.widgets.add(checkBox);
		applyLayout(checkBox);
		return checkBox;
	}

	public ToggleButton createToggleButton(String id, String elementId) {
		ToggleButton toggleButton = new ToggleButton(id, elementId);
		this.widgets.add(toggleButton);
		applyLayout(toggleButton);
		return toggleButton;
	}

	private void applyLayout(WidgetBase widget) {
		if (this.layout.compareTo(Layout.HORIZONTAL) == 0) {
			widget.css("display", "inline");
		}
		else if (this.layout.compareTo(Layout.VERTICAL) == 0) {
			widget.css("display", "block");
		}
		else if (this.layout.compareTo(Layout.ABSOLUTE) == 0) {
			
		}
		
	}

	public WidgetBase createToggleButton(String id) {
		return createToggleButton(id, "");
	}

	public StaticText createStaticText(String text) {
		StaticText staticText = new StaticText(text);
		this.widgets.add(staticText);
		applyLayout(staticText);
		return staticText;
	}

}

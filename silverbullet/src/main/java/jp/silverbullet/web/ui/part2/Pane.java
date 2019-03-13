package jp.silverbullet.web.ui.part2;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jp.silverbullet.web.KeyValue;
import jp.silverbullet.web.ui.part2.UiBuilder.PropertyField;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Pane extends WidgetBase {

	public Layout layout = Layout.VERTICAL;
	public List<WidgetBase> widgets = new ArrayList<>();
	public String caption = "";
	public PropertyField field = PropertyField.NONE;
	public int padding = -1;
	public PropertyField titldField = PropertyField.STATICTEXT;

	public Pane() {}
	public Pane(Layout layout) {
		super(WidgetType.Pane, "", PropertyField.NONE);
		this.layout  = layout;
	}

	public Pane(String caption, PropertyField field, Layout layout2) {
		this.caption = caption;
		this.field = field;
		this.layout = layout2;
	}

	public Pane(WidgetType type, Layout layout2) {
		super(type);
		this.layout = layout2;
	}

	public WidgetBase createComboBox(String id) {
		return this.createWidget(WidgetType.ComboBox, id).field(PropertyField.VALUE);
	}

	public WidgetBase createPane(Layout layout) {
		Pane pane = new Pane(layout);
		return this.applyLayout(pane);
	}

	public WidgetBase createLabel(String id, PropertyField field) {
		return createWidget(WidgetType.Label, id).field(field);
	}

	public WidgetBase createTextField(String id, PropertyField field) {
		return createWidget(WidgetType.TextField, id).field(field);
	}

	public WidgetBase createCheckBox(String id) {
		return createWidget(WidgetType.CheckBox, id);
	}

	public WidgetBase createToggleButton(String id, String optionId) {
		return this.createWidget(WidgetType.ToggleButton, id).optionId(optionId).field(PropertyField.VALUE);
	}

	private WidgetBase applyLayout(WidgetBase widget) {
		this.widgets.add(widget);
		if (this.layout.compareTo(Layout.HORIZONTAL) == 0) {
			widget.css("display", "inline-block");
		}
		else if (this.layout.compareTo(Layout.VERTICAL) == 0) {
			widget.css("display", "block");
		}
		else if (this.layout.compareTo(Layout.ABSOLUTE) == 0) {
			
		}
		
		widget.widgetId = createWidgetId();
		return widget;
	}
	
	public WidgetBase createToggleButton(String id) {
		return createToggleButton(id, "");
	}

	public WidgetBase createStaticText(String text) {
		return createWidget(WidgetType.StaticText, "").text(text);
	}

	public WidgetBase createButton(String id) {
		return createWidget(WidgetType.Button, id);
	}

	public Pane condition(String id, String subId) {
		this.id = id;
		this.subId = subId;
		return this;
	}

	public void css(List<KeyValue> css2) {
		for (KeyValue c : css2) {
			this.css.add(c);
		}
	}

	public WidgetBase createChart(String id) {
		return createWidget(WidgetType.Chart, id);
	}

	public WidgetBase createTable(String id) {
		return createWidget(WidgetType.Table, id);
	}

	public Pane title(String id, PropertyField field) {
		this.id = id;
		this.titldField = field;
		return this;
	}

	public WidgetBase createImage(String id) {
		return createWidget(WidgetType.Image, id);
	}

	public WidgetBase createSlider(String id) {
		return createWidget(WidgetType.Slider, id);
	}
	
	private WidgetBase createWidget(WidgetType type2, String id) {
		WidgetBase widget = new WidgetBase(type2, id);
		return applyLayout(widget);
	}
	
	public Pane title(String text) {
		this.caption = text;
		return this;
	}


	private static long widgetIdNumber = 0;
	private static String createWidgetId() {
		return "WID" + widgetIdNumber++;
	}
	
	public void applyWidgetId() {
		this.widgetId = createWidgetId();
		for (WidgetBase widget : this.widgets) {
			if (widget instanceof Pane) {
				Pane subPane = ((Pane)widget);
				namePane(subPane);
			}
		}
		
	}
	private void namePane(Pane pane) {
		pane.widgetId = createWidgetId();
		for (WidgetBase widget : pane.widgets) {
			widget.widgetId = createWidgetId();
		}
	}
}

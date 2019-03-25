package jp.silverbullet.web.ui.part2;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jp.silverbullet.web.KeyValue;
import jp.silverbullet.web.ui.part2.UiBuilder.PropertyField;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Pane {

	public Layout layout = Layout.VERTICAL;
	public List<Pane> widgets = new ArrayList<>();
	public String caption = "";
	public PropertyField field = PropertyField.NONE;
	public PropertyField titldField = PropertyField.STATICTEXT;

	public WidgetType type = WidgetType.StaticText;
	public String id = "";
	public String subId = "";
	public List<KeyValue> css = new ArrayList<>();
	public String text;
	
	@JsonIgnore
	public String widgetId;
	
	private UiBuilderListener listener;
	private WidgetIdManager widgetIdManager;

	public Pane() {}
	public Pane(WidgetIdManager widgetIdManager, UiBuilderListener listener) {
		this.widgetIdManager = widgetIdManager;
		this.listener = listener;
		this.widgetId = widgetIdManager.createWidgetId(this);
	}

	public Pane(WidgetIdManager widgetIdManager) {
		this.widgetIdManager = widgetIdManager;
		this.widgetId = widgetIdManager.createWidgetId(this);
	}
	
	public Pane createComboBox(String id) {
		return this.createWidget(WidgetType.ComboBox, id).field(PropertyField.VALUE);
	}

	public Pane createPane(Layout layout) {
		Pane pane = new Pane(widgetIdManager, listener).type(WidgetType.Pane).layout(layout);
		this.applyLayout(pane);
		return pane;
	}

	public Pane layout(Layout layout2) {
		this.layout = layout2;
		return this;
	}
	public Pane createLabel(String id, PropertyField field) {
		return createWidget(WidgetType.Label, id).field(field);
	}

	public Pane createTextField(String id, PropertyField field) {
		return createWidget(WidgetType.TextField, id).field(field);
	}

	public Pane createCheckBox(String id) {
		return createWidget(WidgetType.CheckBox, id);
	}

	public Pane createToggleButton(String id, String optionId) {
		return this.createWidget(WidgetType.ToggleButton, id).optionId(optionId).field(PropertyField.VALUE);
	}

	private Pane applyLayout(Pane widget) {
		this.widgets.add(widget);
		if (this.layout.compareTo(Layout.HORIZONTAL) == 0) {
			widget.css("display", "inline-block");
		}
		else if (this.layout.compareTo(Layout.VERTICAL) == 0) {
			widget.css("display", "block");
		}
		else if (this.layout.compareTo(Layout.ABSOLUTE) == 0) {
			
		}
		
		widget.widgetId = widgetIdManager.createWidgetId(widget);
		return widget;
	}
	
	public Pane createToggleButton(String id) {
		return createToggleButton(id, "");
	}

	public Pane createStaticText(String text) {
		return createWidget(WidgetType.StaticText, "").text(text);
	}

	public Pane createButton(String id) {
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

	public Pane createChart(String id) {
		return createWidget(WidgetType.Chart, id);
	}

	public Pane createTable(String id) {
		return createWidget(WidgetType.Table, id);
	}

	public Pane title(String id, PropertyField field) {
		this.id = id;
		this.titldField = field;
		return this;
	}

	public Pane createImage(String id) {
		return createWidget(WidgetType.Image, id);
	}

	public Pane createSlider(String id) {
		return createWidget(WidgetType.Slider, id);
	}
	
	private Pane createWidget(WidgetType type2, String id) {
		Pane widget = new Pane(this.widgetIdManager, this.listener).type(type2).id(id);
		return applyLayout(widget);
	}
	
	private Pane id(String id2) {
		this.id = id2;
		return this;
	}
	public Pane type(WidgetType type2) {
		this.type = type2;
		return this;
	}
	public Pane title(String text) {
		this.caption = text;
		return this;
	}
	
	public void applyWidgetId(WidgetIdManager widgetIdManager) {
		this.widgetId = widgetIdManager.createWidgetId(this);
		new WalkThrough() {
			@Override
			protected void handle(Pane widget) {
				widget.widgetId = widgetIdManager.createWidgetId(widget);
			}
		}.walkThrough(this);		
	}


	public Pane css(String key, String value) {
		boolean updated = false;
		for (KeyValue kv : this.css) {
			if (kv.getKey().equals(key)) {
				kv.setValue(value);
				updated = true;
				break;
			}
		}
		if (!updated) {
			this.css.add(new KeyValue(key, value));
		}
		if (this.listener != null) {
			this.listener.onCssUpdate(this.widgetId, key, value);
		}
		return this;
	}

	public Pane field(PropertyField value) {
		this.field = value;
		return this;
	}

	public Pane optionId(String optionId) {
		this.subId = optionId;
		return this;
	}

	public Pane text(String text) {
		this.text = text;
		return this;
	}
	
	abstract class WalkThrough {
		abstract protected void handle(Pane widget);
		void walkThrough(Pane pane) {
			handle(pane);
			for (Pane w : pane.widgets) {
				walkThrough( w );
			}
		}
	}

	public void setListener(UiBuilderListener uiBuilderListener) {
		new WalkThrough() {
			@Override
			protected void handle(Pane widget) {
				widget.listener = uiBuilderListener;
			}
		}.walkThrough(this);
	}
	public void setType(String type2) {
		this.type = WidgetType.valueOf(type2);
		if (this.listener != null) {
			this.listener.onTypeUpdate(this.widgetId, this.type);
		}
	}
	public void setId(String id2, String subId2) {
		this.id = id2;
		this.subId = subId2;
		if (this.listener != null) {
			this.listener.onIdChange(this.id, this.subId);
		}
	}
}

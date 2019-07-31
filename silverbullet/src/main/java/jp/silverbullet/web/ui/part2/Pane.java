package jp.silverbullet.web.ui.part2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jp.silverbullet.web.KeyValue;
import jp.silverbullet.web.ui.part2.UiBuilder.PropertyField;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Pane implements Cloneable {

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
	public String optional = "";
	
	public List<String> volatileInfo = new ArrayList<>();
	
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
		this.addAsChild(pane);
		return pane;
	}

	public Pane layout(Layout layout2) {
		this.layout = layout2;
		for (Pane p : this.widgets) {
			this.applyLayout(p);
		}
		fireLayoutChange();
		return this;
	}
	public void fireLayoutChange() {
		if (this.listener != null) {
			this.listener.onLayoutChange(this.widgetId);
		}
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

	private Pane addAsChild(Pane widget) {
		this.widgets.add(widget);
		applyLayout(widget);
		
//		widget.widgetId = widgetIdManager.createWidgetId(widget);
		return widget;
	}
	private void applyLayout(Pane widget) {
		widget.css("display", "").css("position", "");

		if (this.layout.compareTo(Layout.HORIZONTAL) == 0) {
			widget.css("display", "inline-block");
		}
		else if (this.layout.compareTo(Layout.VERTICAL) == 0) {
			widget.css("display", "block");
		}
		else if (this.layout.compareTo(Layout.ABSOLUTE) == 0) {
			widget.css("position", "absolute");//.css("display", "");
		}
	}
	
	public Pane createToggleButton(String id) {
		Pane widget = createToggleButton(id, "");
		widget.css("padding", "5px");
		return widget;
	}

	public Pane createStaticText(String text) {
		return createWidget(WidgetType.StaticText, "").text(text);
	}

	public Pane createButton(String id) {
		Pane widget = createWidget(WidgetType.Button, id);
		widget.css("padding", "5px");
		return widget;
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
		Pane newWidget = addAsChild(widget);
		return newWidget;
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
		this.widgetIdManager = widgetIdManager;
		this.widgetId = widgetIdManager.createWidgetId(this);
		new PaneWalkThrough() {
			@Override
			protected boolean handle(Pane widget, Pane parent) {
				widget.widgetId = widgetIdManager.createWidgetId(widget);
				widget.widgetIdManager = widgetIdManager;
				return true;
			}
		}.walkThrough(this, null);		
	}


	public Pane css(String key, String value) {
		boolean updated = false;
		
		if (value == null || value.isEmpty()) {
			updated = true;
			Iterator<KeyValue> it = this.css.iterator();
			while(it.hasNext()) {
				if (it.next().getKey().equals(key)) {
					it.remove();
				}
			}
		}
		else {
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
		}
//		fireCssUpdate(key, value);
		return this;
	}
	private void fireCssUpdate(String key, String value) {
		if (this.listener != null) {
			this.listener.onCssUpdate(this.widgetId, key, value);
		}
	}

	public Pane field(PropertyField value) {
		this.field = value;
//		if (this.listener != null) {
//			this.listener.onFieldChange(this.id);
//		}
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
	

	public void setListener(UiBuilderListener uiBuilderListener) {
		new PaneWalkThrough() {
			@Override
			protected boolean handle(Pane widget, Pane parent) {
				widget.listener = uiBuilderListener;
				return true;
			}
		}.walkThrough(this, null);
	}
	public void setType(String type2) {
		this.type(WidgetType.valueOf(type2));
		//this.type = WidgetType.valueOf(type2);
//		if (this.listener != null) {
//			this.listener.onTypeUpdate(this.widgetId, this.type);
//		}
	}
	public void setId(String id2, String subId2) {
		this.id = id2;
		this.subId = subId2;
//		if (this.listener != null) {
//			this.listener.onIdChange(this.id, this.subId);
//		}
	}
	@JsonIgnore
	private Pane parent;
	public Pane getParent(String divid) {
		parent = null;
		new PaneWalkThrough() {
			@Override
			protected boolean handle(Pane widget, Pane parent2) {
				if (widget.widgetId.equals(divid)) {
					parent = parent2;
					return false;
				}
				return true;
			}
		}.walkThrough(this, null);	
		return parent;
	}
	
	public String css(String key) {
		for (KeyValue kv : this.css) {
			if (kv.getKey().equals(key)) {
				return kv.getValue();
			}
		}
		return null;
	}
	
	public void removeChild(Pane widget) {
		this.widgets.remove(widget);
	}
	
	public void addChild(Pane widget) {
		this.widgets.add(widget);
	}
	
	public void fireTypeChange() {
		if (this.listener != null) {
			this.listener.onTypeUpdate(this.widgetId, type);
		}
	}
	public void fireFieldChange() {
		if (this.listener != null) {
			this.listener.onFieldChange(this.widgetId);
		}
	}
	public void fireIdChange() {
		if (this.listener != null) {
			this.listener.onIdChange(this.id, this.subId);
		}
	}
	public void fireCssChange(String widgetId, String key, String value) {
		if (this.listener != null) {
			this.listener.onCssUpdate(widgetId, key, value);
		}
	}
	public void setOptional(String optional2) {
		this.optional = optional2;
	}
	
	@Override
	protected Pane clone() {
		try {
			Pane pane = (Pane)super.clone();
			pane.widgets = new ArrayList<Pane>(this.widgets);
//			pane.volatileInfo = new ArrayList<String>();
			return pane;
			
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// bad design this should be a saparated class
	@JsonIgnore
	private Pane foundPane = null;
	public Pane findLink(String linkName) {
		foundPane = null;
		new PaneWalkThrough() {
			@Override
			protected boolean handle(Pane widget, Pane parent2) {
				if (widget.optional.startsWith("$NAME")) {
					String name = widget.optional.split("=")[1];
					if (linkName.equals(name)) {
						foundPane = widget;
						return false;
					}
				}

				return true;
			}
		}.walkThrough(this, null);
		return foundPane;
	}
	
	@JsonIgnore
	public List<String> getNameList() {
		List<String> ret = new ArrayList<>();
		new PaneWalkThrough() {

			@Override
			protected boolean handle(Pane widget, Pane parent) {
				if (widget.optional.startsWith("$NAME")) {
					ret.add(widget.optional.split("=")[1]);
				}
				return false;
			}
			
		}.walkThrough(this, null);
		return ret;
	}
	
}

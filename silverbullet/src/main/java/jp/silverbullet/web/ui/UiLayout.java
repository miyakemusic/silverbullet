package jp.silverbullet.web.ui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.SvProperty;
import jp.silverbullet.web.WebSocketBroadcaster;

public class UiLayout {
	@JsonIgnore
	private static UiLayout instance;
	public JsWidget root;
	
	public static void write(UiLayout object) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String s = mapper.writeValueAsString(object);
			Files.write(Paths.get("layout.json"), Arrays.asList(s));
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void save() {
		write(this);
		WebSocketBroadcaster.getInstance().sendMessage("layoutChanged");
	}
	
	
	public void read(String filename) {
		this.root = UiLayout.createRoot();
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			UiLayout object = mapper.readValue(new File(filename), UiLayout.class);
			root = object.root;
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static JsWidget createRoot() {
		JsWidget root = new JsWidget();
		root.setWidgetType(JsWidget.PANEL);
		root.setWidth("800");
		root.setHeight("400");

		return root;
	}

	private UiLayout() {
//		initialize();
	}
	
	public void initialize() {
		if (Files.exists(Paths.get("layout.json"))) {
			read("layout.json");
			return;
		}
		
		root = createRoot();
	}
	
	private JsWidget createPanel() {
		JsWidget widget = new JsWidget();
		widget.setWidgetType(JsWidget.PANEL);
		return widget;
	}
	
	public JsWidget getRoot() {
		return root;
	}
	public static UiLayout getInstance() {
		if (instance == null) {
			instance = new UiLayout();
		}
		return instance;
	}

	public void addWidget(String div, List<String> ids) {
		int unique = extractUnique(div);
		JsWidget panel = null;
		panel = getDiv(unique);
		
		for (String id : ids) {
			SvProperty property = StaticInstances.getBuilderModel().getProperty(id);
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
		save();
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
		
		save();
	}
	
	public void resize(String div, String width, String height) {
		int unique = extractUnique(div);
		JsWidget widget = this.getDiv(unique);
		widget.setWidth(width);
		widget.setHeight(height);
		
		save();
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
	
	public void setLayout(String div, String layout) {
		JsWidget panel = getWidget(div);
		panel.setLayout(layout);
		this.save();
	}

	private JsWidget getWidget(String div) {
		int unique = extractUnique(div);
		JsWidget panel =  this.getDiv(unique);
		return panel;
	}

	public void remove(String div) {
		removeDiv(root, extractUnique(div));
		save();
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

	public void setWidgetType(String div, String widgetType) {
		JsWidget panel = getWidget(div);
		panel.setWidgetType(widgetType);
		this.save();
	}

	public void setSyle(String div, String style) {
		JsWidget panel = getWidget(div);
		panel.setStyleClass(style);
		this.save();
	}

	public void setCss(String div, String css) {
		JsWidget panel = getWidget(div);
		panel.setCss(css);
		this.save();	
	}

	public void setId(String div, String id) {
		JsWidget panel = getWidget(div);
		panel.setId(id);
		this.save();
	}

	public void addDialog(String div, String id) {
		int unique = extractUnique(div);
		JsWidget panel = getDiv(unique);
		JsWidget dialog = new JsWidget();
		dialog.getCustom().put("id", id);
		dialog.setWidgetType(JsWidget.GUI_DIALOG);
		panel.addChild(dialog);
		this.save();
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
		if (this.root.getCustom().get(CustomProperties.GUI_ID).equals(id)) {
			return root;
		}
		return this.findPanel(this.root, id);
	}

	public void clear() {
		this.root = UiLayout.createRoot();
	}

	public void setPresentation(String div, String presentation) {
		JsWidget panel = getWidget(div);
		panel.setPresentation(presentation);
		this.save();
	}

	public void setCustom(String div, Map<String, String> custom) {
		JsWidget panel = getWidget(div);
		panel.setCustom(custom);
		this.save();
	}

	public void cutPaste(String newBaseDiv, String itemDiv) {
		JsWidget item = this.getWidget(itemDiv);
		this.remove(itemDiv);
		this.getWidget(newBaseDiv).addChild(item);
		this.save();
	}

	public void setCustomElement(String div, String customId, String customValue) {
		JsWidget panel = getWidget(div);
		panel.getCustom().put(customId, customValue);
		this.save();
	}
}

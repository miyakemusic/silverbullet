package jp.silverbullet.web.ui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import jp.silverbullet.BuilderFx;
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
	
	private UiLayout() {

	}
	
	public void initialize() {
		if (Files.exists(Paths.get("layout.json"))) {
			read("layout.json");
			return;
		}
		
		root = new JsWidget();
		root.setWidgetType(JsWidget.PANEL);
		root.setWidth("800");
		root.setHeight("400");
		
//		JsWidget panel2 = createPanel();
//		root.addChild(panel2);
//		panel2.setWidth("400");
//		panel2.setHeight("200");
//
//		JsWidget panel3 = createPanel();
//		panel2.addChild(panel3);
//		panel3.setWidth("200");
//		panel3.setHeight("100");
//		
//		root.addChild(createWidget("ID_BAND", JsWidget.COMBOBOX));
//		root.addChild(createWidget("ID_STARTWAVELENGTH", JsWidget.TEXTFIELD));
//		root.addChild(createWidget("ID_STOPWAVELENGTH", JsWidget.TEXTFIELD));
//		
//		JsWidget panel = createPanel();
//		root.addChild(panel);
//		panel.addChild(createWidget("ID_CENTERWAVELENGTH", JsWidget.TEXTFIELD));
//		panel.addChild(createWidget("ID_SPANWAVELENGTH", JsWidget.TEXTFIELD));
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
			SvProperty property = BuilderFx.getModel().getBuilderModel().getProperty(id);
			String type = property.getType();
			JsWidget widget = new JsWidget();
			widget.setId(id);
			
			if (type.equals(SvProperty.DOUBLE_PROPERTY) || type.equals(SvProperty.TEXT_PROPERTY)) {
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
				widget.setWidgetType(JsWidget.TEXTFIELD);
			}
			else if (type.equals(SvProperty.ACTION_PROPERTY)) {
				widget.setWidgetType(JsWidget.ACTIONBUTTON);
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
		int unique = Integer.valueOf(div.split("-")[1]);
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
}

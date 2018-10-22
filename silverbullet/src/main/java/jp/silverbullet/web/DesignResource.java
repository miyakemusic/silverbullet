package jp.silverbullet.web;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.Sequencer;
import jp.silverbullet.StaticInstances;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.RequestRejectedException;
import jp.silverbullet.property.ChartContent;

import jp.silverbullet.web.ui.CustomProperties;
import jp.silverbullet.web.ui.JsProperty;
import jp.silverbullet.web.ui.JsWidget;

@Path("/design")
public class DesignResource {

	@GET
	@Path("/getProperty")
	@Produces(MediaType.APPLICATION_JSON) 
	public JsProperty getProperty(@QueryParam("id") String id, @QueryParam("ext") String ext) {
		SvProperty property = StaticInstances.getInstance().getBuilderModel().getProperty(id);
		JsProperty ret = convertProperty(property, ext);
		return ret;
	}
	
	private JsProperty convertProperty(SvProperty property, String ext) {
		JsProperty ret = new JsProperty();
		ret.setId(property.getId());
		ret.setTitle(property.getTitle());
		ret.setUnit(property.getUnit());
		ret.setElements(property.getAvailableListDetail());
		ret.setEnabled(property.isEnabled());
		
		if (property.isChartProperty()) {
			if (ext == null) {
				ret.setCurrentValue("REQUEST_AGAIN");
			}
			else {
				try {
					if (property.getCurrentValue().isEmpty()) {
						return ret;
					}
					ChartContent chartContent = new ObjectMapper().readValue(property.getCurrentValue(), ChartContent.class);
					int point = Integer.valueOf(ext);
					int allSize = chartContent.getY().length;
					double step = (double)allSize / (double)point;
					String[] y = new String[point];
					for (int i = 0; i < point; i++) {
						y[i] = chartContent.getY()[(int)((double)i*step)];
					}
					chartContent.setY(y);
					ret.setCurrentValue(new ObjectMapper().writeValueAsString(chartContent));
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
		}
		else if (property.isListProperty()) {
			ret.setCurrentValue(property.getSelectedListTitle());
			ret.setCurrentSelectionId(property.getCurrentValue());
		}
		else {
			ret.setCurrentValue(property.getCurrentValue());
		}
		

		return ret;
	}
	
	private List<String> debugDepLog;
	@GET
	@Path("/setValue")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> setCurrentValue(@QueryParam("id") String id, @QueryParam("value") String value) {
		Sequencer sequencer = null;
		try {
			sequencer = StaticInstances.getInstance().getBuilderModel().getSequencer();
			sequencer.requestChange(id, value);
		} catch (RequestRejectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			debugDepLog = sequencer.getDebugDepLog();		
		}
		
		return debugDepLog;
	}
	
	@GET
	@Path("/getDesign")
	@Produces(MediaType.APPLICATION_JSON) 
	public JsWidget getDesign(@QueryParam("root") String root) {
		if (root == null || root.isEmpty()) {
			return StaticInstances.getInstance().getBuilderModel().getUiLayout().getRoot();// StaticInstances.getInstance().getBuilderModel().getUiLayout().getRoot();
		}
		else {
			return StaticInstances.getInstance().getBuilderModel().getUiLayout().getSubTree(root);
		}
	}
	
	@GET
	@Path("/addWidget")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addWidget(@QueryParam("id") List<String> ids, @QueryParam("div") String div) {
		StaticInstances.getInstance().getBuilderModel().getUiLayout().addWidget(div, ids);
		return "OK";
	}

	@GET
	@Path("/addDialog")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addDialog(@QueryParam("div") String div, @QueryParam("id") String id) {
		StaticInstances.getInstance().getBuilderModel().getUiLayout().addDialog(div, id);
		return "OK";
	}
	
	@GET
	@Path("/remove")
	@Produces(MediaType.TEXT_PLAIN) 
	public String remove(@QueryParam("div") String div) {
		StaticInstances.getInstance().getBuilderModel().getUiLayout().remove(div);
		return "OK";
	}
	
	@GET
	@Path("/move")
	@Produces(MediaType.TEXT_PLAIN) 
	public String move(@QueryParam("div") String div, @QueryParam("x") String x, @QueryParam("y") String y) {
//		System.out.println(x + "," + y);
		StaticInstances.getInstance().getBuilderModel().getUiLayout().move(div, x, y);
		return "OK";
	}

	@GET
	@Path("/addPanel")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addPanel(@QueryParam("div") String div) {
		StaticInstances.getInstance().getBuilderModel().getUiLayout().addPanel(div);
		return "OK";
	}

	@GET
	@Path("/addTab")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addTab(@QueryParam("div") String div) {
		StaticInstances.getInstance().getBuilderModel().getUiLayout().addTab(div);
		return "OK";
	}
	
	@GET
	@Path("/clearLayout")
	@Produces(MediaType.TEXT_PLAIN) 
	public String clearLayout() {
		StaticInstances.getInstance().getBuilderModel().getUiLayout().clear();
		return "OK";
	}
	
	@GET
	@Path("/setLayout")
	@Produces(MediaType.TEXT_PLAIN) 
	public String move(@QueryParam("div") String div, @QueryParam("layout") String layout) {
		StaticInstances.getInstance().getBuilderModel().getUiLayout().updateProperty(div, "layout", layout);
		return "OK";
	}

	@GET
	@Path("/resize")
	@Produces(MediaType.TEXT_PLAIN) 
	public String resize(@QueryParam("div") String div, @QueryParam("width") String width, @QueryParam("height") String height) {
		StaticInstances.getInstance().getBuilderModel().getUiLayout().resize(div, width, height);
		return "OK";
	}
	
	@GET
	@Path("/updateGuiProperty")
	@Produces(MediaType.TEXT_PLAIN) 
	public String updateGuiProperty(@QueryParam("div") String div, @QueryParam("propertyType") String propertyType, @QueryParam("value") String value) {
		StaticInstances.getInstance().getBuilderModel().getUiLayout().updateProperty(div, propertyType, value);
		return "OK";
	}

	@GET
	@Path("/updateGuiBooleanProperty")
	@Produces(MediaType.TEXT_PLAIN) 
	public String updateGuiBooleanProperty(@QueryParam("div") String div, @QueryParam("propertyType") String propertyType, @QueryParam("value") Boolean value) {
		StaticInstances.getInstance().getBuilderModel().getUiLayout().updateBooleanProperty(div, propertyType, value);
		return "OK";
	}
	
	@GET
	@Path("/layoutTypes")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getLayoutTypes() {
		return Arrays.asList(JsWidget.ABSOLUTELAYOUT, JsWidget.FLOWLAYOUT, JsWidget.VERTICALLAYOUT);
	}
	
	@GET
	@Path("getWidgetTypes")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getWidgetTypes() {
		return Arrays.asList(JsWidget.TOGGLEBUTTON, JsWidget.CSSBUTTON, JsWidget.ACTIONBUTTON, JsWidget.COMBOBOX, JsWidget.RADIOBUTTON, JsWidget.TEXTFIELD,
				JsWidget.CHART, JsWidget.CHART_CANVASJS, JsWidget.CHECKBOX, JsWidget.GUI_DIALOG, JsWidget.PANEL, JsWidget.TAB, JsWidget.LABEL, JsWidget.MESSAGEBOX);
	}

	@GET
	@Path("setCustom")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setCustom(@QueryParam("div") String div, @QueryParam("custom") String custom) {
		//StaticInstances.getInstance().getBuilderModel().getUiLayout().setCustom(div, custom);
		return "OK";
	}
	
	@GET
	@Path("cutPaste")
	@Produces(MediaType.TEXT_PLAIN) 
	public String cutPaste(@QueryParam("newBaseDiv") String newBaseDiv, @QueryParam("itemDiv") String itemDiv) {
		StaticInstances.getInstance().getBuilderModel().getUiLayout().cutPaste(newBaseDiv, itemDiv);
		return "OK";
	}
	
	@GET
	@Path("getStyleClasses")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getStyleClasses(@QueryParam("type") String type) {
		return Arrays.asList("tabs-top", "tabs-bottom", "itemBox", "BigGrid", "noborder", "fontVeryBig", "fontBig", "fontMedium", "fontSmall");
	}
	
	@GET
	@Path("getCustromDefinition")
	@Produces(MediaType.APPLICATION_JSON) 
	public Map<String, List<Pair>> getCustromDefinition() {
		return CustomProperties.getInstance().getMap();
	}	
	
	@GET
	@Path("setCustomElement")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setCustomElement(@QueryParam("div") String div, @QueryParam("customId") String customId, @QueryParam("customValue") String customValue) {
		StaticInstances.getInstance().getBuilderModel().getUiLayout().setCustomElement(div, customId, customValue);
		return "OK";
	}
	
}

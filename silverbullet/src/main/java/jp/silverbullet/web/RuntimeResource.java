package jp.silverbullet.web;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import javafx.application.Platform;
import jp.silverbullet.BuilderFx;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.engine.RequestRejectedException;
import jp.silverbullet.property.ChartContent;
import jp.silverbullet.web.ui.JsProperty;
import jp.silverbullet.web.ui.JsWidget;
import jp.silverbullet.web.ui.UiLayout;

@Path("/runtime")
public class RuntimeResource {

	@GET
	@Path("/getProperty")
	@Produces(MediaType.APPLICATION_JSON) 
	public JsProperty getProperty(@QueryParam("id") String id) {
		System.out.println(id);
		SvProperty property = BuilderFx.getModel().getBuilderModel().getProperty(id);
		JsProperty ret = convertProperty(property);
		if (property.getType().equals(SvProperty.CHART_PROPERTY)) {
			
		}
		return ret;
	}
	
	private JsProperty convertProperty(SvProperty property) {
		JsProperty ret = new JsProperty();
		ret.setId(property.getId());
		ret.setTitle(property.getTitle());
		ret.setUnit(property.getUnit());
		ret.setElements(property.getAvailableListDetail());
		ret.setCurrentValue(property.getCurrentValue());
		return ret;
	}
	
	@GET
	@Path("/setValue")
	public String setCurrentValue(@QueryParam("id") String id, @QueryParam("value") String value) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					BuilderFx.getModel().getBuilderModel().getSequencer().requestChange(id, value);
				} catch (RequestRejectedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
			
		return "OK";
	}
	
	@GET
	@Path("/getDesign")
	@Produces(MediaType.APPLICATION_JSON) 
	public JsWidget getDesign(@QueryParam("root") String root) {
		if (root == null || root.isEmpty()) {
			return UiLayout.getInstance().getRoot();
		}
		else {
			return UiLayout.getInstance().getSubTree(root);
		}
	}
	
	@GET
	@Path("/addWidget")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addWidget(@QueryParam("id") List<String> ids, @QueryParam("div") String div) {
		UiLayout.getInstance().addWidget(div, ids);
		return "OK";
	}

	@GET
	@Path("/addDialog")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addDialog(@QueryParam("div") String div, @QueryParam("id") String id) {
		UiLayout.getInstance().addDialog(div, id);
		return "OK";
	}
	
	@GET
	@Path("/remove")
	@Produces(MediaType.TEXT_PLAIN) 
	public String remove(@QueryParam("div") String div) {
		UiLayout.getInstance().remove(div);
		return "OK";
	}
	
	@GET
	@Path("/move")
	@Produces(MediaType.TEXT_PLAIN) 
	public String move(@QueryParam("div") String div, @QueryParam("x") String x, @QueryParam("y") String y) {
		System.out.println(x + "," + y);
		UiLayout.getInstance().move(div, x, y);
		return "OK";
	}

	@GET
	@Path("/addPanel")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addPanel(@QueryParam("div") String div) {
		UiLayout.getInstance().addPanel(div);
		return "OK";
	}

	@GET
	@Path("/addTab")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addTab(@QueryParam("div") String div) {
		UiLayout.getInstance().addTab(div);
		return "OK";
	}
	
	@GET
	@Path("/clearLayout")
	@Produces(MediaType.TEXT_PLAIN) 
	public String clearLayout() {
		UiLayout.getInstance().clear();
		return "OK";
	}
	
	@GET
	@Path("/setLayout")
	@Produces(MediaType.TEXT_PLAIN) 
	public String move(@QueryParam("div") String div, @QueryParam("layout") String layout) {
		UiLayout.getInstance().setLayout(div, layout);
		return "OK";
	}

	@GET
	@Path("/setStyle")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setStyle(@QueryParam("div") String div, @QueryParam("style") String style) {
		UiLayout.getInstance().setSyle(div, style);
		return "OK";
	}

	@GET
	@Path("/setCss")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setCss(@QueryParam("div") String div, @QueryParam("css") String css) {
		UiLayout.getInstance().setCss(div, css);
		return "OK";
	}
	@GET
	@Path("/resize")
	@Produces(MediaType.TEXT_PLAIN) 
	public String resize(@QueryParam("div") String div, @QueryParam("width") String width, @QueryParam("height") String height) {
		UiLayout.getInstance().resize(div, width, height);
		return "OK";
	}
	
	@GET
	@Path("/layoutTypes")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getLayoutTypes() {
		return Arrays.asList(JsWidget.ABSOLUTELAYOUT, JsWidget.FLOWLAYOUT, JsWidget.VERTICALLAYOUT);
	}
	
	@GET
	@Path("allWidgetTypes")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getAllWidgeTypes() {
		return Arrays.asList(JsWidget.TOGGLEBUTTON, JsWidget.ACTIONBUTTON, JsWidget.COMBOBOX, JsWidget.RADIOBUTTON, JsWidget.TEXTFIELD,
				JsWidget.CHART, JsWidget.CHECKBOX, JsWidget.GUI_DIALOG, JsWidget.PANEL, JsWidget.TAB);
	}
	
	@GET
	@Path("setWidgetType")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setWidgetType(@QueryParam("div") String div, @QueryParam("widgetType") String widgetType) {
		UiLayout.getInstance().setWidgetType(div, widgetType);
		return "OK";
	}
	
	@GET
	@Path("setId")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setId(@QueryParam("div") String div, @QueryParam("id") String id) {
		UiLayout.getInstance().setId(div, id);
		return "OK";
	}
	
	@GET
	@Path("setPresentation")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setPresentation(@QueryParam("div") String div, @QueryParam("presentation") String presentation) {
		UiLayout.getInstance().setPresentation(div, presentation);
		return "OK";
	}
}

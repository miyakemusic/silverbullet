package jp.silverbullet.web;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.web.ui.part2.UiBuilder;
import jp.silverbullet.web.ui.part2.UiBuilder.PropertyField;
import jp.silverbullet.web.ui.part2.Layout;
import jp.silverbullet.web.ui.part2.Pane;

@Path("/newGui")
public class NewGuiResource {
	@GET
	@Path("/getDesign")
	@Produces(MediaType.APPLICATION_JSON) 
	public UiBuilder getDesign() {
		return StaticInstances.getInstance().getBuilderModel().getUiBuilder();
	}	
	
	@GET
	@Path("/getWidget")
	@Produces(MediaType.APPLICATION_JSON) 
	public Pane getWidget(@QueryParam("divid") final String divid) {
		return StaticInstances.getInstance().getBuilderModel().getUiBuilder().getWidget(divid);
	}	
	
	@GET
	@Path("/setSize")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setSize(@QueryParam("divid") final String divid, @QueryParam("width") final String width, @QueryParam("height") final String height) {
		Pane widget = StaticInstances.getInstance().getBuilderModel().getUiBuilder().getWidget(divid);
		widget.css("width", width).css("height", height);
		return "OK";
	}	
	
	@GET
	@Path("/setCss")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setCss(@QueryParam("divid") final String divid, @QueryParam("key") final String key, @QueryParam("value") final String value) {
		Pane widget = StaticInstances.getInstance().getBuilderModel().getUiBuilder().getWidget(divid);
		widget.css(key, value);
		return "OK";
	}	
	
	@GET
	@Path("/setId")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setId(@QueryParam("divid") final String divid, @QueryParam("id") final String id, @QueryParam("subId") final String subId) {
		Pane widget = StaticInstances.getInstance().getBuilderModel().getUiBuilder().getWidget(divid);
		widget.setId(id, subId);
		return "OK";
	}
	
	@GET
	@Path("/setField")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setField(@QueryParam("divid") final String divid, @QueryParam("field") final String field) {
		Pane widget = StaticInstances.getInstance().getBuilderModel().getUiBuilder().getWidget(divid);
		widget.field(PropertyField.valueOf(field));
		return "OK";
	}
	
	@GET
	@Path("/setLayout")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setLayout(@QueryParam("divid") final String divid, @QueryParam("layout") final String layout) {
		Pane widget = StaticInstances.getInstance().getBuilderModel().getUiBuilder().getWidget(divid);
		widget.layout(Layout.valueOf(layout));
		return "OK";
	}
	
	@GET
	@Path("/getCssKeys")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getCssKeys() {
		return Arrays.asList("font", "font-weight", "font-size", "border", "border-width", "border-color", "color", 
				"background-color", "padding", "margin", "top", "left", "width", "height");
	}

	@GET
	@Path("/setWidgetType")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setWidgetType(@QueryParam("divid") final String divid, @QueryParam("type") final String type) {
		Pane widget = StaticInstances.getInstance().getBuilderModel().getUiBuilder().getWidget(divid);
		widget.setType(type);
		System.out.println(divid + "->" + type);
		return "OK";
	}
	
	@GET
	@Path("/addWidget")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addWidget(@QueryParam("divid") final String divid) {
		Pane widget = StaticInstances.getInstance().getBuilderModel().getUiBuilder().getWidget(divid);
		widget.createPane(Layout.HORIZONTAL).css("width", "100").css("height", "30");
		return "OK";
	}
}

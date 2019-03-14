package jp.silverbullet.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.web.ui.part2.UiBuilder;
import jp.silverbullet.web.ui.part2.WidgetBase;

@Path("/newGui")
public class NewGuiResource {
	@GET
	@Path("/getDesign")
	@Produces(MediaType.APPLICATION_JSON) 
	public UiBuilder getDesign() {
		return StaticInstances.getInstance().getBuilderModel().getUiBuilder();
	}	
	
	@GET
	@Path("/setSize")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setSize(@QueryParam("divid") final String divid, @QueryParam("width") final String width, @QueryParam("height") final String height) {
		WidgetBase widget = StaticInstances.getInstance().getBuilderModel().getUiBuilder().getWidget(divid);
		widget.css("width", width).css("height", height);
		return "OK";
	}	
}

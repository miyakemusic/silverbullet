package jp.silverbullet.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.BuilderFx;
import jp.silverbullet.SvProperty;
import jp.silverbullet.web.ui.JsWidget;
import jp.silverbullet.web.ui.LayoutDemo;

@Path("/runtime")
public class RuntimeResource {

	@GET
	@Path("/property")
	@Produces(MediaType.APPLICATION_JSON) 
	public SvProperty getProperty(@QueryParam("id") String id) {
		return BuilderFx.getModel().getBuilderModel().getProperty(id);
	}
	
	private static LayoutDemo layout = new LayoutDemo();
	
	@GET
	@Path("/layout")
	@Produces(MediaType.APPLICATION_JSON) 
	public JsWidget getLayout(@QueryParam("ui") String ui) {
		return layout.getRoot();
	}
}

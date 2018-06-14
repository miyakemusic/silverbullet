package jp.silverbullet.web;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import javafx.application.Platform;
import jp.silverbullet.BuilderFx;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.engine.RequestRejectedException;
import jp.silverbullet.web.ui.JsProperty;
import jp.silverbullet.web.ui.JsWidget;
import jp.silverbullet.web.ui.LayoutDemo;

@Path("/runtime")
public class RuntimeResource {

	@GET
	@Path("/getProperty")
	@Produces(MediaType.APPLICATION_JSON) 
	public JsProperty getProperty(@QueryParam("id") String id) {
		return convertProperty(BuilderFx.getModel().getBuilderModel().getProperty(id));
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
	@Path("/getLayout")
	@Produces(MediaType.APPLICATION_JSON) 
	public JsWidget getLayout(@QueryParam("ui") String ui) {
		return LayoutDemo.getInstance().getRoot();
	}
	
	@GET
	@Path("/addWidget")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addWidget(@QueryParam("id") List<String> ids, @QueryParam("div") String div) {
		LayoutDemo.getInstance().addWidget(div, ids);
		return "OK";
	}
	
	@GET
	@Path("/move")
	@Produces(MediaType.TEXT_PLAIN) 
	public String move(@QueryParam("div") String div, @QueryParam("x") String x, @QueryParam("y") String y) {
		System.out.println(x + "," + y);
		LayoutDemo.getInstance().move(div, x, y);
		return "OK";
	}
	
	@GET
	@Path("/resize")
	@Produces(MediaType.TEXT_PLAIN) 
	public String resize(@QueryParam("div") String div, @QueryParam("width") String width, @QueryParam("height") String height) {
		LayoutDemo.getInstance().resize(div, width, height);
		return "OK";
	}
}

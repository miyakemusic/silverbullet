package jp.silverbullet.web;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.property2.PropertyType2;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.web.ui.JsProperty;
import jp.silverbullet.web.ui.JsPropertyConverter;

@Path("/runtime")
public class RuntimeResource {
	@GET
	@Path("/getProperty")
	@Produces(MediaType.APPLICATION_JSON) 
	public JsProperty getProperty(@QueryParam("id") String id, @QueryParam("index") Integer index, @QueryParam("ext") String ext) {
		RuntimeProperty property = StaticInstances.getInstance().getBuilderModel().getRuntimePropertyStore().get(RuntimeProperty.createIdText(id,index));
		return JsPropertyConverter.convert(property, ext);
	}

	@GET
	@Path("/respondMessage")
	@Produces(MediaType.APPLICATION_JSON)
	public String respondMessage(@QueryParam("id") String id, @QueryParam("type") String type) {
		StaticInstances.getInstance().getBuilderModel().respondToMessage(id, type);
		return "OK";
	}
	
	@GET
	@Path("/getProperties")
	@Produces(MediaType.APPLICATION_JSON)
	public List<JsProperty> getProperties() {
		return JsPropertyConverter.convert(StaticInstances.getInstance().getBuilderModel().getRuntimePropertyStore().getAllProperties(PropertyType2.NotSpecified));
	}
	
	@GET
	@Path("/setValue")
	@Produces(MediaType.APPLICATION_JSON) 
	public ValueSetResult setCurrentValue(@QueryParam("id") String id, @QueryParam("index") Integer index, @QueryParam("value") String value) {
		return StaticInstances.getInstance().getBuilderModel().requestChange(id, index, value);
	}
	

}

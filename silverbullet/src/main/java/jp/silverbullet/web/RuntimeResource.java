package jp.silverbullet.web;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.SilverBulletServer;
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
		RuntimeProperty property = SilverBulletServer.getStaticInstance().getBuilderModel().getRuntimePropertyStore().get(RuntimeProperty.createIdText(id,index));
		if (property == null) {
			System.out.println();
		}
		return JsPropertyConverter.convert(property, ext, SilverBulletServer.getStaticInstance().getBuilderModel().getBlobStore());
	}

	@GET
	@Path("/respondMessage")
	@Produces(MediaType.APPLICATION_JSON)
	public String respondMessage(@QueryParam("id") String id, @QueryParam("type") String type) {
		SilverBulletServer.getStaticInstance().getBuilderModel().respondToMessage(id, type);
		return "OK";
	}
	
	@GET
	@Path("/getProperties")
	@Produces(MediaType.APPLICATION_JSON)
	public List<JsProperty> getProperties() {
		return JsPropertyConverter.convert(SilverBulletServer.getStaticInstance().getBuilderModel().getRuntimePropertyStore().getAllProperties(PropertyType2.NotSpecified));
	}
	
	@GET
	@Path("/setValue")
	@Produces(MediaType.APPLICATION_JSON) 
	public ValueSetResult setCurrentValue(@QueryParam("id") String id, @QueryParam("index") Integer index, @QueryParam("value") String value) {
		return SilverBulletServer.getStaticInstance().getBuilderModel().requestChange(id, index, value);
	}
	
	@GET
	@Path("/defaultValues")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setDefaultValues() {
		SilverBulletServer.getStaticInstance().getBuilderModel().setDefaultValues();
		return "OK";
	}
}

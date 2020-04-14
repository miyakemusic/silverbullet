package jp.silverbullet.web;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.core.property2.PropertyType2;
import jp.silverbullet.core.property2.RuntimeProperty;
import jp.silverbullet.core.ui.UiProperty;
import jp.silverbullet.core.ui.UiPropertyConverter;

@Path("/runtime")
public class RuntimeResource {
	@GET
	@Path("/getProperty")
	@Produces(MediaType.APPLICATION_JSON) 
	public UiProperty getProperty(@QueryParam("id") String id, @QueryParam("index") Integer index, @QueryParam("ext") String ext) {
		RuntimeProperty property = SilverBulletServer.getStaticInstance().getBuilderModel().getRuntimePropertyStore().get(RuntimeProperty.createIdText(id,index));
		if (property == null) {
			System.err.println(id);
		}
		return UiPropertyConverter.convert(property, ext, SilverBulletServer.getStaticInstance().getBuilderModel().getBlobStore());
	}

	@GET
	@Path("/getBlob")
	@Produces(MediaType.APPLICATION_JSON) 
	public BlobJson getProperty(@QueryParam("id") String id, @QueryParam("name") String name) {
		Object object = SilverBulletServer.getStaticInstance().getBuilderModel().getBlobStore().get(id);
		BlobJson json = new BlobJson();
		json.data = object.toString();
		return json;
	}
	
	@GET
	@Path("/respondMessage")
	@Produces(MediaType.APPLICATION_JSON)
	public String respondMessage(@QueryParam("id") String id, @QueryParam("type") String type) {
		SilverBulletServer.getStaticInstance().getBuilderModel().respondToMessage(id, type);
		return "OK";
	}

	@GET
	@Path("/replyDialog")
	@Produces(MediaType.APPLICATION_JSON)
	public String replyDialog(@QueryParam("messageId") String messageId, @QueryParam("reply") String reply) {
		SilverBulletServer.getStaticInstance().getBuilderModel().replyDialog(messageId, reply);
		return "OK";
	}
	
	@GET
	@Path("/getProperties")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UiProperty> getProperties() {
		return UiPropertyConverter.convert(SilverBulletServer.getStaticInstance().getBuilderModel().getRuntimePropertyStore().getAllProperties(PropertyType2.NotSpecified));
	}
	
	@GET
	@Path("/setValue")
	@Produces(MediaType.APPLICATION_JSON) 
	public ValueSetResult setCurrentValue(@QueryParam("id") String id, @QueryParam("index") Integer index, @QueryParam("value") String value) {
		return SilverBulletServer.getStaticInstance().getBuilderModel().requestChangeByUser(id, index, value);
	}

	@GET
	@Path("/defaultValues")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setDefaultValues() {
		SilverBulletServer.getStaticInstance().getBuilderModel().setDefaultValues();
		return "OK";
	}
	
	@GET
	@Path("/dependencyDebug")
	@Produces(MediaType.TEXT_PLAIN) 
	public String dependencyDebug(@QueryParam("enabled") boolean enabled) {
		SilverBulletServer.setDebugEnabled(enabled);
		return "OK";
	}
	
}

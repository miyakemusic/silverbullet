package jp.silverbullet.web;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.core.property2.PropertyType2;
import jp.silverbullet.core.property2.RuntimeProperty;
import jp.silverbullet.core.ui.UiProperty;
import jp.silverbullet.core.ui.UiPropertyConverter;

@Path("/{app}/{device}/runtime")
public class RuntimeResource {
	@GET
	@Path("/getProperty")
	@Produces(MediaType.APPLICATION_JSON) 
	public UiProperty getProperty(@PathParam("app") String app, @PathParam("device") String device,
			@QueryParam("id") String id, @QueryParam("index") Integer index, @QueryParam("ext") String ext) {
		RuntimeProperty property = SilverBulletServer.getStaticInstance().getBuilderModel(app, device).getRuntimePropertyStore().get(RuntimeProperty.createIdText(id,index));
		if (property == null) {
			System.err.println(id);
		}
		UiProperty ret = UiPropertyConverter.convert(property, ext, SilverBulletServer.getStaticInstance().getBuilderModel(app, device).getBlobStore());
		return ret;
	}

	@GET
	@Path("/getBlob")
	@Produces(MediaType.APPLICATION_JSON) 
	public BlobJson getProperty(@PathParam("app") String app, @PathParam("device") String device,
			@QueryParam("id") String id, @QueryParam("name") String name) {
		Object object = SilverBulletServer.getStaticInstance().getBuilderModel(app, device).getBlobStore().get(id);
		BlobJson json = new BlobJson();
		json.data = object.toString();
		return json;
	}
	
	@GET
	@Path("/respondMessage")
	@Produces(MediaType.APPLICATION_JSON)
	public String respondMessage(@PathParam("app") String app, @PathParam("device") String device,
			@QueryParam("id") String id, @QueryParam("type") String type) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app, device).respondToMessage(id, type);
		return "OK";
	}

	@GET
	@Path("/replyDialog")
	@Produces(MediaType.APPLICATION_JSON)
	public String replyDialog(@PathParam("app") String app, @PathParam("device") String device,
			@QueryParam("messageId") String messageId, @QueryParam("reply") String reply) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app, device).replyDialog(messageId, reply);
		return "OK";
	}
	
	@GET
	@Path("/getProperties")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UiProperty> getProperties(@PathParam("app") String app, @PathParam("device") String device) {
		return UiPropertyConverter.convert(SilverBulletServer.getStaticInstance().getBuilderModel(app, device).getRuntimePropertyStore().getAllProperties(PropertyType2.NotSpecified));
	}
	
	@GET
	@Path("/setValue")
	@Produces(MediaType.APPLICATION_JSON) 
	public ValueSetResult setCurrentValue(@PathParam("app") String app, @PathParam("device") String device,
			@QueryParam("id") String id, @QueryParam("index") Integer index, @QueryParam("value") String value) {
		return SilverBulletServer.getStaticInstance().getBuilderModel(app, device).requestChangeByUser(id, index, value);
	}

	@GET
	@Path("/defaultValues")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setDefaultValues(@PathParam("app") String app, @PathParam("device") String device) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app, device).setDefaultValues();
		return "OK";
	}
	
	@GET
	@Path("/dependencyDebug")
	@Produces(MediaType.TEXT_PLAIN) 
	public String dependencyDebug(@PathParam("app") String app, @PathParam("device") String device,
			@QueryParam("enabled") boolean enabled) {
		SilverBulletServer.setDebugEnabled(enabled);
		return "OK";
	}
	
}

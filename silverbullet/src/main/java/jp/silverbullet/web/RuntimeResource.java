package jp.silverbullet.web;

import java.util.List;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.core.dependency2.Id;
import jp.silverbullet.core.property2.PropertyType2;
import jp.silverbullet.core.property2.RuntimeProperty;
import jp.silverbullet.core.ui.UiProperty;
import jp.silverbullet.core.ui.UiPropertyConverter;
import jp.silverbullet.dev.Automator;

@Path("/{app}/{device}/runtime")
public class RuntimeResource {
	@GET
	@Path("/getProperty")
	@Produces(MediaType.APPLICATION_JSON) 
	public UiProperty getProperty(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @PathParam("device") String device,
			@QueryParam("id") String id, @QueryParam("index") Integer index, @QueryParam("ext") String ext) {
		RuntimeProperty property = SilverBulletServer.getStaticInstance().getBuilderModelBySessionName(cookie, app, device).getRuntimePropertyStore().get(RuntimeProperty.createIdText(id,index));
		if (property == null) {
			System.err.println(id);
		}
		UiProperty ret = UiPropertyConverter.convert(property, ext, SilverBulletServer.getStaticInstance().getBuilderModelBySessionName(cookie, app, device).getBlobStore());
		return ret;
	}

	@GET
	@Path("/getBlob")
	@Produces(MediaType.APPLICATION_JSON) 
	public BlobJson getProperty(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @PathParam("device") String device,
			@QueryParam("id") String id, @QueryParam("name") String name) {
		Object object = SilverBulletServer.getStaticInstance().getBuilderModelBySessionName(cookie, app, device).getBlobStore().get(id);
		BlobJson json = new BlobJson();
		json.data = object.toString();

		return json;
	}
	
	@GET
	@Path("/respondMessage")
	@Produces(MediaType.APPLICATION_JSON)
	public String respondMessage(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @PathParam("device") String device,
			@QueryParam("id") String id, @QueryParam("type") String type) {
		SilverBulletServer.getStaticInstance().getBuilderModelBySessionName(cookie, app, device).respondToMessage(id, type);
		return "OK";
	}

	@GET
	@Path("/replyDialog")
	@Produces(MediaType.APPLICATION_JSON)
	public String replyDialog(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @PathParam("device") String device,
			@QueryParam("messageId") String messageId, @QueryParam("reply") String reply) {
		SilverBulletServer.getStaticInstance().getBuilderModelBySessionName(cookie, app, device).replyDialog(messageId, reply);
		return "OK";
	}
	
	@GET
	@Path("/replyMessage")
	@Produces(MediaType.APPLICATION_JSON)
	public String replyMessage(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @PathParam("device") String device,
			@QueryParam("messageId") String messageId, @QueryParam("reply") String reply) {
//		SilverBulletServer.getStaticInstance().getBuilderModelBySessionName(cookie, app, device).replyMessage(messageId, reply);
		
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		Automator automator = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getAutomator(userid);
		automator.onReplyMessage(messageId, reply);
		return "OK";
	}
	
	@GET
	@Path("/getProperties")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UiProperty> getProperties(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @PathParam("device") String device) {
		return UiPropertyConverter.convert(SilverBulletServer.getStaticInstance().getBuilderModelBySessionName(cookie, app, device).getRuntimePropertyStore().getAllProperties(PropertyType2.NotSpecified));
	}
	
	@GET
	@Path("/setValue")
	@Produces(MediaType.APPLICATION_JSON) 
	public ValueSetResult setCurrentValue(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @PathParam("device") String device,
			@QueryParam("id") String id, @QueryParam("index") Integer index, @QueryParam("value") String value) {
		return SilverBulletServer.getStaticInstance().getBuilderModelBySessionName(cookie, app, device).requestChangeByUser(new Id(id, index), value);
	}

	@GET
	@Path("/justSelect")
	@Produces(MediaType.APPLICATION_JSON) 
	public Response justSelect(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, 
			@PathParam("device") String device,
			@QueryParam("id") String id, @QueryParam("index") Integer index, @QueryParam("action") String action) {
		
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		RuntimeProperty prop = SilverBulletServer.getStaticInstance().getBuilderModelBySessionName(cookie, app, device).getRuntimePropertyStore().get(id);
		SilverBulletServer.getStaticInstance().getBuilderModelHolder().getAutomator(userid).addAction(device,id, prop.getCurrentValue(), action);
		
		return Response.ok().build();
	}
	
	@GET
	@Path("/defaultValues")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setDefaultValues(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @PathParam("device") String device) {
		SilverBulletServer.getStaticInstance().getBuilderModelBySessionName(cookie, app, device).setDefaultValues();
		return "OK";
	}
	
	@GET
	@Path("/dependencyDebug")
	@Produces(MediaType.TEXT_PLAIN) 
	public String dependencyDebug(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @PathParam("device") String device,
			@QueryParam("enabled") boolean enabled) {
		SilverBulletServer.setDebugEnabled(enabled);
		return "OK";
	}
	
	
	@POST
	@Path("/postFile")
	@Produces(MediaType.TEXT_PLAIN)
	public String postFile(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, 
			@PathParam("device") String device,	@QueryParam("filename") String filename, String base64) {

		String access_token = SilverBulletServer.getStaticInstance().getUserStore().getBySessionName(cookie).access_token;
		
		String path = "SilverBullet/Automated/" + device + "/toDevice";
		new GoogleDrivePost().base64(base64).post(access_token, path + "/" + filename);
		List<com.google.api.services.drive.model.File> files = SystemResource.googleHandler.getFileList(access_token, path);
		try {		
			FilePendingResponse msg = new FilePendingResponse(files);
			MessageToDevice message = new MessageToDevice(MessageToDevice.FILEREADY, FilePendingResponse.class.getName(), new ObjectMapper().writeValueAsString(msg));

			SilverBulletServer.getStaticInstance().sendMessageToDevice(cookie, app, device, 
					new ObjectMapper().writeValueAsString(message));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "OK";
	}
}

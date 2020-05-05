package jp.silverbullet.web;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.core.property2.PropertyType2;
import jp.silverbullet.core.property2.RuntimeProperty;
import jp.silverbullet.core.ui.UiProperty;
import jp.silverbullet.core.ui.UiPropertyConverter;

@Path("/{app}/{device}/runtime")
public class RuntimeResource {
	@GET
	@Path("/getProperty")
	@Produces(MediaType.APPLICATION_JSON) 
	public UiProperty getProperty(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @PathParam("device") String device,
			@QueryParam("id") String id, @QueryParam("index") Integer index, @QueryParam("ext") String ext) {
		RuntimeProperty property = SilverBulletServer.getStaticInstance().getBuilderModelBySessionID(cookie, app, device).getRuntimePropertyStore().get(RuntimeProperty.createIdText(id,index));
		if (property == null) {
			System.err.println(id);
		}
		UiProperty ret = UiPropertyConverter.convert(property, ext, SilverBulletServer.getStaticInstance().getBuilderModelBySessionID(cookie, app, device).getBlobStore());
		return ret;
	}

	@GET
	@Path("/getBlob")
	@Produces(MediaType.APPLICATION_JSON) 
	public BlobJson getProperty(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @PathParam("device") String device,
			@QueryParam("id") String id, @QueryParam("name") String name) {
		Object object = SilverBulletServer.getStaticInstance().getBuilderModelBySessionID(cookie, app, device).getBlobStore().get(id);
		BlobJson json = new BlobJson();
		json.data = object.toString();
		return json;
	}
	
	@GET
	@Path("/respondMessage")
	@Produces(MediaType.APPLICATION_JSON)
	public String respondMessage(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @PathParam("device") String device,
			@QueryParam("id") String id, @QueryParam("type") String type) {
		SilverBulletServer.getStaticInstance().getBuilderModelBySessionID(cookie, app, device).respondToMessage(id, type);
		return "OK";
	}

	@GET
	@Path("/replyDialog")
	@Produces(MediaType.APPLICATION_JSON)
	public String replyDialog(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @PathParam("device") String device,
			@QueryParam("messageId") String messageId, @QueryParam("reply") String reply) {
		SilverBulletServer.getStaticInstance().getBuilderModelBySessionID(cookie, app, device).replyDialog(messageId, reply);
		return "OK";
	}
	
	@GET
	@Path("/getProperties")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UiProperty> getProperties(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @PathParam("device") String device) {
		return UiPropertyConverter.convert(SilverBulletServer.getStaticInstance().getBuilderModelBySessionID(cookie, app, device).getRuntimePropertyStore().getAllProperties(PropertyType2.NotSpecified));
	}
	
	@GET
	@Path("/setValue")
	@Produces(MediaType.APPLICATION_JSON) 
	public ValueSetResult setCurrentValue(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @PathParam("device") String device,
			@QueryParam("id") String id, @QueryParam("index") Integer index, @QueryParam("value") String value) {
		return SilverBulletServer.getStaticInstance().getBuilderModelBySessionID(cookie, app, device).requestChangeByUser(id, index, value);
	}

	@GET
	@Path("/defaultValues")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setDefaultValues(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @PathParam("device") String device) {
		SilverBulletServer.getStaticInstance().getBuilderModelBySessionID(cookie, app, device).setDefaultValues();
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

		String path = "SilverBullet/Automated/" + device + "/toDevice/" + filename;
		String access_token = SilverBulletServer.getStaticInstance().getUserStore().getBySessionID(cookie).access_token;

		String[] tmp = base64.split(",");
		
		String type = tmp[0].split("[:;]+")[1];
		String data = tmp[1];
		
		String fileID = new GoogleDrivePost().base64(base64).post(access_token, path);
		try {		
			FileUploadMessage msg = new FileUploadMessage(fileID, path);
			MessageToDevice message = new MessageToDevice(MessageToDevice.FILEREADY, FileUploadMessage.class.getName(), new ObjectMapper().writeValueAsString(msg));

			SilverBulletServer.getStaticInstance().sendMessageToDevice(cookie, app, device, 
					new ObjectMapper().writeValueAsString(message));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "OK";
	}
}

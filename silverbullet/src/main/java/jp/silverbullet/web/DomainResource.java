package jp.silverbullet.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.core.dependency2.Id;
import jp.silverbullet.core.property2.LightProperty;
import jp.silverbullet.core.property2.RuntimeProperty;
import jp.silverbullet.dev.Automator;

@Path("/{app}/domain")
public class DomainResource {
	
	@GET
	@Path("/devices")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getDevices(@CookieParam("SilverBullet") String cookie) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		Map<String, WebSocketObject> set = WebSocketBroadcaster.getInstance().getDomainModels(userid);
		if (set != null) {
			return new ArrayList<String>(set.keySet());
		}
		else {
			return new ArrayList<String>();
		}
	}
	@GET
	@Path("/record")
	@Produces(MediaType.APPLICATION_JSON) 
	public Response record(@CookieParam("SilverBullet") String cookie) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		Automator automator = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getAutomator(userid);
		automator.clear();
		return Response.ok().build();
	}
	@GET
	@Path("/script")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> selectDevice(@CookieParam("SilverBullet") String cookie) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		Automator automator = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getAutomator(userid);
		return automator.getLines();
	}
	
	@POST
	@Path("/playback")
	@Produces(MediaType.APPLICATION_JSON) 
	public String playback(@CookieParam("SilverBullet") String cookie, String script) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		Automator automator = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getAutomator(userid);
		automator.playback(script);
		return "OK";
	}
	
	@POST
	@Path("/saveScript")
	@Produces(MediaType.APPLICATION_JSON) 
	public String saveScript(@CookieParam("SilverBullet") String cookie, String script, @QueryParam("name") String name) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		Automator automator = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getAutomator(userid);
		automator.register(name, script);
		return "OK";
	}
	
	@GET
	@Path("/scriptList")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> scriptList(@CookieParam("SilverBullet") String cookie) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		Automator automator = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getAutomator(userid);
		return automator.scriptList();
	}
	
//	@GET
//	@Path("/{device}/selectDevice")
//	@Produces(MediaType.APPLICATION_JSON) 
//	public Response selectDevice(@CookieParam("SilverBullet") String cookie) {
//		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
//		Map<String, WebSocketObject> set = WebSocketBroadcaster.getInstance().getDomainModels(userid);
//		return Response.ok().build();
//	}
	
	@GET
	@Path("/{device}/login")
	@Produces(MediaType.TEXT_PLAIN) 
	public String longin(@QueryParam("userid") String userid, @PathParam("app") String app, 
			@PathParam("device") String device, @Context HttpServletRequest request) {
		System.out.println("login: + " + app + "@" + device);
		SilverBulletServer.getStaticInstance().createDevice(userid, app, device);
		return "OK";
	}

	@GET
	@Path("/{device}/logout")
	@Produces(MediaType.TEXT_PLAIN) 
	public String logout(@QueryParam("userid") String userid, @PathParam("app") String app, @PathParam("device") String device) {
		System.out.println("logout: + " + app + "@" + device);
		SilverBulletServer.getStaticInstance().deleteDevice(userid, app, device);
		return "OK";
	}
	
	@GET
	@Path("/{device}/getProperty")
	@Produces(MediaType.APPLICATION_JSON) 
	public LightProperty getProperty(@QueryParam("userid") String userid, @PathParam("app") String app, @PathParam("device") String device, 
			@QueryParam("id") String id, @QueryParam("index") Integer index, @QueryParam("ext") String ext) {
		RuntimeProperty property = SilverBulletServer.getStaticInstance().getBuilderModelByUserId(userid, app, device).getRuntimePropertyStore().get(RuntimeProperty.createIdText(id,index));
		if (property == null) {
			System.err.println(id);
		}
		
		LightProperty ret = new LightProperty();
		ret.currentValue = property.getCurrentValue();
		ret.selectedListTitle = property.getSelectedListTitle();
		return ret;
	}
	
	@POST
	@Path("/{device}/setValueBySystem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN) 
	public String setValueBySystem(@QueryParam("userid") String userid, @PathParam("app") String app, 
			@PathParam("device") String device, String json) {
		try {
			LightProperty prop = new ObjectMapper().readValue(json, LightProperty.class);
			SilverBulletServer.getStaticInstance().getBuilderModelByUserId(userid, app, device)
				.requestChangeBySystem(new Id(prop.id, 0, device), prop.currentValue);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return "OK";
	}
	
	@POST
	@Path("/{device}/postValueBySystem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String postValue(@QueryParam("userid") String userid, 
			@PathParam("app") String app, @PathParam("device") String device, 
			@QueryParam("id") String id, @QueryParam("index") Integer index, 
			@QueryParam("name") String name, @QueryParam("classname") String classname, String json) {
//		System.out.println("postValue " + object);
		try {
			Object object = new ObjectMapper().readValue(json, Class.forName(classname));
			return SilverBulletServer.getStaticInstance().getBuilderModelByUserId(userid, app, device)
					.requestBlobChangeBySystem(new Id(id, index, device), object, name);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return "OK";
	}
	
	@GET
	@Path("/{device}/getUiEntry")
	@Produces(MediaType.TEXT_PLAIN) 
	public String getUiEntry(@PathParam("app") String app, @PathParam("device") String device) {
		if (device.startsWith("MT")) {
			return "MAIN";
		}
		return device.replaceAll("[0-9]", "");
	}
	
	@GET
	@Path("/{device}/download")
	@Produces(MediaType.TEXT_PLAIN) 
	public String download(@PathParam("app") String app, @PathParam("device") String device, 
			@QueryParam("userid") String userid, @QueryParam("fileid") String fileid) {
		String access_token = SilverBulletServer.getStaticInstance().getUserStore().findByUseID(userid).getAccess_token();
		byte[] data = SystemResource.googleHandler.download(access_token, fileid);
		
		return new String(Base64.getEncoder().encode(data));
	}
	
	@GET
	@Path("/{device}/downloadCompleted")
	@Produces(MediaType.TEXT_PLAIN) 
	public Response downloadCompleted(@PathParam("app") String app, @PathParam("device") String device, 
			@QueryParam("userid") String userid, @QueryParam("fileid") String fileid) {
		String access_token = SilverBulletServer.getStaticInstance().getUserStore().findByUseID(userid).getAccess_token();
		SystemResource.googleHandler.downloadCompleted(access_token, fileid);
		
		return Response.ok().build();
	}	
	
	@POST
	@Path("/{device}/upload")
	@Produces(MediaType.APPLICATION_OCTET_STREAM) 
	public Response upload(@PathParam("app") String app, @PathParam("device") String device, 
			@QueryParam("userid") String userid, @QueryParam("filepath") String filepath, String base64) {
		String access_token = SilverBulletServer.getStaticInstance().getUserStore().findByUseID(userid).getAccess_token();
		
		String fileID = new GoogleDrivePost().type("application/octet-stream").base64(base64).post(access_token, filepath);
		return Response.ok().build();
	}
	
	@GET
	@Path("/{device}/pendingFiles")
	@Produces(MediaType.APPLICATION_JSON) 
	public FilePendingResponse download(@PathParam("app") String app, @PathParam("device") String device, 
			@QueryParam("userid") String userid) {
		String access_token = SilverBulletServer.getStaticInstance().getUserStore().findByUseID(userid).getAccess_token();
		
		List<com.google.api.services.drive.model.File> files = SystemResource.googleHandler.getFileList(access_token, "SilverBullet/Automated/" + device + "/toDevice");
		FilePendingResponse ret = new FilePendingResponse(files);
		return ret;
	}
}

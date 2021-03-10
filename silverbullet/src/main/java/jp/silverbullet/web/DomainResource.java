package jp.silverbullet.web;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
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

import org.apache.commons.lang3.StringEscapeUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.core.dependency2.Id;
import jp.silverbullet.core.property2.LightProperty;
import jp.silverbullet.core.property2.RuntimeProperty;
import jp.silverbullet.dev.Automator;
import jp.silverbullet.testspec.TestResultList;

@Path("/{app}/domain")
public class DomainResource {
	
	@GET
	@Path("/devices")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<DeviceProperty> getDevices(@CookieParam("SilverBullet") String cookie) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		List<DeviceProperty> ret = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getActiveDevices(userid);
//		escapeHtml(ret);
		
		return ret;

	}
	private void escapeHtml(List<DeviceProperty> activeDevices) {
		for (DeviceProperty p : activeDevices) {
			p.setDeviceName(StringEscapeUtils.escapeHtml3(p.getDeviceName()));
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
	public List<String> selectDevice(@CookieParam("SilverBullet") String cookie, @QueryParam("name") String name) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		Automator automator = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getAutomator(userid);
		if (name != null) {
			automator.loadScript(name);
		}
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

	
	@GET
	@Path("/{device}/{serialNo}/login")
	@Produces(MediaType.TEXT_PLAIN) 
	public String longin(@QueryParam("userid") String userid, @PathParam("app") String app, @PathParam("serialNo") String serialNo, 
			@PathParam("device") String device, @Context HttpServletRequest request) {
		System.out.println("login: + " + app + "@" + device);
		SilverBulletServer.getStaticInstance().createDevice(userid, app, new DeviceName().generate(device, serialNo));
		return "OK";
	}

	@GET
	@Path("/{device}/{serialNo}/logout")
	@Produces(MediaType.TEXT_PLAIN) 
	public String logout(@QueryParam("userid") String userid, @PathParam("app") String app, @PathParam("device") String device, @PathParam("serialNo") String serialNo) {
		System.out.println("logout: + " + app + "@" + device);
		SilverBulletServer.getStaticInstance().deleteDevice(userid, app, new DeviceName().generate(device, serialNo));
		return "OK";
	}
	

	@GET
	@Path("/autoScriptListFromDevice")
	@Produces(MediaType.APPLICATION_JSON) 
	public ListStringClass autoScriptListFromDevice(@QueryParam("userid") String userid) {
		Automator automator = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getAutomator(userid);
		List<String> list = automator.scriptList();
		ListStringClass ret = new ListStringClass();
		ret.list = list;
		return ret;
	}
	
	@GET
	@Path("/autoScriptFromDevice")
	@Produces(MediaType.APPLICATION_JSON) 
	public ListStringClass autoScriptListFromDevice(@QueryParam("userid") String userid, @QueryParam("name") String name) {
		Automator automator = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getAutomator(userid);
		automator.loadScript(name);
		ListStringClass ret = new ListStringClass();
		ret.list = automator.getLines();
		return ret;
	}

	
	@GET
	@Path("/{projectName}/resultListFromDevice")
	@Produces(MediaType.APPLICATION_JSON) 
	public TestResultList resultList(@PathParam("projectName") String projectName, @QueryParam("userid") String userid, @QueryParam("name") String name) {

		String path = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getStorePath(userid) + "/" + projectName;
		
		TestResultList ret = new TestResultList();
		for (File file : new File(path).listFiles()) {
			String filename = file.getName();
			String portId = filename.split("\\.")[1];
			String testMethod = filename.split("\\.")[4];
			
			ret.add(portId, testMethod);
		}
		
		return ret;
		
	}
	
	
	@GET
	@Path("/{device}/{serialNo}/getProperty")
	@Produces(MediaType.APPLICATION_JSON) 
	public LightProperty getProperty(@QueryParam("userid") String userid, @PathParam("app") String app, @PathParam("device") String device, @PathParam("serialNo") String serialNo, 
			@QueryParam("id") String id, @QueryParam("index") Integer index, @QueryParam("ext") String ext) {
		RuntimeProperty property = SilverBulletServer.getStaticInstance().getBuilderModelByUserId(userid, app, new DeviceName().generate(device, serialNo)).getRuntimePropertyStore().get(RuntimeProperty.createIdText(id,index));
		if (property == null) {
			System.err.println(id);
		}
		
		LightProperty ret = new LightProperty();
		ret.currentValue = property.getCurrentValue();
		ret.selectedListTitle = property.getSelectedListTitle();
		return ret;
	}
	
	@POST
	@Path("/{device}/{serialNo}/setValueBySystem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN) 
	public String setValueBySystem(@QueryParam("userid") String userid, @PathParam("app") String app, @PathParam("serialNo") String serialNo, 
			@PathParam("device") String device, String json) {
		try {
			LightProperty prop = new ObjectMapper().readValue(json, LightProperty.class);
			SilverBulletServer.getStaticInstance().getBuilderModelByUserId(userid, app, new DeviceName().generate(device, serialNo))
				.requestChangeBySystem(new Id(prop.id, 0, device), prop.currentValue);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return "OK";
	}
	
	@POST
	@Path("/{device}/{serialNo}/postValueBySystem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String postValue(@QueryParam("userid") String userid, 
			@PathParam("app") String app, @PathParam("device") String device, @PathParam("serialNo") String serialNo, 
			@QueryParam("id") String id, @QueryParam("index") Integer index, 
			@QueryParam("name") String name, @QueryParam("classname") String classname, String json) {
//		System.out.println("postValue " + object);
		try {
			Object object = new ObjectMapper().readValue(json, Class.forName(classname));
			return SilverBulletServer.getStaticInstance().getBuilderModelByUserId(userid, app, new DeviceName().generate(device, serialNo))
					.requestBlobChangeBySystem(new Id(id, index, new DeviceName().generate(device, serialNo)), object, name);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return "OK";
	}
	
//	private static Map<String, String> uiEntry = new HashMap<>();
	@GET
	@Path("/{device}/{serialNo}/getUiEntry")
	@Produces(MediaType.TEXT_PLAIN) 
	public String getUiEntry(@CookieParam("SilverBullet") String cookie, 
			@PathParam("app") String app, @PathParam("device") String device, @PathParam("serialNo") String serialNo) {
		
		String ret = SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getUiEntry(device);
//		String ret = uiEntry.get(device);
		if (ret == null) {
			ret = "";
		}
		return ret;
	}
		
	@GET
	@Path("/{device}/{serialNo}/setUiEntry")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setDeviceUi(@CookieParam("SilverBullet") String cookie, 
			@PathParam("app") String app, @PathParam("device") String device, @PathParam("serialNo") String serialNo, @QueryParam("ui") String ui) {
//		uiEntry.put(device, ui);
		SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).setUiEntry(device, ui);
		return "OK";
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
	@Path("/{device}/{serialNo}/upload")
	@Produces(MediaType.APPLICATION_OCTET_STREAM) 
	public Response upload(@PathParam("app") String app, @PathParam("device") String device, @PathParam("serialNo") String serialNo, 
			@QueryParam("userid") String userid, @QueryParam("filepath") String filepath, String base64) {
//		String access_token = SilverBulletServer.getStaticInstance().getUserStore().findByUseID(userid).getAccess_token();
		
//		String fileID = new GoogleDrivePost().type("application/octet-stream").base64(base64).post(access_token, filepath);
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

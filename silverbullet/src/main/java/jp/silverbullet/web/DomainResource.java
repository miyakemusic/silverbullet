package jp.silverbullet.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.core.property2.LightProperty;
import jp.silverbullet.core.property2.RuntimeProperty;

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
	@Path("/{device}/login")
	@Produces(MediaType.TEXT_PLAIN) 
	public String longin(@QueryParam("userid") String userid, @PathParam("app") String app, @PathParam("device") String device) {
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
	public String setValueBySystem(@QueryParam("userid") String userid, @PathParam("app") String app, @PathParam("device") String device, 
			String json) {
		try {
			LightProperty prop = new ObjectMapper().readValue(json, LightProperty.class);
			SilverBulletServer.getStaticInstance().getBuilderModelByUserId(userid, app, device).requestChangeBySystem(prop.id, 0, prop.currentValue);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "OK";
	}
	
	@POST
	@Path("/{device}/postValueBySystem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String postValue(@QueryParam("userid") String userid, @PathParam("app") String app, @PathParam("device") String device, 
			@QueryParam("id") String id, @QueryParam("index") Integer index, 
			@QueryParam("name") String name, @QueryParam("classname") String classname, String json) {
//		System.out.println("postValue " + object);
		try {
			Object object = new ObjectMapper().readValue(json, Class.forName(classname));
			return SilverBulletServer.getStaticInstance().getBuilderModelByUserId(userid, app, device).requestBlobChangeBySystem(id, index, object, name);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return "OK";
	}
	
	@GET
	@Path("/{device}/getUiEntry")
	@Produces(MediaType.TEXT_PLAIN) 
	public String getUiEntry(@PathParam("app") String app, @PathParam("device") String device) {
		if (device.startsWith("OTDR")) {
			return "OTDR2";
		}
		else if (device.startsWith("OSC")) {
			return "OSC2";
		}
		else if (device.startsWith("COMBI")) {
			return "MAIN";
		}
		return "ERROR";
	}
}

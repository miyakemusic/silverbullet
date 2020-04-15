package jp.silverbullet.web;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.core.property2.LightProperty;
import jp.silverbullet.core.property2.RuntimeProperty;

@Path("/domain")
public class DomainResource {
	@GET
	@Path("/getProperty")
	@Produces(MediaType.APPLICATION_JSON) 
	public LightProperty getProperty(@QueryParam("id") String id, @QueryParam("index") Integer index, @QueryParam("ext") String ext) {
		RuntimeProperty property = SilverBulletServer.getStaticInstance().getBuilderModel().getRuntimePropertyStore().get(RuntimeProperty.createIdText(id,index));
		if (property == null) {
			System.err.println(id);
		}
		
		LightProperty ret = new LightProperty();
		ret.currentValue = property.getCurrentValue();
		ret.selectedListTitle = property.getSelectedListTitle();
		return ret;
	}
	
	@POST
	@Path("/setValueBySystem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN) 
	public String setValueBySystem(String json) {
		try {
			LightProperty prop = new ObjectMapper().readValue(json, LightProperty.class);
			SilverBulletServer.getStaticInstance().getBuilderModel().requestChangeBySystem(prop.id, 0, prop.currentValue);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "OK";
	}
	
	@POST
	@Path("/postValueBySystem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String postValue(@QueryParam("id") String id, @QueryParam("index") Integer index, 
			@QueryParam("name") String name, @QueryParam("classname") String classname, String json) {
//		System.out.println("postValue " + object);
		try {
			Object object = new ObjectMapper().readValue(json, Class.forName(classname));
			return SilverBulletServer.getStaticInstance().getBuilderModel().requestBlobChangeBySystem(id, index, object, name);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return "OK";
	}
}

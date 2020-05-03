package jp.silverbullet.web;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import jp.silverbullet.core.property2.PropertyFactory;
import jp.silverbullet.core.property2.PropertyHolder2;
import jp.silverbullet.core.property2.PropertyType2;

//@Path("/id2/{code}")
@Path("/{app}/id2")
public class IdResource2 {
	@GET
	@Path("/selection")
	@Produces(MediaType.APPLICATION_JSON) 
	public JsonTable getSelections(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @QueryParam("id") final String id) {
		JsonTable ret = new WebTableConverter(getPropertiesHolder(cookie, app)).createOptionTable(id);
		return ret;
	}
	
	@GET
	@Path("/addNew")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addNewProperty(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @QueryParam("type") final String type) {
		String id = "ID_" + Calendar.getInstance().getTime().getTime();
		getPropertiesHolder(cookie, app).addProperty(new PropertyFactory().create(id, PropertyType2.valueOf(type)));
		return "OK";
	}
	
	@GET
	@Path("/remove")
	@Produces(MediaType.TEXT_PLAIN) 
	public String remove(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @QueryParam("id") final String id) {
		getPropertiesHolder(cookie, app).remove(id);
		return "OK";
	}

	private PropertyHolder2 getPropertiesHolder(String cookie, String app) {
		return SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getPropertiesHolder2();
	}
	
	@GET
	@Path("/addChoice")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addNewChoice(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @QueryParam("id") final String id) {
		String optionId = id + "_" + Calendar.getInstance().getTime().getTime();
		try {
			getPropertiesHolder(cookie, app).addOption(id, optionId);
		} catch (Exception e) {
			return e.toString();
		}
		return "OK";
	}
	
	@GET
	@Path("/updateChoice")
	@Produces(MediaType.APPLICATION_JSON) 
	public String updateChoice(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @QueryParam("id") final String id, @QueryParam("selectionId") final String selectionId, 
			@QueryParam("paramName") final String paramName, @QueryParam("value") final String value) {
		
		WebTableConverter converter = new WebTableConverter(getPropertiesHolder(cookie, app));
		converter.updateOptionField(id, selectionId, paramName, value);
		return "OK";
	}
	
	@GET
	@Path("/properties")
	@Produces(MediaType.APPLICATION_JSON) 
	public Response test(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @QueryParam("type") final String type) {
		JsonTable table = new WebTableConverter(getPropertiesHolder(cookie, app)).
				createIdTable(PropertyType2.valueOf(type));
		
		Response res = Response
			      .status(Response.Status.OK)
			      .entity(table)
			      .build();
		return res;
	}

	@GET
	@Path("/update")
	@Produces(MediaType.TEXT_PLAIN) 
	public String updateValue(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @QueryParam("id") final String id, 
			@QueryParam("paramName") final String paramName,  
			@QueryParam("value") final String value) {
		
		WebTableConverter converter = new WebTableConverter(getPropertiesHolder(cookie, app));
		converter.updateMainField(id, paramName, value);

		return "OK";
	}
	
	@GET
	@Path("/typeNames")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getTypeNames(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app) {
		return getPropertiesHolder(cookie, app).getTypes();
	}

	@GET
	@Path("/ids")
	@Produces(MediaType.APPLICATION_JSON) 
	public Set<String> getIds(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app) {
		return getPropertiesHolder(cookie, app).getAllIds(PropertyType2.NotSpecified);
	}
}

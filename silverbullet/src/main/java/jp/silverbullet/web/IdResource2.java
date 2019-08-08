package jp.silverbullet.web;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import jp.silverbullet.SilverBulletServer;
import jp.silverbullet.property2.PropertyFactory;
import jp.silverbullet.property2.PropertyType2;
import jp.silverbullet.property2.WebTableConverter;

//@Path("/id2/{code}")
@Path("/id2")
public class IdResource2 {
	@GET
	@Path("/selection")
	@Produces(MediaType.APPLICATION_JSON) 
	public JsonTable getSelections(/*@PathParam("code") final String code, */@QueryParam("id") final String id) {
		JsonTable ret = new WebTableConverter(SilverBulletServer.getStaticInstance().getBuilderModel().getPropertiesHolder2()).createOptionTable(id);
		return ret;
	}
	
	@GET
	@Path("/addNew")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addNewProperty(@QueryParam("type") final String type) {
		String id = "ID_" + Calendar.getInstance().getTime().getTime();
		SilverBulletServer.getStaticInstance().getBuilderModel().getPropertiesHolder2().addProperty(new PropertyFactory().create(id, PropertyType2.valueOf(type)));
		return "OK";
	}
	
	@GET
	@Path("/remove")
	@Produces(MediaType.TEXT_PLAIN) 
	public String remove(@QueryParam("id") final String id) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getPropertiesHolder2().remove(id);
		return "OK";
	}
	
	@GET
	@Path("/addChoice")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addNewChoice(@QueryParam("id") final String id) {
		String optionId = id + "_" + Calendar.getInstance().getTime().getTime();
		try {
			SilverBulletServer.getStaticInstance().getBuilderModel().getPropertiesHolder2().get(id).option(optionId, "", "");
		} catch (Exception e) {
			return e.toString();
		}
		return "OK";
	}
	
	@GET
	@Path("/updateChoice")
	@Produces(MediaType.APPLICATION_JSON) 
	public String updateChoice(@QueryParam("id") final String id, @QueryParam("selectionId") final String selectionId, @QueryParam("paramName") final String paramName, @QueryParam("value") final String value) {
		WebTableConverter converter = new WebTableConverter(SilverBulletServer.getStaticInstance().getBuilderModel().getPropertiesHolder2());
		converter.updateOptionField(id, selectionId, paramName, value);
		return "OK";
	}
	
	@GET
	@Path("/properties")
	@Produces(MediaType.APPLICATION_JSON) 
	public Response test(@QueryParam("type") final String type) {
		JsonTable table = new WebTableConverter(SilverBulletServer.getStaticInstance().getBuilderModel().getPropertiesHolder2()).
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
	public String updateValue(@QueryParam("id") final String id, 
			@QueryParam("paramName") final String paramName,  
			@QueryParam("value") final String value) {
		
		WebTableConverter converter = new WebTableConverter(SilverBulletServer.getStaticInstance().getBuilderModel().getPropertiesHolder2());
		converter.updateMainField(id, paramName, value);

		return "OK";
	}
	
	@GET
	@Path("/typeNames")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getTypeNames() {
		return SilverBulletServer.getStaticInstance().getBuilderModel().getPropertiesHolder2().getTypes();
	}

	@GET
	@Path("/ids")
	@Produces(MediaType.APPLICATION_JSON) 
	public Set<String> getIds() {
		return SilverBulletServer.getStaticInstance().getBuilderModel().
				getPropertiesHolder2().getAllIds(PropertyType2.NotSpecified);
	}
}

package jp.silverbullet.web;

import java.util.Calendar;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.property2.PropertyFactory;
import jp.silverbullet.property2.PropertyType2;
import jp.silverbullet.property2.WebTableConverter;


@Path("/id2")
public class IdResource2 {
	@GET
	@Path("/selection")
	@Produces(MediaType.APPLICATION_JSON) 
	public JsonTable getSelections(@QueryParam("id") final String id) {
		JsonTable ret = new WebTableConverter(StaticInstances.getInstance().getBuilderModel().getPropertiesHolder2()).createOptionTable(id);
		return ret;
	}
	
	@GET
	@Path("/addNew")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addNewProperty(@QueryParam("type") final String type) {
		String id = "ID_" + Calendar.getInstance().getTime().getTime();
		StaticInstances.getInstance().getBuilderModel().getPropertiesHolder2().addProperty(new PropertyFactory().create(id, PropertyType2.valueOf(type)));
		return "OK";
	}
	
	@GET
	@Path("/remove")
	@Produces(MediaType.TEXT_PLAIN) 
	public String remove(@QueryParam("id") final String id) {
		StaticInstances.getInstance().getBuilderModel().getPropertiesHolder2().remove(id);
		return "OK";
	}
	
	@GET
	@Path("/addChoice")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addNewChoice(@QueryParam("id") final String id) {
		String optionId = id + "_" + Calendar.getInstance().getTime().getTime();
		try {
			StaticInstances.getInstance().getBuilderModel().getPropertiesHolder2().get(id).option(optionId, "", "");
		} catch (Exception e) {
			return e.toString();
		}
		return "OK";
	}
	
	@GET
	@Path("/updateChoice")
	@Produces(MediaType.APPLICATION_JSON) 
	public String updateChoice(@QueryParam("id") final String id, @QueryParam("selectionId") final String selectionId, @QueryParam("paramName") final String paramName, @QueryParam("value") final String value) {
		WebTableConverter converter = new WebTableConverter(StaticInstances.getInstance().getBuilderModel().getPropertiesHolder2());
		converter.updateOptionField(id, selectionId, paramName, value);
		return "OK";
	}
	
	@GET
	@Path("/properties")
	@Produces(MediaType.APPLICATION_JSON) 
	public JsonTable test(@QueryParam("type") final String type) {
		return new WebTableConverter(StaticInstances.getInstance().getBuilderModel().getPropertiesHolder2()).createIdTable(PropertyType2.valueOf(type));
	}

	@GET
	@Path("/update")
	@Produces(MediaType.TEXT_PLAIN) 
	public String updateValue(@QueryParam("id") final String id, 
			@QueryParam("paramName") final String paramName,  
			@QueryParam("value") final String value) {
		
		WebTableConverter converter = new WebTableConverter(StaticInstances.getInstance().getBuilderModel().getPropertiesHolder2());
		converter.updateMainField(id, paramName, value);

		return "OK";
	}
	
	@GET
	@Path("/typeNames")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getTypeNames() {
		return StaticInstances.getInstance().getBuilderModel().getPropertiesHolder2().getTypes();
	}
	
}

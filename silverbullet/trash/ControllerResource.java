package jp.silverbullet.web;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.handlers.HandlerProperty;
import jp.silverbullet.property2.PropertyType2;

@Path("/controller")
public class ControllerResource {

	@GET
	@Path("/getSpecs")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<HandlerProperty> getSpecs() {
		return StaticInstances.getInstance().getBuilderModel().getHandlerPropertyHolder().getHandlers();
	}
	
	@GET
	@Path("/remove")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<HandlerProperty> removeRow(@QueryParam("row") int row) {
		List<HandlerProperty> ret = StaticInstances.getInstance().getBuilderModel().getHandlerPropertyHolder().remove(row);
		StaticInstances.getInstance().save();
		return ret;
	}
	
	@GET
	@Path("/update")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<HandlerProperty> updateValue(@QueryParam("row") int row, @QueryParam("name") String name, @QueryParam("value") String value) {
		HandlerProperty data = StaticInstances.getInstance().getBuilderModel().getHandlerPropertyHolder().getHandlers().get(row);
		if (name.equals("name")) {
			data.setName(value);
		}
		else if (name.equals("description")) {
			data.setDescription(value);
		}
		else if (name.equals("ids")) {
			data.setIds(Arrays.asList(value.split(",")));
		}
		else if (name.equals("async")) {
			data.setAsync(Boolean.valueOf(value));
		}
		else if (name.equals("externalClass")) {
			data.setExternalClass(value);
		}
		StaticInstances.getInstance().save();
		return getSpecs();
	}
	
	@GET
	@Path("/addNew")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<HandlerProperty> updateValue() {
		String id = StaticInstances.getInstance().getBuilderModel().getPropertiesHolder2().getAllIds(PropertyType2.NotSpecified).iterator().next();
		StaticInstances.getInstance().getBuilderModel().getHandlerPropertyHolder().addHandler("New", "---", true, id);
		return getSpecs();
	}
}

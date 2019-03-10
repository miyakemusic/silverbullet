package jp.silverbullet.web;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.dependency2.design.RestrictionMatrix;

@Path("/dependencyDesign")
public class DependencyDesignResource {
	@GET
	@Path("/getSpec")
	@Produces(MediaType.APPLICATION_JSON) 
	public RestrictionMatrix getSpec() {
		return StaticInstances.getInstance().getBuilderModel().getDependencyDesigner();
	}
	
	@GET
	@Path("/getTriggers")
	@Produces(MediaType.APPLICATION_JSON) 
	public Set<String> getTrigger() {
		return StaticInstances.getInstance().getBuilderModel().getDependencyDesigner().getTriggers();
	}
	
	@GET
	@Path("/getTargets")
	@Produces(MediaType.APPLICATION_JSON) 
	public Set<String> getTarget() {
		return StaticInstances.getInstance().getBuilderModel().getDependencyDesigner().getTargets();
	}
	
	@GET
	@Path("/changeSpec")
	@Produces(MediaType.TEXT_PLAIN) 
	public String changeSpec(@QueryParam("row") final int row, @QueryParam("col") final int col, @QueryParam("checked") final boolean checked) {
		StaticInstances.getInstance().getBuilderModel().getDependencyDesigner().updateEnabled(row, col, checked);
		return "OK";
	}
	
	@GET
	@Path("/setCombination")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setCombination(@QueryParam("trigger") final String trigger, @QueryParam("target") final String target) {
		//StaticInstances.getInstance().getBuilderModel().getDependencyDesigner().setCombination(trigger, target);
		return "OK";
	}
	
	@GET
	@Path("/build")
	@Produces(MediaType.TEXT_PLAIN) 
	public String build() {
		StaticInstances.getInstance().getBuilderModel().getDependencyDesigner().build();
		return "OK";
	}
	
	@GET
	@Path("/switch")
	@Produces(MediaType.TEXT_PLAIN) 
	public String switchTriggerTarget() {
		StaticInstances.getInstance().getBuilderModel().getDependencyDesigner().switchTriggerTarget();
		return "OK";
	}
	
	
	@GET
	@Path("/alwaysTrue")
	@Produces(MediaType.TEXT_PLAIN) 
	public String alwaysTrue() {
		StaticInstances.getInstance().getBuilderModel().getDependencyDesigner().alwaysTrue();
		return "OK";
	}
	
	@GET
	@Path("/addId")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addId(@QueryParam("id") final String id, @QueryParam("type") final String type) {
		RestrictionMatrix.AxisType axisType = convertAxisType(type);	
		StaticInstances.getInstance().getBuilderModel().getDependencyDesigner().add(id, axisType);
		return "OK";
	}

	private RestrictionMatrix.AxisType convertAxisType(final String type) {
		RestrictionMatrix.AxisType axisType = null;
		if (type.equals("trigger")) {
			axisType = RestrictionMatrix.AxisType.X;
		}
		else if (type.equals("target")) {
			axisType = RestrictionMatrix.AxisType.Y;
		}
		return axisType;
	}
	
	@GET
	@Path("/removeId")
	@Produces(MediaType.TEXT_PLAIN) 
	public String removeId(@QueryParam("id") final String id, @QueryParam("type") final String type) {
		StaticInstances.getInstance().getBuilderModel().getDependencyDesigner().hide(id, convertAxisType(type));
		return "OK";
	}
	
	@GET
	@Path("/showAll")
	@Produces(MediaType.TEXT_PLAIN) 
	public String showAll() {
		StaticInstances.getInstance().getBuilderModel().getDependencyDesigner().showAll();
		return "OK";
	}
	
	@GET
	@Path("/getPriorities")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<KeyValue> getPriorities() {
		return StaticInstances.getInstance().getBuilderModel().getDependencyDesigner().getPriorities();
	}
	
	@GET
	@Path("/setPriority")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setPriority(@QueryParam("id") final String id, @QueryParam("value") final Integer value) {
		StaticInstances.getInstance().getBuilderModel().getDependencyDesigner().setPriority(id, value);
		return "OK";
	}
}

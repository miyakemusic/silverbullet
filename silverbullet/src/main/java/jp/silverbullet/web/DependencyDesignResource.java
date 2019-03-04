package jp.silverbullet.web;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.dependency2.design.RestrictionMatrix;

@Path("/dependencyDesign")
public class DependencyDesignResource {
	@GET
	@Path("/getSpec")
	@Produces(MediaType.APPLICATION_JSON) 
	public RestrictionMatrix getSpec() {
		return RestrictionMatrix.getInstance();
	}
	
	@GET
	@Path("/getTriggers")
	@Produces(MediaType.APPLICATION_JSON) 
	public Set<String> getTrigger() {
		return RestrictionMatrix.getInstance().getTriggers();
	}
	
	@GET
	@Path("/getTargets")
	@Produces(MediaType.APPLICATION_JSON) 
	public Set<String> getTarget() {
		return RestrictionMatrix.getInstance().getTargets();
	}
	
	@GET
	@Path("/changeSpec")
	@Produces(MediaType.TEXT_PLAIN) 
	public String changeSpec(@QueryParam("row") final int row, @QueryParam("col") final int col, @QueryParam("checked") final boolean checked) {
		RestrictionMatrix.getInstance().updateEnabled(row, col, checked);
		return "OK";
	}
	
	@GET
	@Path("/setCombination")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setCombination(@QueryParam("trigger") final String trigger, @QueryParam("target") final String target) {
		//RestrictionMatrix.getInstance().setCombination(trigger, target);
		return "OK";
	}
	
	@GET
	@Path("/build")
	@Produces(MediaType.TEXT_PLAIN) 
	public String build() {
		RestrictionMatrix.getInstance().build();
		return "OK";
	}
	
	@GET
	@Path("/switch")
	@Produces(MediaType.TEXT_PLAIN) 
	public String switchTriggerTarget() {
		RestrictionMatrix.getInstance().switchTriggerTarget();
		return "OK";
	}
	
	
	@GET
	@Path("/alwaysTrue")
	@Produces(MediaType.TEXT_PLAIN) 
	public String alwaysTrue() {
		RestrictionMatrix.getInstance().alwaysTrue();
		return "OK";
	}
	
	@GET
	@Path("/addId")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addId(@QueryParam("id") final String id, @QueryParam("type") final String type) {
		RestrictionMatrix.getInstance().add(id, type);
		return "OK";
	}
	
	@GET
	@Path("/removeId")
	@Produces(MediaType.TEXT_PLAIN) 
	public String removeId(@QueryParam("id") final String id, @QueryParam("type") final String type) {
		RestrictionMatrix.getInstance().hide(id, type);
		return "OK";
	}
	
	@GET
	@Path("/showAll")
	@Produces(MediaType.TEXT_PLAIN) 
	public String showAll() {
		RestrictionMatrix.getInstance().showAll();
		return "OK";
	}
	
	@GET
	@Path("/getPriorities")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<KeyValue> getPriorities() {
		return RestrictionMatrix.getInstance().getPriorities();
	}
	
	@GET
	@Path("/setPriority")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setPriority(@QueryParam("id") final String id, @QueryParam("value") final Integer value) {
		RestrictionMatrix.getInstance().setPriority(id, value);
		return "OK";
	}
}

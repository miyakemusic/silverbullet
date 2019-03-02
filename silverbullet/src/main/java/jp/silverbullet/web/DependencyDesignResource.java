package jp.silverbullet.web;

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
	@Path("/getTrigger")
	@Produces(MediaType.TEXT_PLAIN) 
	public String getTrigger() {
		return RestrictionMatrix.getInstance().getTrigger();
	}
	
	@GET
	@Path("/getTarget")
	@Produces(MediaType.TEXT_PLAIN) 
	public String getTarget() {
		return RestrictionMatrix.getInstance().getTarget();
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
		RestrictionMatrix.getInstance().setCombination(trigger, target);
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
}

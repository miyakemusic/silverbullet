package jp.silverbullet.web.trash;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.core.dependency2.design.DependencyDesignConfig;
import jp.silverbullet.core.dependency2.design.RestrictionMatrix;
import jp.silverbullet.dev.StaticInstances;
import jp.silverbullet.web.SilverBulletServer;

@Path("/dependencyDesign")
public class DependencyDesignResource {
	@GET
	@Path("/getSpec")
	@Produces(MediaType.APPLICATION_JSON) 
	public RestrictionMatrix getEnableSpec() {
		//return SilverBulletServer.getStaticInstance().getBuilderModel().getRestrictionMatrix();
		return null;
	}
	
	
	@GET
	@Path("/getTriggers")
	@Produces(MediaType.APPLICATION_JSON) 
	public Set<String> getTrigger() {
		//return SilverBulletServer.getStaticInstance().getBuilderModel().getRestrictionMatrix().getTriggers();
		return null;
	}
	
	@GET
	@Path("/getTargets")
	@Produces(MediaType.APPLICATION_JSON) 
	public Set<String> getTarget() {
		//return SilverBulletServer.getStaticInstance().getBuilderModel().getRestrictionMatrix().getTargets();
		return null;
	}
	
	@GET
	@Path("/changeSpec")
	@Produces(MediaType.TEXT_PLAIN) 
	public String changeSpec(@QueryParam("row") final int row, @QueryParam("col") final int col, @QueryParam("checked") final boolean checked) {
//		SilverBulletServer.getStaticInstance().getBuilderModel().getRestrictionMatrix().updateEnabled(row, col, checked);
		return "OK";
	}
	
	@GET
	@Path("/changeSpecValue")
	@Produces(MediaType.TEXT_PLAIN) 
	public String changeSpecValue(@QueryParam("row") final int row, @QueryParam("col") final int col, 
			@QueryParam("value") @Encoded final String value) {
//		SilverBulletServer.getStaticInstance().getBuilderModel().getRestrictionMatrix().updateValue(row, col, value);
		return "OK";
	}
	
	@GET
	@Path("/build")
	@Produces(MediaType.TEXT_PLAIN) 
	public String build() {
//		SilverBulletServer.getStaticInstance().getBuilderModel().getRestrictionMatrix().build();
		return "OK";
	}
	
	@GET
	@Path("/switch")
	@Produces(MediaType.TEXT_PLAIN) 
	public String switchTriggerTarget() {
//		SilverBulletServer.getStaticInstance().getBuilderModel().getRestrictionMatrix().switchTriggerTarget();
		return "OK";
	}
		
	@GET
	@Path("/addId")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addId(@QueryParam("id") final String id, @QueryParam("type") final String type) {
//		RestrictionMatrix.AxisType axisType = convertAxisType(type);	
//		SilverBulletServer.getStaticInstance().getBuilderModel().getRestrictionMatrix().add(id, axisType);
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
//		SilverBulletServer.getStaticInstance().getBuilderModel().getRestrictionMatrix().hide(id, convertAxisType(type));
		return "OK";
	}
	
	@GET
	@Path("/showAll")
	@Produces(MediaType.TEXT_PLAIN) 
	public String showAll() {
//		SilverBulletServer.getStaticInstance().getBuilderModel().getRestrictionMatrix().showAll();
		return "OK";
	}
	@GET
	@Path("/getDefinedPriorities")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<Integer> getDefinedPriorities() {
		//return SilverBulletServer.getStaticInstance().getBuilderModel().getRestrictionMatrix().getDefinedPriorities();
		return null;
	}
	
	@GET
	@Path("/getPriorities")
	@Produces(MediaType.APPLICATION_JSON) 
	public Map<Integer, List<String>> getPriorities() {
		//return SilverBulletServer.getStaticInstance().getBuilderModel().getRestrictionMatrix().getPriorities();
		return null;
	}
	
	@GET
	@Path("/setPriority")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setPriority(@QueryParam("id") final String id, @QueryParam("value") final Integer value) {
//		SilverBulletServer.getStaticInstance().getBuilderModel().getRestrictionMatrix().setPriority(id, value);
//		SilverBulletServer.getStaticInstance().getBuilderModel().getRestrictionMatrix().build();
		return "OK";
	}
}

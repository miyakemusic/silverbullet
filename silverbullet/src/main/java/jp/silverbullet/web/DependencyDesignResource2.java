package jp.silverbullet.web;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.dev.dependency2.design.DependencyDesignConfig;
import jp.silverbullet.dev.dependency2.design.RestrictionMatrix;

@Path("/{app}/dependencyDesign2")
public class DependencyDesignResource2 {
	//
	@GET
	@Path("getDependencyDesignConfig")
	@Produces(MediaType.APPLICATION_JSON)
	public DependencyDesignConfig getDependencyDesignConfig(@PathParam("app") String app, @QueryParam("name") final String name) {
		return SilverBulletServer.getStaticInstance().getBuilderModel(app).getDependencyDesigner().getDependencyDesignConfig(name);
	}
	
	@GET
	@Path("getDependencyDesignConfigList")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getDependencyDesignConfigList(@PathParam("app") String app) {
		return SilverBulletServer.getStaticInstance().getBuilderModel(app).getDependencyDesigner().getConfigList();
	}
	
	@GET
	@Path("updateDependencyDesignConfig")
	@Produces(MediaType.TEXT_PLAIN)
	public String updateDependencyDesignConfig(@PathParam("app") String app, @QueryParam("name") final String name, @QueryParam("triggers") final String triggers,
			@QueryParam("targets") final String targets) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getDependencyDesigner().updateConfig(name, triggers, targets);
		return "OK";
	}
	
	@GET
	@Path("getMatrix")
	@Produces(MediaType.APPLICATION_JSON)
	public RestrictionMatrix getMatrix(@PathParam("app") String app, @QueryParam("triggers") final String triggers, @QueryParam("targets") final String targets) {
		return SilverBulletServer.getStaticInstance().getBuilderModel(app).getDependencyDesigner().getMatrix(triggers, targets);
	}
	
	@GET
	@Path("setSpecValue")
	@Produces(MediaType.TEXT_PLAIN)
	public String setSpecValue(@PathParam("app") String app, @QueryParam("trigger") final String trigger, @QueryParam("target") final String target, 
			@QueryParam("value") /*@Encoded*/ final String value) {
//		String val = URLDecoder.decode(value);
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getDependencyDesigner().setSpecValue(trigger, target, value);
		return "OK";
	}

	@GET
	@Path("setSpecValueCondition")
	@Produces(MediaType.TEXT_PLAIN)
	public String setSpecValueCondition(@PathParam("app") String app, @QueryParam("trigger") final String trigger, @QueryParam("target") final String target, 
			@QueryParam("condition") /*@Encoded*/ final String condition) {
//		String val = URLDecoder.decode(value);
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getDependencyDesigner().setSpecValueCondition(trigger, target, condition);
		return "OK";
	}
	
	@GET
	@Path("setBlockPropagation")
	@Produces(MediaType.TEXT_PLAIN)
	public String setBlockPropagation(@PathParam("app") String app, @QueryParam("trigger") final String trigger, @QueryParam("target") final String target, 
			@QueryParam("enabled") final Boolean enabled) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getDependencyDesigner().setSpecValueBlockPropagation(trigger, target, enabled);
		
		return "OK";
	}
	
	@GET
	@Path("setSpecEnabled")
	@Produces(MediaType.TEXT_PLAIN)
	public String setSpecEnabled(@PathParam("app") String app, @QueryParam("trigger") final String trigger, @QueryParam("target") final String target, 
			@QueryParam("enabled") final Boolean enabled) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getDependencyDesigner().setSpecEnabled(trigger, target, enabled);
		
		return "OK";
	}
	
	@GET
	@Path("/getDefinedPriorities")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<Integer> getDefinedPriorities(@PathParam("app") String app) {
		return SilverBulletServer.getStaticInstance().getBuilderModel(app).getDependencyDesigner().getDefinedPriorities();
	}
	
	@GET
	@Path("/getPriorities")
	@Produces(MediaType.APPLICATION_JSON) 
	public Map<Integer, List<String>> getPriorities(@PathParam("app") String app) {
		return SilverBulletServer.getStaticInstance().getBuilderModel(app).getDependencyDesigner().getPriorities();
	}
	
	@GET
	@Path("/setPriority")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setPriority(@PathParam("app") String app, @QueryParam("id") final String id, @QueryParam("value") final Integer value) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getDependencyDesigner().setPriority(id, value);
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getDependencyDesigner().buildSpec();
		return "OK";
	}
	
	@GET
	@Path("/confirmSamePriority")
	@Produces(MediaType.TEXT_PLAIN) 
	public String confirmSamePriority(@PathParam("app") String app, @QueryParam("enabled") final boolean enabled) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getDependencyDesigner().setConfirmSamePriority(enabled);
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getDependencyDesigner().buildSpec();
		return "OK";
	}
	

}

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
		return new RestrictionMatrix();
	}
	
	@GET
	@Path("/changeSpec")
	@Produces(MediaType.APPLICATION_JSON) 
	public String changeSpec(@QueryParam("row") final int row, @QueryParam("col") final int col, @QueryParam("checked") final boolean checked) {
		
		return "OK";
	}
}

package jp.silverbullet.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.StaticInstances;

@Path("/system")
public class SystemResource {
	@GET
	@Path("/save")
	@Produces(MediaType.TEXT_PLAIN) 
	public String Save() {
		StaticInstances.getInstance().save();
		return "OK";
	}
	
	@GET
	@Path("/generateSource")
	@Produces(MediaType.TEXT_PLAIN) 
	public String generateSource() {
		StaticInstances.getInstance().generateSource();
		return "OK";
	}
}

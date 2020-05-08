package jp.silverbullet.web;

import java.util.List;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/{app}/storage")
public class StorageResource {
	@GET
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON) 
	public Response add(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @QueryParam("id") String id) {
		SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getPersistent().add(id);
		
		return Response.ok().build();
	}
	
	@GET
	@Path("/ids")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getIds(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app) {
		return SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getPersistent().getList();
	}
	
	@GET
	@Path("/removeId")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getIds(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, 
			@QueryParam("id") String id) {
		return SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getPersistent().remove(id);
	}
}

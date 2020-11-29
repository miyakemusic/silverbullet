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
	@Path("/addTriger")
	@Produces(MediaType.APPLICATION_JSON) 
	public Response add(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @QueryParam("id") String id) {
		SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getPersistent().addTrigger(id);
		
		return Response.ok().build();
	}
	
	@GET
	@Path("/triggerIds")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> triggerIds(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app) {
		return SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getPersistent().triggerList();
	}
	
	@GET
	@Path("/addStoredId")
	@Produces(MediaType.APPLICATION_JSON) 
	public String addStoredId(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app,
			@QueryParam("triggerId") String triggerId, @QueryParam("storedId") String storedId) {
		SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getPersistent().addStoredIs(triggerId, storedId);
		return "OK";
	}
	
	@GET
	@Path("/storedIds")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> storedIds(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app,
			@QueryParam("triggerId") String triggerId) {
		return SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getPersistent().storedId(triggerId);
	}
	
	@GET
	@Path("/setPath")
	@Produces(MediaType.APPLICATION_JSON) 
	public String setPath(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app,
			@QueryParam("triggerId") String triggerId, @QueryParam("pathId") String pathId) {
		SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getPersistent().pathId(triggerId, pathId);
		return "OK";
	}
	
	@GET
	@Path("/path")
	@Produces(MediaType.TEXT_PLAIN) 
	public String path(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app,
			@QueryParam("triggerId") String triggerId) {
		return SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getPersistent().path(triggerId);
	}
	
	@GET
	@Path("/removeId")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getIds(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, 
			@QueryParam("id") String id) {
		return SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getPersistent().remove(id);
	}
}

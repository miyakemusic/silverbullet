package jp.silverbullet.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/{app}/selfbuild")
public class SelfBuildResource {
	@GET
	@Path("/setInfo")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setInfo(@PathParam("app") String app, @QueryParam("path") String path, @QueryParam("package") String packageName) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getSelfBuilder().setInfo(path, packageName);
		return "OK";
	}
	
	@GET
	@Path("/getPath")
	@Produces(MediaType.TEXT_PLAIN) 
	public String getPath(@PathParam("app") String app) {
		return SilverBulletServer.getStaticInstance().getBuilderModel().getSelfBuilder().getPath();
	}
	
	@GET
	@Path("/getPackage")
	@Produces(MediaType.TEXT_PLAIN) 
	public String getPackage(@PathParam("app") String app) {
		return SilverBulletServer.getStaticInstance().getBuilderModel().getSelfBuilder().getPackage();
	}
	
	@GET
	@Path("/build")
	@Produces(MediaType.TEXT_PLAIN) 
	public String build(@PathParam("app") String app) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getSelfBuilder().build();
		return "OK";
	}
}

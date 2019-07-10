package jp.silverbullet.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.SilverBulletServer;

@Path("/selfbuild")
public class SelfBuildResource {
	@GET
	@Path("/setInfo")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setInfo(@QueryParam("path") String path, @QueryParam("package") String packageName) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getSelfBuilder().setInfo(path, packageName);
		return "OK";
	}
	
	@GET
	@Path("/getPath")
	@Produces(MediaType.TEXT_PLAIN) 
	public String getPath() {
		return SilverBulletServer.getStaticInstance().getBuilderModel().getSelfBuilder().getPath();
	}
	
	@GET
	@Path("/getPackage")
	@Produces(MediaType.TEXT_PLAIN) 
	public String getPackage() {
		return SilverBulletServer.getStaticInstance().getBuilderModel().getSelfBuilder().getPackage();
	}
	
	@GET
	@Path("/build")
	@Produces(MediaType.TEXT_PLAIN) 
	public String build() {
		SilverBulletServer.getStaticInstance().getBuilderModel().getSelfBuilder().build();
		return "OK";
	}
}

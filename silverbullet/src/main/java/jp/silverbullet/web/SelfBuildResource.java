package jp.silverbullet.web;

import java.io.File;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.core.property2.SvFileException;

@Path("/{app}/selfbuild")
public class SelfBuildResource {
	@GET
	@Path("/setInfo")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setInfo(@PathParam("app") String app, @QueryParam("path") String path, @QueryParam("package") String packageName) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getSelfBuilder().setInfo(path, packageName);
		return "OK";
	}
	
	@GET
	@Path("/getPath")
	@Produces(MediaType.TEXT_PLAIN) 
	public String getPath(@PathParam("app") String app) {
		return SilverBulletServer.getStaticInstance().getBuilderModel(app).getSelfBuilder().getPath();
	}
	
	@GET
	@Path("/getPackage")
	@Produces(MediaType.TEXT_PLAIN) 
	public String getPackage(@PathParam("app") String app) {
		return SilverBulletServer.getStaticInstance().getBuilderModel(app).getSelfBuilder().getPackage();
	}
	
	@GET
	@Path("/build")
	@Produces(MediaType.TEXT_PLAIN) 
	public String build(@PathParam("app") String app) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getSelfBuilder().build();
		return "OK";
	}
	
	@GET
	@Path("/generateSource")
	@Produces(MediaType.TEXT_PLAIN) 
	public String generateSource(@PathParam("app") String app) {
		SilverBulletServer.getStaticInstance().generateSource(app);
		return "OK";
	}
	
	@GET
	@Path("/saveParameters")
	@Produces(MediaType.TEXT_PLAIN) 
	public String saveParameters(@PathParam("app") String app, @QueryParam("filename") final String filename) {
		try {
			SilverBulletServer.getStaticInstance().getBuilderModel(app).saveParameters(filename);
		} catch (SvFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "OK";
	}
	
	@GET
	@Path("/loadParameters")
	@Produces(MediaType.TEXT_PLAIN) 
	public String loadParameters(@PathParam("app") String app, @QueryParam("filename") final String filename) {
		try {
			SilverBulletServer.getStaticInstance().getBuilderModel(app).loadParameters(filename);
		} catch (SvFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "OK";
	}
	
	@GET
	@Path("/save")
	@Produces(MediaType.TEXT_PLAIN) 
	public String Save(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app) {
		String filename = SilverBulletServer.getStaticInstance().save(app);
		PersonalCookie r = SystemResource.userStore.findByCookie(cookie);
		
//		filename = "C:\\Users\\miyak\\git\\openti\\openti\\sv_tmp\\id_def.json";
		
		String contentType = "application/octet-stream";
		File file = new File(filename);
		
		SystemResource.googleHandler.postFile(r.personal.access_token, contentType, file);
//		SystemResource..(cookie, new File(filename));
		return "OK";
	}
}

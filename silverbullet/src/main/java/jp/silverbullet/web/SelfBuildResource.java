package jp.silverbullet.web;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import jp.silverbullet.core.property2.SvFileException;

@Path("/{app}/selfbuild")
public class SelfBuildResource {
	@GET
	@Path("/setInfo")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setInfo(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @QueryParam("path") String path, @QueryParam("package") String packageName) {
		SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getSelfBuilder().setInfo(path, packageName);
		return "OK";
	}
	
	@GET
	@Path("/getPath")
	@Produces(MediaType.TEXT_PLAIN) 
	public String getPath(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app) {
		return SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getSelfBuilder().getPath();
	}
	
	@GET
	@Path("/getPackage")
	@Produces(MediaType.TEXT_PLAIN) 
	public String getPackage(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app) {
		return SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getSelfBuilder().getPackage();
	}
	
	@GET
	@Path("/build")
	@Produces(MediaType.TEXT_PLAIN) 
	public String build(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app) {
		SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getSelfBuilder().build();
		return "OK";
	}
	
	@GET
	@Path("/generateSource")
	@Produces(MediaType.TEXT_PLAIN) 
	public String generateSource(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app) {
		SilverBulletServer.getStaticInstance().generateSource(cookie, app);
		return "OK";
	}
	
	@GET
	@Path("/saveParameters")
	@Produces(MediaType.TEXT_PLAIN) 
	public String saveParameters(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @QueryParam("filename") final String filename) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
			String[] tmp = filename.split("\\.");
			String fname  = tmp[0] + "_" + sdf.format(Calendar.getInstance().getTime()) + "." + tmp[1];
			SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).saveParameters(fname);		
			String access_token = SilverBulletServer.getStaticInstance().getUserStore().findBySessionID(cookie).getPersonal().getAccess_token();

			String fileID = new GoogleDrivePost().type("application/json").type("text/plain").filename(fname)
					.post(access_token, "/SilverBullet/Applications/" + app + "/parameters/" + fname);
		} catch (SvFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "OK";
	}
	
	@GET
	@Path("/loadParameters")
	@Produces(MediaType.TEXT_PLAIN) 
	public String loadParameters(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @QueryParam("filename") final String filename) {
		try {
			SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).loadParameters(filename);
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
		UserStore userStore = SilverBulletServer.getStaticInstance().getUserStore();
		
		String filename = SilverBulletServer.getStaticInstance().save(cookie, app);
		PersonalCookie r = userStore.findBySessionID(cookie);
		
//		filename = "C:\\Users\\miyak\\git\\openti\\openti\\sv_tmp\\id_def.json";
		
		String contentType = "application/octet-stream";
		File file = new File(filename);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dstring = sdf.format(Calendar.getInstance().getTime());
		//String name =file.getName();
		//String fname = name.split("\\.")[0] + "(" + dstring + ")." + name.split("\\.")[1];
		SystemResource.googleHandler.postFile(r.getPersonal().access_token, 
				contentType, "SilverBullet/Applications/" + app + "/" + dstring + ".zip", file);
//		SystemResource..(cookie, new File(filename));
		return "OK";
	}
}

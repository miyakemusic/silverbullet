package jp.silverbullet.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.SilverBulletServer;
import jp.silverbullet.StaticInstances;
import jp.silverbullet.property2.SvFileException;

@Path("/system")
public class SystemResource {
	@GET
	@Path("/save")
	@Produces(MediaType.TEXT_PLAIN) 
	public String Save() {
		SilverBulletServer.getStaticInstance().save();
		return "OK";
	}
	
	@GET
	@Path("/generateSource")
	@Produces(MediaType.TEXT_PLAIN) 
	public String generateSource() {
		SilverBulletServer.getStaticInstance().generateSource();
		return "OK";
	}
	
	@GET
	@Path("/saveParameters")
	@Produces(MediaType.TEXT_PLAIN) 
	public String saveParameters(@QueryParam("filename") final String filename) {
		try {
			SilverBulletServer.getStaticInstance().getBuilderModel().saveParameters(filename);
		} catch (SvFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "OK";
	}
	
	@GET
	@Path("/loadParameters")
	@Produces(MediaType.TEXT_PLAIN) 
	public String loadParameters(@QueryParam("filename") final String filename) {
		try {
			SilverBulletServer.getStaticInstance().getBuilderModel().loadParameters(filename);
		} catch (SvFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "OK";
	}
}

package jp.silverbullet.web;

import java.io.File;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.dev.SbFiles;

@Path("/open")
public class OpenResource {
	@GET
	@Path("/{filename}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM) 
	public File getIdFile(@PathParam("filename") String filename) {        
		//String dir = getClass().getClassLoader().getResource("/").toExternalForm();
		String dir = SbFiles.PERSISTENT_FOLDER + "/Default00/";
		File file = new File(dir + filename);
		
		return file;
	}
}

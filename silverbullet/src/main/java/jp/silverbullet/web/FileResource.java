package jp.silverbullet.web;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.core.KeyValue;

@Path("/config")
public class FileResource {

	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<KeyValue> getIdFile() {
		List<KeyValue> files = new ArrayList<>();
		for (File file : new File("sv_tmp").listFiles()) {
			try {
				StringBuilder lines = new StringBuilder();;
				for (String line : Files.readAllLines(Paths.get(file.getAbsolutePath()))) {
					lines.append(line);
				}
				files.add(new KeyValue(file.getName(), lines.toString()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return files;
	}
}

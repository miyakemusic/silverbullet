package jp.silverbullet.web;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.core.dependency2.IdValue;
import jp.silverbullet.core.property2.IdValues;

@Path("/{app}/report")
public class ReportResource {
	@GET
	@Path("/getReport")
	@Produces(MediaType.TEXT_HTML) 
	public String getReport() {
		String path  = "C:\\Users\\miyak\\git\\silverbullet\\silverbullet\\2020-12-06\\";
		
		String html = "<HTML><BODY>";
		
		for (File file : new File(path).listFiles()) {
			if (file.getName().endsWith(".json")) {
				try {
					html += "<div>**** " + file.getName() + " *****</div>";
					String json = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
					IdValues v = new ObjectMapper().readValue(json, IdValues.class);
					for (IdValue idValue :  v.idValue) {
						html += "<div>" + idValue.getId() + ":" + idValue.getValue() + "</div>";
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		html += "<table><rt>";
		int i = 0;
		for (File file : new File(path).listFiles()) {
			if (file.getName().endsWith(".png")) {
				html += "<td>";
				html += "<image src=\"" + "rest/app/report/image/" + file.getName() + "\" width=\"300px\" height=\"200px\">";// + "\"" +  "" + "\"" + ">";
				html += "</td>";
				i++;
				if (i%2 == 0) {
					html += "</tr><tr>";
				}
			}
		}
		html += "</tr></table>";
		html += "</BODY></HTML>";
		return html;
	}
	
	@GET
	@Path("/image/{filename}")
	@Produces("image/png") 
	public Response image(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @PathParam("filename") String filename) {
		String path  = "C:\\Users\\miyak\\git\\silverbullet\\silverbullet\\2020-12-06\\";
		path += filename;  
		
		try {
			BufferedImage image = ImageIO.read(new File(path));
			return Response.ok(image).build();	
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}

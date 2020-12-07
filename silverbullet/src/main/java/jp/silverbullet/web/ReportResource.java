package jp.silverbullet.web;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import jp.silverbullet.core.property2.ChartProperty;
import jp.silverbullet.core.property2.IdValues;

@Path("/{app}/report")
public class ReportResource {
	abstract class FileSearch {
		public void search(String path, String ext, boolean content) {
			int count = 0;
			for (File file : new File(path).listFiles()) {
				if (file.getName().endsWith("." + ext)) {
					byte[] bytes = null;
					if (content) {
						try {
							bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
						} catch (IOException e) {
							e.printStackTrace();
						}						
					}

					data(file.getName(), bytes, count);
					count++;
				}
			}
		}
		abstract void data(String filename, byte[] data, int count);
	}
	
	@GET
	@Path("/getReport")
	@Produces(MediaType.TEXT_HTML) 
	public String getReport() {
		String path  = "C:\\Users\\miyak\\git\\silverbullet\\silverbullet\\2020-12-06\\";
		
		final StringBuilder html = new StringBuilder();
		html.append("<HTML><BODY>");

		new FileSearch() {
			@Override
			void data(String filename, byte[] bytes, int count) {
				try {
					html.append("<div>**** " + filename + " *****</div>");
					String json = new String(bytes);
					IdValues v = new ObjectMapper().readValue(json, IdValues.class);
					for (IdValue idValue :  v.idValue) {
						html.append("<div>" + idValue.getId() + ":" + idValue.getValue() + "</div>");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.search(path, "json", true);
		
		html.append("<table><rt>");
		new FileSearch() {
			@Override
			void data(String filename, byte[] bytes, int count) {
				html.append("<td>");
				html.append("<image src=\"" + "rest/app/report/image/" + filename + "\" width=\"300px\" height=\"200px\">");// + "\"" +  "" + "\"" + ">";
				html.append("</td>");
				count++;
				if (count%2 == 0) {
					html.append("</tr><tr>");
				}
			}
		}.search(path, "png", false);
		html.append("</tr></table>");
		
		html.append("<table><rt>");
		new FileSearch() {
			@Override
			void data(String filename, byte[] bytes, int count) {
				html.append("<td>");
				html.append("<image src=\"" + "rest/app/report/chart/" + filename + "\" width=\"300px\" height=\"200px\">");// + "\"" +  "" + "\"" + ">";
				html.append("</td>");
				count++;
				if (count%2 == 0) {
					html.append("</tr><tr>");
				}
			}
		}.search(path, "otdr_ID_TRACE", false);
		html.append("</tr></table>");	
		
		html.append("</BODY></HTML>");
		return html.toString();
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
	@GET
	@Path("/chart/{filename}")
	@Produces("image/png") 
	public Response chart(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @PathParam("filename") String filename) {
		String path  = "C:\\Users\\miyak\\git\\silverbullet\\silverbullet\\2020-12-06\\";
		path += filename;  
		
		try {
			byte[] bytes = Files.readAllBytes(Paths.get(path));
			ChartProperty chart = new ObjectMapper().readValue(bytes, ChartProperty.class);
			ByteArrayOutputStream baos = new ChartImage().get(chart);
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(baos.toByteArray())); 
			return Response.ok(image).build();	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

package jp.silverbullet.web;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.imageio.ImageIO;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.core.dependency2.IdValue;
import jp.silverbullet.core.property2.ChartProperty;
import jp.silverbullet.core.property2.IdValues;
import jp.silverbullet.core.property2.RuntimeProperty;
import jp.silverbullet.core.property2.RuntimePropertyStore;

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
	@Path("/getList")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getList(@CookieParam("SilverBullet") String cookie) {
		return SilverBulletServer.getStaticInstance().getStorePaths(cookie);
	}
	
	@GET
	@Path("/getReport")
	@Produces(MediaType.TEXT_HTML) 
	public String getReport(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, 
			@QueryParam("target") String target) {
		String path  = SilverBulletServer.getStaticInstance().getStorePath(cookie) + "/" + target;
//		String path  = "C:\\Users\\miyak\\git\\silverbullet\\silverbullet\\2020-12-06\\";
		
		RuntimePropertyStore store = SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getRuntimePropertyStore();
				
		final StringBuilder html = new StringBuilder();
		html.append("<HTML><BODY>");
		html.append("<table><tr>");
		new FileSearch() {
			@Override
			void data(String filename, byte[] bytes, int count) {
				try {
					html.append("<td>");
					
					html.append("<table><tr>");
					html.append("<div>" + filename + "</div>");
					
					String json = new String(bytes);
					IdValues v = new ObjectMapper().readValue(json, IdValues.class);
					for (IdValue idValue :  v.idValue) {
						RuntimeProperty prop = store.get(idValue.getId().getId());
						
						html.append("<tr><td>" + prop.getTitle() + "</td><td>" + createPresentation(prop, idValue.getValue()) + "</tr>");
					}
					html.append("</tr></table>");
					html.append("<image src=\"" + "rest/silverbullet/report/chart/" + target + "/" + findChart(path, filename) + "\" width=\"300px\" height=\"200px\">");
					html.append("</td>");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (count%2 == 0) {
					html.append("</tr><tr>");
				}
			}

			private String createPresentation(RuntimeProperty prop, String value) {
				if (prop.isList()) {
					return prop.getOptionTitle(value);
				}
				else if (prop.isNumericProperty()) {
					return String.format("." + prop.getDecimals() + "f", Double.valueOf(value));
				}
				else {
					return value + prop.getUnit();
				}
				
			}
		}.search(path, "json", true);
		html.append("</tr></table>");
		
		html.append("<table><rt>");
		new FileSearch() {
			@Override
			void data(String filename, byte[] bytes, int count) {
				html.append("<td>");
				html.append("<div>" + filename + "</div>");
				html.append("<image src=\"" + "rest/silverbullet/report/image/" + target + "/" + filename + "\" width=\"300px\" height=\"200px\">");// + "\"" +  "" + "\"" + ">";
				html.append("</td>");
				count++;
				if (count%2 == 0) {
					html.append("</tr><tr>");
				}
			}
		}.search(path, "png", false);
		html.append("</tr></table>");
		
//		html.append("<table><rt>");
//		new FileSearch() {
//			@Override
//			void data(String filename, byte[] bytes, int count) {
//				html.append("<td>");
//				html.append("<div>" + filename + "</div>");
//				html.append("<image src=\"" + "rest/silverbullet/report/chart/" + filename + "\" width=\"300px\" height=\"200px\">");// + "\"" +  "" + "\"" + ">";
//				html.append("</td>");
//				count++;
//				if (count%2 == 0) {
//					html.append("</tr><tr>");
//				}
//			}
//		}.search(path, "otdr_ID_TRACE", false);
//		html.append("</tr></table>");	
		
		html.append("<iframe src=hhttps://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2162.9965192692625!2d139.7652839627025!3d35.68111511958082!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x60188bfbd89f700b%3A0x277c49ba34ed38!2z5p2x5Lqs6aeF!5e0!3m2!1sja!2sjp!4v1529927231570 width=h600 height=h450 frameborder=h0 style=hborder:0 allowfullscreen></iframe>");
		html.append("</BODY></HTML>");
		return html.toString();
	}
	
	protected String findChart(String path, String filename) {
		for (String f : new File(path).list()) {
			if (f.startsWith(filename.replace(".json", ".chart"))) {
				return f;
			}
		}
		return null;
	}

	@GET
	@Path("/image/{folder}/{filename}")
	@Produces("image/png") 
	public Response image(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, 
			@PathParam("folder") String folder, @PathParam("filename") String filename) {
		String path  = SilverBulletServer.getStaticInstance().getStorePath(cookie) + "/" + folder + "/" + filename;
		//String path  = "C:\\Users\\miyak\\git\\silverbullet\\silverbullet\\2020-12-06\\";
//		path += filename;  
		
		try {
			BufferedImage image = ImageIO.read(new File(path));
			return Response.ok(image).build();	
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	@GET
	@Path("/chart/{folder}/{filename}")
	@Produces("image/png") 
	public Response chart(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, 
			@PathParam("folder") String folder, @PathParam("filename") String filename) {
		String path  = SilverBulletServer.getStaticInstance().getStorePath(cookie) + "/" + folder + "/" + filename;
	//	path += "/" + filename;  
		
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

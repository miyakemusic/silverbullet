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
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import jp.silverbullet.core.dependency2.Id;
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
	@Path("/parameter/{folder}")
	@Produces(MediaType.TEXT_HTML) 
	public String getReport(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, 
			@PathParam("folder") String folder, @QueryParam("filename") String filename, @QueryParam("id") String id) throws IOException {
		
		RuntimePropertyStore store = SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getRuntimePropertyStore();
		String path = SilverBulletServer.getStaticInstance().getStorePath(cookie) + "/" + folder + "/" + filename;
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		String json = new String(bytes);
		IdValues v = new ObjectMapper().readValue(json, IdValues.class);
		StringBuilder html = new StringBuilder();
		html.append("<html><body><table>");
		for (String id2 : id.split(",")) {
			for (IdValue idValue :  v.idValue) {
				if (id2.equals(new Id(idValue.getId().getId()).getId())){
					html.append("<tr>");
					RuntimeProperty prop = store.get(idValue.getId().getId());
//					return prop.getTitle() + ":" + createPresentation(prop, idValue.getValue());
					html.append("<td>" + prop.getTitle() + "</td>");
					html.append("<td>" + ":" + "</td>");
					html.append("<td>" + createPresentation(prop, idValue.getValue()) + "</td>");
					html.append("</tr>");
				}
			}
		}
		html.append("</table></body></html>");
		return html.toString();
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
	@GET
	@Path("/getReport/{folder}")
	@Produces(MediaType.TEXT_HTML) 
	public String getReport(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, 
			@PathParam("folder") String folder) {
		String path  = SilverBulletServer.getStaticInstance().getStorePath(cookie) + "/" + folder;
//		String path  = "C:\\Users\\miyak\\git\\silverbullet\\silverbullet\\2020-12-06\\";
		
		RuntimePropertyStore store = SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getRuntimePropertyStore();
				
		final StringBuilder html = new StringBuilder();
		html.append("<HTML><BODY>");
		html.append("<table><tr>");
		new FileSearch() {
			@Override
			void data(String filename, byte[] bytes, int count) {
				try {
					html.append("<td style=\"width:400px;\">");
					
					html.append("<table><tr>");
					html.append("<div>" + filename + "</div>");
					
					String json = new String(bytes);
					IdValues v = new ObjectMapper().readValue(json, IdValues.class);
					for (IdValue idValue :  v.idValue) {
						RuntimeProperty prop = store.get(idValue.getId().getId());
						
						html.append("<tr><td style=\"width:100px;\">" + prop.getTitle() + "</td><td style=\"width:200px;\">" + createPresentation(prop, idValue.getValue()) + "</tr>");
					}
					html.append("</tr></table>");
					if (filename.contains(".otdr.")) {
						html.append("<image src=\"" + "rest/silverbullet/report/chart/" + folder + "/" + findChart(path, filename) + "\" width=\"300px\" height=\"200px\">");
					}
					else if (filename.contains(".vip")) {
						html.append("<image src=\"" + "rest/silverbullet/report/image/" + folder + "/" + findImage(path, filename) + "\" width=\"300px\" height=\"200px\">");						
					}
//					html.append("<iframe src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3238.320912359825!2d139.70341441472212!3d35.74291633017989!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x601892a496b90f4f%3A0x68652521cbf1f5ec!2z44CSMTczLTAwMjUg5p2x5Lqs6YO95p2_5qmL5Yy654aK6YeO55S6!5e0!3m2!1sja!2sjp!4v1607765425112!5m2!1sja!2sjp\" width=\"400\" height=\"300\" frameborder=\"0\" style=\"border:0;\" allowfullscreen=\"\" aria-hidden=\"false\" tabindex=\"0\"></iframe>");

					html.append("</td>");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (count%2 == 1) {
					html.append("</tr><tr>");
				}
			}


		}.search(path, "json", true);
		
		html.append("<table><tr><td style=\"width:400px;\">");
		html.append("<iframe src=\"rest/silverbullet/report/parameter/" + folder + 
				"?filename=" + "result1.otdr.json&id=ID_PULSEWIDTH,ID_DISTANCERANGE,ID_AVERAGETIME,ID_TEST_MODE,ID_AVERAGE_RESULT,ID_OTDR_PASSFAIL" + "\"></iframe>");
		html.append("<image src=\"" + "rest/silverbullet/report/chart/" + folder + "/result1.otdr.chart.ID_TRACE" + "\" width=\"400px\" height=\"300px\">");
		
		html.append("</td><td style=\"width:400px;\">");
		html.append("<iframe src=\"rest/silverbullet/report/parameter/" + folder + 
				"?filename=" + "result2.otdr.json&id=ID_PULSEWIDTH,ID_DISTANCERANGE,ID_AVERAGETIME,ID_TEST_MODE,ID_AVERAGE_RESULT,ID_OTDR_PASSFAIL" + "\"></iframe>");
		html.append("<image src=\"" + "rest/silverbullet/report/chart/"+ folder + "/result2.otdr.chart.ID_TRACE" + "\" width=\"400px\" height=\"300px\">");
		
		html.append("</td></tr></table>");

		html.append("</BODY></HTML>");
		return html.toString();
	}
	
	protected String findImage(String path, String filename) {
		String prefix = filename.replace(".json", "");
		for (String f : new File(path).list()) {
			if (f.startsWith(prefix) && f.endsWith(".png")) {
				return f;
			}
		}
		return null;
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
	
	@POST
	@Path("/html")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_HTML)
	public String html(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, String html) {
		String ret  = new String(Base64.decode(html.replace("data:text/html;base64,", "")));
		return ret;
	}
}

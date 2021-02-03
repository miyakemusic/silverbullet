package jp.silverbullet.web;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import org.apache.commons.lang3.StringEscapeUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.core.property2.ChartProperty;
import jp.silverbullet.core.property2.IdValues;
import jp.silverbullet.core.property2.RuntimeProperty;
import jp.silverbullet.core.property2.RuntimePropertyStore;
import jp.silverbullet.dev.Automator;
import jp.silverbullet.testspec.NetworkConfiguration;
import jp.silverbullet.testspec.NetworkTestConfigurationHolder;
import jp.silverbullet.testspec.TsNode;
import jp.silverbullet.testspec.TsPort;
import jp.silverbullet.testspec.TsPortConfig;
import jp.silverbullet.testspec.TsPresentationNodes;
import jp.silverbullet.testspec.TsTestSpec;

@Path("/testSpec")
public class TestSpecResource {
	@GET
	@Path("/getDemo")
	@Produces(MediaType.APPLICATION_JSON) 
	public TsPresentationNodes getTest(@CookieParam("SilverBullet") String cookie) {
		return new TsPresentationNodes(new NetworkConfiguration().createDemo());
	}
	
	@GET
	@Path("/connectors")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> testType(@CookieParam("SilverBullet") String cookie) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		return SilverBulletServer.getStaticInstance().getBuilderModelHolder().getTestConfig(userid).selections.connetros;
	}

	@GET
	@Path("/portConfig")
	@Produces(MediaType.APPLICATION_JSON) 
	public TsPortConfig portConfig(@CookieParam("SilverBullet") String cookie, @QueryParam("id") String id) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		NetworkTestConfigurationHolder testConfig = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getTestConfig(userid);
		return testConfig.getPortConfig(id);
	}
	
	@GET
	@Path("/getTestSpec")
	@Produces(MediaType.APPLICATION_JSON) 
	public TsPresentationNodes getTestSpec(@CookieParam("SilverBullet") String cookie) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		NetworkTestConfigurationHolder testConfig = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getTestConfig(userid);
		return new TsPresentationNodes(testConfig.get());
	}
	
	@GET
	@Path("/createDemo")
	@Produces(MediaType.TEXT_PLAIN) 
	public Response createDemo(@CookieParam("SilverBullet") String cookie) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		NetworkTestConfigurationHolder testConfig = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getTestConfig(userid);
		testConfig.createDemo();
		return Response.ok().build();
	}
	
	@GET
	@Path("/registerScript")
	@Produces(MediaType.TEXT_PLAIN) 
	public Response registerScript(@CookieParam("SilverBullet") String cookie) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		SilverBulletServer.getStaticInstance().getBuilderModelHolder().registerScript(userid);
		
		return Response.ok().build();
	}
	
	@GET
	@Path("/createScript")
	@Produces(MediaType.APPLICATION_JSON) 
	public TsTestSpec createScript(@CookieParam("SilverBullet") String cookie, @QueryParam("id") String ids) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		NetworkTestConfigurationHolder testConfig = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getTestConfig(userid);
		TsTestSpec testSpec = testConfig.createScript(Arrays.asList(ids.split(",")));
		return testSpec;
	}
	
	@GET
	@Path("/sortBy")
	@Produces(MediaType.APPLICATION_JSON) 
	public TsTestSpec sortBy(@CookieParam("SilverBullet") String cookie, @QueryParam("sortBy") String sortBy) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		NetworkTestConfigurationHolder testConfig = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getTestConfig(userid);
		TsTestSpec testSpec = testConfig.sortBy(sortBy);
		return testSpec;
	}
	
	@GET
	@Path("/result")
	@Produces(MediaType.TEXT_PLAIN)
	public String result(@CookieParam("SilverBullet") String cookie,  @QueryParam("portId") String portId,  @QueryParam("testMethod") String testMethod,
			@QueryParam("side") String side) {
		
//		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		String ret = readJson(cookie, portId, testMethod, side);
		return ret;
	}

	private String readJson(String cookie, String portId, String testMethod, String side) {
		StringBuilder builder = new StringBuilder();
		for (File file : new File("C:\\Users\\miyak\\OneDrive\\openti\\results").listFiles()) {
			String name = file.getName();
			if (name.contains(portId) && name.contains(testMethod) && name.contains(side)) {
				try {
					byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
					String json = new String(bytes);
					IdValues v = new ObjectMapper().readValue(json, IdValues.class);
					
					String app = v.application;
					RuntimePropertyStore store = SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getRuntimePropertyStore();
					v.idValue.forEach(a -> {
						RuntimeProperty prop = store.get(a.getId().getId());
						String value = prop.getCurrentValue();
						if (prop.isList()) {
							value = prop.getSelectedListTitle();
						}
						String line = "<div>" + prop.getTitle() + " : " + value + prop.getUnit() + "</div>";
						builder.append(line);
					});
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		String ret = "<img src=\"rest/testSpec/image?portId=" + portId + "&testMethod=" + testMethod + "&side=" + side + "\" width=\"300px\" height=\"200px\">";
		
		System.out.println(ret);
		
		builder.append(ret);
		return builder.toString();
	}
	
	@GET
	@Path("/image")
	@Produces("image/png") 
	public Response image(@CookieParam("SilverBullet") String cookie,  @QueryParam("portId") String portId,  @QueryParam("testMethod") String testMethod,
			@QueryParam("side") String side) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);

		System.out.println(testMethod);
		for (File file : new File("C:\\Users\\miyak\\OneDrive\\openti\\results").listFiles()) {
			String name = file.getName();
			if (name.contains(portId) && name.contains(testMethod) && name.contains(side)) {
				System.out.println(name);
				if (name.endsWith(".chart")) {
					try {
						byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
						ChartProperty chart = new ObjectMapper().readValue(bytes, ChartProperty.class);
						ByteArrayOutputStream baos = new ChartImage().get(chart);
						BufferedImage image = ImageIO.read(new ByteArrayInputStream(baos.toByteArray())); 
						return Response.ok(image).build();	
					} catch (IOException e) {
						e.printStackTrace();
					}		
				}
				else if (name.endsWith(".png")) {
					if (file.getName().contains(".png")) {
						try {
							BufferedImage image = ImageIO.read(file);
							return Response.ok(image).build();	
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}				
			}
		}

		return Response.ok().build();

	}	
	
	@GET
	@Path("/testMethods")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> testMethods(@CookieParam("SilverBullet") String cookie) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		List<String> ret = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getTestConfig(userid).getTestMethods();
		return ret;
	}
	
	@POST
	@Path("/postPortConfig")
	@Consumes(MediaType.APPLICATION_JSON) 
	public Response portConfig(@CookieParam("SilverBullet") String cookie, @QueryParam("id") String id, TsPortConfig config) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		NetworkTestConfigurationHolder testConfig = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getTestConfig(userid);
		testConfig.setPortConfig(id, config);
		return Response.ok().build();
	}
	
	@POST
	@Path("/copyConfig")
	@Consumes(MediaType.APPLICATION_JSON) 
	public Response copyConfig(@CookieParam("SilverBullet") String cookie, @QueryParam("id") String id, TsPortConfig config) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		NetworkTestConfigurationHolder testConfig = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getTestConfig(userid);
		testConfig.copyPortConfig(id, config);
		return Response.ok().build();
	}
	
	@GET
	@Path("/report")
	@Produces(MediaType.TEXT_HTML)
	public String testMethods(@CookieParam("SilverBullet") String cookie, @QueryParam("nodeId") String nodeId) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		TsNode node = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getTestConfig(userid).getNode(nodeId);
		
		StringBuilder builder = new StringBuilder();
		builder.append("<HTML><BODY>");
		
		builder.append("<div><h2>[" + node.getName() + "]</h2></div>");
		
	
		generateTable(cookie, builder, node.getInputs());
		generateTable(cookie, builder, node.getOutputs());


		builder.append("</BODY></HTML>");
		return builder.toString();
	}

	private void generateTable(String cookie, StringBuilder builder, Map<String, TsPort> outlet) {
//		builder.append("<table>");
		outlet.forEach((k,v) -> {
			builder.append(v.getName());
			
			v.config().insideTest.forEach(t -> {
				String side = "Device Side";
				builder.append("<div>" + t + "</div>");
				builder.append(readJson(cookie, v.id, t, side).replace("rest/testSpec/", ""));
				
			});
			v.config().outsideTest.forEach(t -> {
				String side = "Fiber Side";
				builder.append("<div>" + t + "</div>");
				builder.append(readJson(cookie, v.id, t, side).replace("rest/testSpec/", ""));
			});
		});
//		builder.append("</table>");
	}
}

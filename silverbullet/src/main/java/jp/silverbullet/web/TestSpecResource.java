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
import java.util.Comparator;
import java.util.LinkedHashMap;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.core.property2.ChartProperty;
import jp.silverbullet.core.property2.IdValues;
import jp.silverbullet.core.property2.RuntimeProperty;
import jp.silverbullet.core.property2.RuntimePropertyStore;
import jp.silverbullet.testspec.NetworkConfiguration;
import jp.silverbullet.testspec.NetworkTestConfigurationHolder;
import jp.silverbullet.testspec.PortStateEnum;
import jp.silverbullet.testspec.JsPortStatus;
import jp.silverbullet.testspec.TestResultManager;
import jp.silverbullet.testspec.TestResultManager.Project;
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
	@Path("/projectList")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> projectList(@CookieParam("SilverBullet") String cookie) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		NetworkTestConfigurationHolder testConfig = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getTestConfig(userid);
		return testConfig.getProjectList();
	}
	
	@GET
	@Path("/connectors")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> testType(@CookieParam("SilverBullet") String cookie) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		return SilverBulletServer.getStaticInstance().getBuilderModelHolder().getTestConfig(userid).selections.connetros;
	}

	@GET
	@Path("/{projectName}/portConfig")
	@Produces(MediaType.APPLICATION_JSON) 
	public TsPortConfig portConfig(@CookieParam("SilverBullet") String cookie, @PathParam("projectName") String projectName, @QueryParam("id") String id) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		NetworkTestConfigurationHolder testConfig = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getTestConfig(userid);
		TsPortConfig ret = testConfig.getPortConfig(projectName, id);
		return ret;
	}
	
	@GET
	@Path("/{projectName}/getTestSpec")
	@Produces(MediaType.APPLICATION_JSON) 
	public TsPresentationNodes getTestSpec(@CookieParam("SilverBullet") String cookie, @PathParam("projectName") String projectName) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		NetworkTestConfigurationHolder testConfig = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getTestConfig(userid);
		return new TsPresentationNodes(testConfig.get(projectName));
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
	@Path("/{projectName}/registerScript")
	@Produces(MediaType.TEXT_PLAIN) 
	public Response registerScript(@CookieParam("SilverBullet") String cookie, @PathParam("projectName") String projectName) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		SilverBulletServer.getStaticInstance().getBuilderModelHolder().registerScript(projectName, userid);
		
		return Response.ok().build();
	}
	
	@GET
	@Path("/{projectName}/createScript")
	@Produces(MediaType.APPLICATION_JSON) 
	public TsTestSpec createScript(@CookieParam("SilverBullet") String cookie, @PathParam("projectName") String projectName, @QueryParam("id") String ids) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		NetworkTestConfigurationHolder testConfig = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getTestConfig(userid);
		TsTestSpec testSpec = testConfig.createScript(projectName, Arrays.asList(ids.split(",")));
		return testSpec;
	}
	
	@GET
	@Path("/{projectName}/sortBy")
	@Produces(MediaType.APPLICATION_JSON) 
	public TsTestSpec sortBy(@CookieParam("SilverBullet") String cookie, @PathParam("projectName") String projectName, @QueryParam("sortBy") String sortBy) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		NetworkTestConfigurationHolder testConfig = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getTestConfig(userid);
		TsTestSpec testSpec = testConfig.sortBy(projectName, sortBy);
		return testSpec;
	}
	
	@GET
	@Path("/{projectName}/result")
	@Produces(MediaType.TEXT_PLAIN)
	public String result(@CookieParam("SilverBullet") String cookie,  @PathParam("projectName") String projectName, @QueryParam("portId") String portId,  
			@QueryParam("testMethod") String testMethod,
			@QueryParam("side") String side) {

		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		
		String path = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getStorePath(userid);
		
//		NetworkTestConfigurationHolder testConfig = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getTestConfig(userid);
		
		String ret = readJson(cookie, portId, testMethod, side, path, projectName);
		return ret;
	}

	private String readJson(String cookie, String portId, String testMethod, String side, String path, String projectName) {
		boolean imageExists = false;
		String path2 = path + "/" + projectName;
		if (!Files.exists(Paths.get(path2))) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for (File file : new File(path2).listFiles()) {
			String name = file.getName();
			if (name.contains(portId) && name.contains(testMethod) && name.contains(side)) {
				if (name.endsWith(".json")) {
				try {
					byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
					String json = new String(bytes);
					IdValues v = new ObjectMapper().readValue(json, IdValues.class);
					
					String app = v.application;
					RuntimePropertyStore store = SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getRuntimePropertyStore();
					v.idValue.forEach(a -> {
						RuntimeProperty prop = store.get(a.getId().getId());
						String value = "";//prop.getCurrentValue();
						if (prop.isList()) {
							value = prop.getOptionTitle(a.getValue());
						}
						else {
							value = a.getValue();
						}
						String line = "<div>" + prop.getTitle() + " : " + value + prop.getUnit() + "</div>";
						builder.append(line);
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
				}
				else if (name.endsWith(".png") || name.endsWith(".chart")) {
					imageExists = true;
				}
			}
		}
		if (imageExists) {
			String ret = "<img src=\"rest/testSpec/" + projectName + "/image?portId=" + portId + "&testMethod=" + testMethod + "&side=" + side
					+ "\" width=\"300px\" height=\"200px\">";
			System.out.println(ret);
			builder.append(ret);
		}

		return builder.toString();
	}
	
	@GET
	@Path("/{projectName}/image")
	@Produces("image/png") 
	public Response image(@CookieParam("SilverBullet") String cookie,  @PathParam("projectName") String projectName, 
			@QueryParam("portId") String portId,  @QueryParam("testMethod") String testMethod,
			@QueryParam("side") String side) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);

		String path = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getStorePath(userid);
		
		System.out.println(testMethod);
		for (File file : new File(path + "/" + projectName).listFiles()) {
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
	@Path("/{projectName}/postPortConfig")
	@Consumes(MediaType.APPLICATION_JSON) 
	public Response portConfig(@CookieParam("SilverBullet") String cookie, @PathParam("projectName") String projectName, @QueryParam("id") String id, TsPortConfig config) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		NetworkTestConfigurationHolder testConfig = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getTestConfig(userid);
		testConfig.setPortConfig(projectName, id, config);
		return Response.ok().build();
	}
	
	@POST
	@Path("/{projectName}/copyConfig")
	@Consumes(MediaType.APPLICATION_JSON) 
	public Response copyConfig(@CookieParam("SilverBullet") String cookie, @PathParam("projectName") String projectName, @QueryParam("id") String id, TsPortConfig config) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		NetworkTestConfigurationHolder testConfig = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getTestConfig(userid);
		testConfig.copyPortConfig(projectName, id, config);
		return Response.ok().build();
	}
	
	@GET
	@Path("/style.css")
	public String css(@CookieParam("SilverBullet") String cookie) {
		StringBuilder builder = new StringBuilder();
		builder.append(".myTable table,.myTable td,.myTable th {border-collapse: collapse;border:1px solid #333;}");
		builder.append(".portTitle {text-align: center;font-weight:bold;}");
		builder.append(".side {text-align: center;font-weight:bold;}");
		builder.append(".method {text-align: center;}");
		return builder.toString();
	}
	
	@GET
	@Path("/{projectName}/portState")
	@Produces(MediaType.APPLICATION_JSON)
	public List<JsPortStatus> portState(@CookieParam("SilverBullet") String cookie, @PathParam("projectName") String projectName) throws IOException {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
	
		String path = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getStorePath(userid);
		
		TestResultManager manager = new TestResultManager().load(path);
		
		List<JsPortStatus> ret = new ArrayList<>();
		
		Project project = manager.project(projectName);
		return project.summary();
//		Map<String, PortStateEnum> map = new LinkedHashMap<>();
//		
//		File[] files = new File(path + "/" + projectName).listFiles();
//		
//		if (files == null) {
//			return new ArrayList<PortStatus>();
//		}
//		Arrays.sort(files, Comparator.comparingLong(File::lastModified));
//		
//		for (File file : files) {
//			String name = file.getName();
//			String portId = name.split("\\.")[1];
//			if (!map.containsKey(portId)) {
//				map.put(portId, PortStateEnum.ON_GOING);
//			}
//			if (name.endsWith(".json")) {
//				List<String> lines = Files.readAllLines(Paths.get(file.getAbsolutePath()));
//				if (lines.toString().toUpperCase().contains("PASS")) {
//					if (map.get(portId).equals(PortStateEnum.ON_GOING)) {
//						map.put(portId, PortStateEnum.COMPLETE_PASS);
//					}
//				}
//				else if (lines.toString().toUpperCase().contains("FAIL")) {
//					map.put(portId, PortStateEnum.COMPLETE_FAIL);
//				}
//			}
//		}
//		
//		List<PortStatus> ret = new ArrayList<>();
//		map.forEach((k,v) ->{
//			PortStateEnum r = map.get(k);
//			ret.add(new PortStatus(k,r));
//		});
//		return ret;
		
		
	}
	
	@GET
	@Path("/{projectName}/report")
	@Produces(MediaType.TEXT_HTML)
	public String testMethods(@CookieParam("SilverBullet") String cookie, @PathParam("projectName") String projectName, 
			@QueryParam("nodeId") String nodeId) {
		String userid = SilverBulletServer.getStaticInstance().getUserID(cookie);
		String path = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getStorePath(userid);
		
		StringBuilder builder = new StringBuilder();
		builder.append("<HTML>");
		builder.append("<HEAD>");
		builder.append("<link href=\"style.css\" rel=\"stylesheet\" type=\"text/css\">");
		builder.append("</HEAD>");
		
		builder.append("<BODY>");
		
		for (String n : nodeId.split(",")) {
			TsNode node = SilverBulletServer.getStaticInstance().getBuilderModelHolder().getTestConfig(userid).getNode(projectName, n);
					
			builder.append("<div><h2>[" + node.getName() + "]</h2></div>");
			
			generateTable(cookie, builder, node.getInputs(), path, projectName);
			generateTable(cookie, builder, node.getOutputs(), path, projectName);
		}
		builder.append("</BODY></HTML>");
		return builder.toString();
	}

	private void generateTable(String cookie, StringBuilder builder, Map<String, TsPort> outlet, String path, String projectName) {
		builder.append("<table class=\"myTable\">");
		outlet.forEach((k,v) -> {
			builder.append("<tr>");
			builder.append("<td colspan=\"2\"><div class=\"portTitle\">" + "[" + v.getName() + "]</div></td>");
			builder.append("</tr>");
			builder.append("<tr><td><div class=\"side\">Device Side</div></td><td><div class=\"side\">Fiber Side</div></td></tr>");
			int max = Math.max(v.config().insideTest.size(), v.config().outsideTest.size());
			for (int i = 0; i < max; i++) {
				builder.append("<tr>");
				{
					builder.append("<td>");
					if (i < v.config().insideTest.size()) {
						String testMethod = v.config().insideTest.get(i);
						
						if (testMethod != null) {
							builder.append("<div class=\"method\">[" + testMethod + "]</div>");
							String html = readJson(cookie, v.id, testMethod, "Device Side", path, projectName).replace("rest/testSpec/", "");
							builder.append(html);
						}
					}
					builder.append("</td>");
				}
				{
					builder.append("<td>");
					if (i < v.config().outsideTest.size()) {
						String testMethod = v.config().outsideTest.get(i);
						if (testMethod != null) {
							builder.append("<div class=\"method\">[" + testMethod + "]</div>");
							String html = readJson(cookie, v.id, testMethod, "Fiber Side", path, projectName).replace("rest/testSpec/", "");
							builder.append(html);
						}
					}
					builder.append("</td>");
				}
				builder.append("</tr>");
			}
			
//			v.config().insideTest.forEach(t -> {
//				String side = "Device Side";
//				builder.append("<div>" + t + "</div>");
//				builder.append(readJson(cookie, v.id, t, side).replace("rest/testSpec/", ""));
//				
//			});
//			v.config().outsideTest.forEach(t -> {
//				String side = "Fiber Side";
//				builder.append("<div>" + t + "</div>");
//				builder.append(readJson(cookie, v.id, t, side).replace("rest/testSpec/", ""));
//			});
		});
		builder.append("</table>");
	}
}

package jp.silverbullet.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import jp.silverbullet.testspec.NetworkConfiguration;
import jp.silverbullet.testspec.TsConnectorType;
import jp.silverbullet.testspec.TsPortConfig;
import jp.silverbullet.testspec.TsPresentationNodes;

@Path("/testSpec")
public class TestSpecResource {
	@GET
	@Path("/getDemo")
	@Produces(MediaType.APPLICATION_JSON) 
	public TsPresentationNodes getTest(@CookieParam("SilverBullet") String cookie) {
		return new TsPresentationNodes(new NetworkConfiguration().createDemo());
	}
	
	static Map<String, TsPortConfig> configs = new HashMap<>();
	
	@GET
	@Path("/testType")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> testType(@CookieParam("SilverBullet") String cookie) {
		List<String> ret = new ArrayList<>();
		for (TsConnectorType t : TsConnectorType.values()) {
			ret.add(t.toString());
		}
		return ret;
	}
	
	static private NetworkConfiguration config = new NetworkConfiguration();
	
	@GET
	@Path("/portConfig")
	@Produces(MediaType.APPLICATION_JSON) 
	public TsPortConfig portConfig(@CookieParam("SilverBullet") String cookie, @QueryParam("id") String id) {
		if (!configs.containsKey(id)) {
			configs.put(id, new TsPortConfig());
		}
		return configs.get(id);
	}
	
	@GET
	@Path("/getTestSpec")
	@Produces(MediaType.APPLICATION_JSON) 
	public TsPresentationNodes getTestSpec(@CookieParam("SilverBullet") String cookie) {
		return new TsPresentationNodes(config);
	}
	
	@GET
	@Path("/createDemo")
	@Produces(MediaType.TEXT_PLAIN) 
	public Response createDemo(@CookieParam("SilverBullet") String cookie) {
		config = config.createDemo();
		return Response.ok().build();
	}
	
	@POST
	@Path("/postPortConfig")
	@Consumes(MediaType.APPLICATION_JSON) 
	public Response portConfig(@CookieParam("SilverBullet") String cookie, @QueryParam("id") String id, TsPortConfig config) {
		configs.put(id, config);
		return Response.ok().build();
	}
}

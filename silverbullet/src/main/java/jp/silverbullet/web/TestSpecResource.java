package jp.silverbullet.web;

import java.util.ArrayList;
import java.util.Arrays;
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

import jp.silverbullet.dev.Automator;
import jp.silverbullet.testspec.NetworkConfiguration;
import jp.silverbullet.testspec.NetworkTestConfigurationHolder;
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
}

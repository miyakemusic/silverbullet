package jp.silverbullet.web;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.testspec.NetworkConfiguration;

@Path("/testSpec")
public class TestSpecResource {
	@GET
	@Path("/getDemo")
	@Produces(MediaType.APPLICATION_JSON) 
	public NetworkConfiguration getTest(@CookieParam("SilverBullet") String cookie) {
		return new NetworkConfiguration().createDemo();
	}
}

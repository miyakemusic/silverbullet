package jp.silverbullet.web;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.test.TestItem;

@Path("/test")
public class TestResource {
	@GET
	@Path("/getTest")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<TestItem> saveParameters(@QueryParam("testName") final String testName) {
		return StaticInstances.getInstance().getBuilderModel().getTestRecorder().getItems();
	}
	
	@GET
	@Path("/startRecording")
	@Produces(MediaType.APPLICATION_JSON) 
	public String startRecording() {
		StaticInstances.getInstance().getBuilderModel().getTestRecorder().startRecording();
		return "OK";
	}
	
	@GET
	@Path("/stopRecording")
	@Produces(MediaType.APPLICATION_JSON) 
	public String stopRecording() {
		StaticInstances.getInstance().getBuilderModel().getTestRecorder().stopRecording();
		return "OK";
	}
	
	@GET
	@Path("/playBack")
	@Produces(MediaType.APPLICATION_JSON) 
	public String playBack() {
		StaticInstances.getInstance().getBuilderModel().getTestRecorder().playBack();
		return "OK";
	}
}

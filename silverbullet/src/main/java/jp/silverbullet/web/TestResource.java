package jp.silverbullet.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.test.TestScriptPresentation;
import jp.silverbullet.web.ui.JsWidget;

@Path("/test")
public class TestResource {
	@GET
	@Path("/getTest")
	@Produces(MediaType.APPLICATION_JSON) 
	public TestScriptPresentation getTest(@QueryParam("testName") final String testName) {
		return new TestScriptPresentation(StaticInstances.getInstance().getBuilderModel().getTestRecorder().getResult());
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
	
	@GET
	@Path("/deleteRow")
	@Produces(MediaType.APPLICATION_JSON) 
	public String deleteRow(@QueryParam("serial") final long serial) {
		StaticInstances.getInstance().getBuilderModel().getTestRecorder().remove(serial);
		return "OK";
	}
	
	@GET
	@Path("/updateValue")
	@Produces(MediaType.APPLICATION_JSON) 
	public String updateValue(@QueryParam("serial") final long serial, @QueryParam("value") final String value) {
		StaticInstances.getInstance().getBuilderModel().getTestRecorder().updateValue(serial, value);
		return "OK";
	}
	
	@GET
	@Path("/updateExpected")
	@Produces(MediaType.APPLICATION_JSON) 
	public String updateExpected(@QueryParam("serial") final long serial, @QueryParam("value") final String value) {
		StaticInstances.getInstance().getBuilderModel().getTestRecorder().updateExpected(serial, value);
		return "OK";
	}
	
	@GET
	@Path("/addPropertyTest")
	@Produces(MediaType.APPLICATION_JSON) 
	public String addPropertyTest(@QueryParam("div") final String div) {
		JsWidget widget = StaticInstances.getInstance().getBuilderModel().getUiLayout().getWidget(div);
		StaticInstances.getInstance().getBuilderModel().getTestRecorder().addPropertyTest(widget.getId());
		return "OK";
	}

	@GET
	@Path("/addCommand")
	@Produces(MediaType.APPLICATION_JSON) 
	public String addCommand(@QueryParam("type") final String type, @QueryParam("target") final String target, @QueryParam("value") final String value, @QueryParam("serial") final long serial) {
		StaticInstances.getInstance().getBuilderModel().getTestRecorder().addCommand(type, target, value, serial);
		return "OK";
	}
	
	@GET
	@Path("/move")
	@Produces(MediaType.APPLICATION_JSON) 
	public String move(@QueryParam("type") final String type, @QueryParam("serial") final long serial) {
		if (type.equals("up")) {
			StaticInstances.getInstance().getBuilderModel().getTestRecorder().moveUp(serial);
		}
		else if (type.equals("down")) {
			StaticInstances.getInstance().getBuilderModel().getTestRecorder().moveDown(serial);
		}
		return "OK";
	}
	
	@GET
	@Path("/save")
	@Produces(MediaType.APPLICATION_JSON) 
	public String save() {
		StaticInstances.getInstance().getBuilderModel().getTestRecorder().save();
		return "OK";
	}
}

package jp.silverbullet.web;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.dev.StaticInstances;
import jp.silverbullet.dev.test.TestScriptPresentation;
import jp.silverbullet.web.ui.JsWidget;

@Path("/test")
public class TestResource {
	@GET
	@Path("/getTest")
	@Produces(MediaType.APPLICATION_JSON) 
	public TestScriptPresentation getTest(@QueryParam("testName") final String testName) {
		return new TestScriptPresentation(SilverBulletServer.getStaticInstance().getBuilderModel().getTestRecorder().getResult());
	}
	
	@GET
	@Path("/startRecording")
	@Produces(MediaType.APPLICATION_JSON) 
	public String startRecording() {
		SilverBulletServer.getStaticInstance().getBuilderModel().getTestRecorder().startRecording();
		return "OK";
	}
	
	@GET
	@Path("/stopRecording")
	@Produces(MediaType.APPLICATION_JSON) 
	public String stopRecording() {
		SilverBulletServer.getStaticInstance().getBuilderModel().getTestRecorder().stopRecording();
		return "OK";
	}
	
	@GET
	@Path("/playBack")
	@Produces(MediaType.APPLICATION_JSON) 
	public String playBack() {
		SilverBulletServer.getStaticInstance().getBuilderModel().getTestRecorder().playBack();
		return "OK";
	}
	
	@GET
	@Path("/deleteRow")
	@Produces(MediaType.APPLICATION_JSON) 
	public String deleteRow(@QueryParam("serial") final long serial) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getTestRecorder().remove(serial);
		return "OK";
	}
	
	@GET
	@Path("/updateValue")
	@Produces(MediaType.APPLICATION_JSON) 
	public String updateValue(@QueryParam("serial") final long serial, @QueryParam("value") final String value) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getTestRecorder().updateValue(serial, value);
		return "OK";
	}
	
	@GET
	@Path("/updateExpected")
	@Produces(MediaType.APPLICATION_JSON) 
	public String updateExpected(@QueryParam("serial") final long serial, @QueryParam("value") final String value) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getTestRecorder().updateExpected(serial, value);
		return "OK";
	}
	
//	@GET
//	@Path("/addPropertyTest")
//	@Produces(MediaType.APPLICATION_JSON) 
//	public String addPropertyTest(@QueryParam("div") final String div) {
//		JsWidget widget = SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().getWidget(div);
//		SilverBulletServer.getStaticInstance().getBuilderModel().getTestRecorder().addPropertyTest(widget.getId());
//		return "OK";
//	}
//
//	@GET
//	@Path("/addPropertyCommand")
//	@Produces(MediaType.APPLICATION_JSON) 
//	public String addCommand(@QueryParam("div") final String div) {
//		JsWidget widget = SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().getWidget(div);
//		SilverBulletServer.getStaticInstance().getBuilderModel().getTestRecorder().addPropertyCommand(widget.getId());
//		return "OK";
//	}
	
	@GET
	@Path("/addCommand")
	@Produces(MediaType.APPLICATION_JSON) 
	public String addCommand(@QueryParam("type") final String type, @QueryParam("target") final String target, @QueryParam("value") final String value, @QueryParam("serial") final String serial) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getTestRecorder().addCommand(type, target, value, serial);
		return "OK";
	}
	
	@GET
	@Path("/moveUp")
	@Produces(MediaType.APPLICATION_JSON) 
	public String moveUp(@QueryParam("serial") final long serial) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getTestRecorder().moveUp(serial);
		return "OK";
	}
	
	@GET
	@Path("/moveDown")
	@Produces(MediaType.APPLICATION_JSON) 
	public String moveDown(@QueryParam("serial") final long serial) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getTestRecorder().moveDown(serial);
		return "OK";
	}
	
	@GET
	@Path("/save")
	@Produces(MediaType.APPLICATION_JSON) 
	public String save(@QueryParam("testName") final String testName) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getTestRecorder().save(testName);
		return "OK";
	}
	
	@GET
	@Path("/getTestList")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getTestList() {
		return SilverBulletServer.getStaticInstance().getBuilderModel().getTestRecorder().getTestList();
	}
	
	@GET
	@Path("/selectTest")
	@Produces(MediaType.APPLICATION_JSON) 
	public String selectTest(@QueryParam("testName") final String testName) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getTestRecorder().loadTest(testName);
		return "OK";
	}
	
	@GET
	@Path("/selectRow")
	@Produces(MediaType.APPLICATION_JSON) 
	public String selectRow(@QueryParam("serial") final long serial) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getTestRecorder().selectRow(serial);
		return "OK";
	}
}

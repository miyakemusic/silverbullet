package jp.silverbullet.web;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.dev.test.TestScriptPresentation;

@Path("/{app}/test")
public class TestResource {
	@GET
	@Path("/getTest")
	@Produces(MediaType.APPLICATION_JSON) 
	public TestScriptPresentation getTest(@PathParam("app") String app, @QueryParam("testName") final String testName) {
		return new TestScriptPresentation(SilverBulletServer.getStaticInstance().getBuilderModel(app).getTestRecorder().getResult());
	}
	
	@GET
	@Path("/startRecording")
	@Produces(MediaType.APPLICATION_JSON) 
	public String startRecording(@PathParam("app") String app) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getTestRecorder().startRecording();
		return "OK";
	}
	
	@GET
	@Path("/stopRecording")
	@Produces(MediaType.APPLICATION_JSON) 
	public String stopRecording(@PathParam("app") String app) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getTestRecorder().stopRecording();
		return "OK";
	}
	
	@GET
	@Path("/playBack")
	@Produces(MediaType.APPLICATION_JSON) 
	public String playBack(@PathParam("app") String app) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getTestRecorder().playBack();
		return "OK";
	}
	
	@GET
	@Path("/deleteRow")
	@Produces(MediaType.APPLICATION_JSON) 
	public String deleteRow(@PathParam("app") String app, @QueryParam("serial") final long serial) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getTestRecorder().remove(serial);
		return "OK";
	}
	
	@GET
	@Path("/updateValue")
	@Produces(MediaType.APPLICATION_JSON) 
	public String updateValue(@PathParam("app") String app, @QueryParam("serial") final long serial, @QueryParam("value") final String value) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getTestRecorder().updateValue(serial, value);
		return "OK";
	}
	
	@GET
	@Path("/updateExpected")
	@Produces(MediaType.APPLICATION_JSON) 
	public String updateExpected(@PathParam("app") String app, @QueryParam("serial") final long serial, @QueryParam("value") final String value) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getTestRecorder().updateExpected(serial, value);
		return "OK";
	}
	
//	@GET
//	@Path("/addPropertyTest")
//	@Produces(MediaType.APPLICATION_JSON) 
//	public String addPropertyTest(@QueryParam("div") final String div) {
//		JsWidget widget = SilverBulletServer.getStaticInstance().getBuilderModel(app).getUiLayoutHolder().getCurrentUi().getWidget(div);
//		SilverBulletServer.getStaticInstance().getBuilderModel(app).getTestRecorder().addPropertyTest(widget.getId());
//		return "OK";
//	}
//
//	@GET
//	@Path("/addPropertyCommand")
//	@Produces(MediaType.APPLICATION_JSON) 
//	public String addCommand(@QueryParam("div") final String div) {
//		JsWidget widget = SilverBulletServer.getStaticInstance().getBuilderModel(app).getUiLayoutHolder().getCurrentUi().getWidget(div);
//		SilverBulletServer.getStaticInstance().getBuilderModel(app).getTestRecorder().addPropertyCommand(widget.getId());
//		return "OK";
//	}
	
	@GET
	@Path("/addCommand")
	@Produces(MediaType.APPLICATION_JSON) 
	public String addCommand(@PathParam("app") String app, @QueryParam("type") final String type, @QueryParam("target") final String target, @QueryParam("value") final String value, @QueryParam("serial") final String serial) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getTestRecorder().addCommand(type, target, value, serial);
		return "OK";
	}
	
	@GET
	@Path("/moveUp")
	@Produces(MediaType.APPLICATION_JSON) 
	public String moveUp(@PathParam("app") String app, @QueryParam("serial") final long serial) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getTestRecorder().moveUp(serial);
		return "OK";
	}
	
	@GET
	@Path("/moveDown")
	@Produces(MediaType.APPLICATION_JSON) 
	public String moveDown(@PathParam("app") String app, @QueryParam("serial") final long serial) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getTestRecorder().moveDown(serial);
		return "OK";
	}
	
	@GET
	@Path("/save")
	@Produces(MediaType.APPLICATION_JSON) 
	public String save(@PathParam("app") String app, @QueryParam("testName") final String testName) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getTestRecorder().save(testName);
		return "OK";
	}
	
	@GET
	@Path("/getTestList")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getTestList(@PathParam("app") String app) {
		return SilverBulletServer.getStaticInstance().getBuilderModel(app).getTestRecorder().getTestList();
	}
	
	@GET
	@Path("/selectTest")
	@Produces(MediaType.APPLICATION_JSON) 
	public String selectTest(@PathParam("app") String app, @QueryParam("testName") final String testName) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getTestRecorder().loadTest(testName);
		return "OK";
	}
	
	@GET
	@Path("/selectRow")
	@Produces(MediaType.APPLICATION_JSON) 
	public String selectRow(@PathParam("app") String app, @QueryParam("serial") final long serial) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getTestRecorder().selectRow(serial);
		return "OK";
	}
}

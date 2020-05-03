package jp.silverbullet.web;

import java.util.List;

import javax.ws.rs.CookieParam;
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
	public TestScriptPresentation getTest(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @QueryParam("testName") final String testName) {
		return new TestScriptPresentation(SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getTestRecorder().getResult());
	}
	
	@GET
	@Path("/startRecording")
	@Produces(MediaType.APPLICATION_JSON) 
	public String startRecording(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app) {
		SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getTestRecorder().startRecording();
		return "OK";
	}
	
	@GET
	@Path("/stopRecording")
	@Produces(MediaType.APPLICATION_JSON) 
	public String stopRecording(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app) {
		SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getTestRecorder().stopRecording();
		return "OK";
	}
	
	@GET
	@Path("/playBack")
	@Produces(MediaType.APPLICATION_JSON) 
	public String playBack(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app) {
		SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getTestRecorder().playBack();
		return "OK";
	}
	
	@GET
	@Path("/deleteRow")
	@Produces(MediaType.APPLICATION_JSON) 
	public String deleteRow(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @QueryParam("serial") final long serial) {
		SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getTestRecorder().remove(serial);
		return "OK";
	}
	
	@GET
	@Path("/updateValue")
	@Produces(MediaType.APPLICATION_JSON) 
	public String updateValue(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @QueryParam("serial") final long serial, @QueryParam("value") final String value) {
		SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getTestRecorder().updateValue(serial, value);
		return "OK";
	}
	
	@GET
	@Path("/updateExpected")
	@Produces(MediaType.APPLICATION_JSON) 
	public String updateExpected(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @QueryParam("serial") final long serial, @QueryParam("value") final String value) {
		SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getTestRecorder().updateExpected(serial, value);
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
	public String addCommand(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @QueryParam("type") final String type, @QueryParam("target") final String target, @QueryParam("value") final String value, @QueryParam("serial") final String serial) {
		SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getTestRecorder().addCommand(type, target, value, serial);
		return "OK";
	}
	
	@GET
	@Path("/moveUp")
	@Produces(MediaType.APPLICATION_JSON) 
	public String moveUp(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @QueryParam("serial") final long serial) {
		SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getTestRecorder().moveUp(serial);
		return "OK";
	}
	
	@GET
	@Path("/moveDown")
	@Produces(MediaType.APPLICATION_JSON) 
	public String moveDown(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @QueryParam("serial") final long serial) {
		SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getTestRecorder().moveDown(serial);
		return "OK";
	}
	
	@GET
	@Path("/save")
	@Produces(MediaType.APPLICATION_JSON) 
	public String save(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @QueryParam("testName") final String testName) {
		SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getTestRecorder().save(testName);
		return "OK";
	}
	
	@GET
	@Path("/getTestList")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getTestList(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app) {
		return SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getTestRecorder().getTestList();
	}
	
	@GET
	@Path("/selectTest")
	@Produces(MediaType.APPLICATION_JSON) 
	public String selectTest(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @QueryParam("testName") final String testName) {
		SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getTestRecorder().loadTest(testName);
		return "OK";
	}
	
	@GET
	@Path("/selectRow")
	@Produces(MediaType.APPLICATION_JSON) 
	public String selectRow(@CookieParam("SilverBullet") String cookie, @PathParam("app") String app, @QueryParam("serial") final long serial) {
		SilverBulletServer.getStaticInstance().getBuilderModel(cookie, app).getTestRecorder().selectRow(serial);
		return "OK";
	}
}

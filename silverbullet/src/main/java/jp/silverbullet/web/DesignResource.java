package jp.silverbullet.web;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.SilverBulletServer;
import jp.silverbullet.StaticInstances;
import jp.silverbullet.web.ui.JsWidget;
import jp.silverbullet.web.ui.UiLayout;

@Path("/design")
public class DesignResource {

	@GET
	@Path("/getDesign")
	@Produces(MediaType.APPLICATION_JSON) 
	public JsWidget getDesign(@QueryParam("root") String root) {
		return SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().getDesign(root);
	}
	
	@GET
	@Path("/getSubDesign")
	@Produces(MediaType.APPLICATION_JSON) 
	public JsWidget getSubDesign(@QueryParam("div") String div) {
		return SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().getWidget(div);
	}
	
	@GET
	@Path("/addWidget")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addWidget(@QueryParam("id") List<String> ids, @QueryParam("div") String div) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().addWidget(div, ids);
		return "OK";
	}

	@GET
	@Path("/addDialog")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addDialog(@QueryParam("div") String div) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().addDialog(div);
		return "OK";
	}
	
	@GET
	@Path("/remove")
	@Produces(MediaType.TEXT_PLAIN) 
	public String remove(@QueryParam("div") String div) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().remove(div);
		return "OK";
	}
	
	@GET
	@Path("/move")
	@Produces(MediaType.TEXT_PLAIN) 
	public String move(@QueryParam("div") String div, @QueryParam("x") String x, @QueryParam("y") String y) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().move(div, x, y);
		return "OK";
	}

	@GET
	@Path("/addPanel")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addPanel(@QueryParam("div") String div) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().addPanel(div);
		return "OK";
	}

	@GET
	@Path("/addTab")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addTab(@QueryParam("div") String div) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().addTab(div);
		return "OK";
	}

	@GET
	@Path("/addRegisterShortcut")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addRegisterShortcut(@QueryParam("div") String div, @QueryParam("register") String register) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().addRegisterShortcut(div, register);
		return "OK";
	}
	
	
	@GET
	@Path("/clearLayout")
	@Produces(MediaType.TEXT_PLAIN) 
	public String clearLayout() {
		SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().clear();
		return "OK";
	}
	
	@GET
	@Path("/setLayout")
	@Produces(MediaType.TEXT_PLAIN) 
	public String move(@QueryParam("div") String div, @QueryParam("layout") String layout) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().updateProperty(div, "layout", layout);
		return "OK";
	}

	@GET
	@Path("/resize")
	@Produces(MediaType.TEXT_PLAIN) 
	public String resize(@QueryParam("div") String div, @QueryParam("width") String width, @QueryParam("height") String height) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().resize(div, width, height);
		return "OK";
	}
	
	@GET
	@Path("/updateGuiProperty")
	@Produces(MediaType.TEXT_PLAIN) 
	public String updateGuiProperty(@QueryParam("div") String div, @QueryParam("propertyType") String propertyType, @QueryParam("value") String value) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().updateProperty(div, propertyType, value);
		return "OK";
	}

	@GET
	@Path("/updateGuiBooleanProperty")
	@Produces(MediaType.TEXT_PLAIN) 
	public String updateGuiBooleanProperty(@QueryParam("div") String div, @QueryParam("propertyType") String propertyType, @QueryParam("value") Boolean value) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().updateBooleanProperty(div, propertyType, value);
		return "OK";
	}
	
	@GET
	@Path("/layoutTypes")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getLayoutTypes() {
		return Arrays.asList(JsWidget.ABSOLUTELAYOUT, JsWidget.FLOWLAYOUT, JsWidget.VERTICALLAYOUT);
	}
	
	@GET
	@Path("getWidgetTypes")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getWidgetTypes() {
		return JsWidget.getAllWidgetTypes();	
	}

	@GET
	@Path("cutPaste")
	@Produces(MediaType.TEXT_PLAIN) 
	public String cutPaste(@QueryParam("newBaseDiv") String newBaseDiv, @QueryParam("itemDiv") String itemDiv) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().cutPaste(newBaseDiv, itemDiv);
		return "OK";
	}
	
	@GET
	@Path("copyPaste")
	@Produces(MediaType.TEXT_PLAIN) 
	public String copyPaste(@QueryParam("newBaseDiv") String newBaseDiv, @QueryParam("itemDiv") String itemDiv) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().copyPaste(newBaseDiv, itemDiv);
		return "OK";
	}
	
	@GET
	@Path("getStyleClasses")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getStyleClasses(@QueryParam("type") String type) {
		return SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().getStyleClasses(type);
	}
	
	@GET
	@Path("getCustromDefinition")
	@Produces(MediaType.APPLICATION_JSON) 
	public Map<String, List<Pair>> getCustromDefinition() {
		return SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().getCustomDefinitions();
	}	
	
	@GET
	@Path("setCustomElement")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setCustomElement(@QueryParam("div") String div, @QueryParam("customId") String customId, @QueryParam("customValue") String customValue) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().setCustomElement(div, customId, customValue);
		return "OK";
	}
	
	@GET
	@Path("getFiles")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getFiles() {
		return SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().getFileList();
	}
	
	@GET
	@Path("createNewFile")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> createNewFile(@QueryParam("filename") String filename) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().createNewFile(filename);
		return SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().getFileList();
	}
	
	@GET
	@Path("removeFile")
	@Produces(MediaType.APPLICATION_JSON) 
	public String removeFile(@QueryParam("filename") String filename) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().removeFile(filename);
		return "OK";
	}
	
	@GET
	@Path("switchFile")
	@Produces(MediaType.APPLICATION_JSON) 
	public UiLayout switchFile(@QueryParam("filename") String filename) {
		return SilverBulletServer.getStaticInstance().getBuilderModel().switchUiFile(filename);
	}
	
	@GET
	@Path("addArray")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addArray(@QueryParam("div") String div) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().addArray(div);
		return "OK";
	}

}

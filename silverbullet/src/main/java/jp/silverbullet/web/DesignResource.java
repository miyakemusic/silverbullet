package jp.silverbullet.web;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.dependency2.CommitListener;
import jp.silverbullet.dependency2.RequestRejectedException;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.sequncer.Sequencer;
import jp.silverbullet.web.ui.JsProperty;
import jp.silverbullet.web.ui.JsWidget;
import jp.silverbullet.web.ui.UiLayout;

@Path("/design")
public class DesignResource {

	@GET
	@Path("/getProperty")
	@Produces(MediaType.APPLICATION_JSON) 
	public JsProperty getProperty(@QueryParam("id") String id, @QueryParam("index") Integer index, @QueryParam("ext") String ext) {
		RuntimeProperty property = StaticInstances.getInstance().getBuilderModel().getProperty(RuntimeProperty.createIdText(id,index));
		return new JsProperty(property, ext);
	}

	
	@GET
	@Path("/setValue")
	@Produces(MediaType.APPLICATION_JSON) 
	public ValueSetResult setCurrentValue(@QueryParam("id") String id, @QueryParam("index") Integer index, @QueryParam("value") String value) {
		ValueSetResult ret = new ValueSetResult();
		Sequencer sequencer = null;
		
		try {
			sequencer = StaticInstances.getInstance().getBuilderModel().getSequencer();
			sequencer.requestChange(id, index, value, new CommitListener() {
				@Override
				public Reply confirm(String message) {
					return Reply.Accept;
				}
			});
			StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().doAutoDynamicPanel();

			ret.result = "Accepted";
		} catch (RequestRejectedException e) {
			ret.message = e.getMessage();
			ret.result = "Rejected";
		} finally {
			ret.debugLog = sequencer.getDebugDepLog();		
		}

		return ret;
	}
	
	@GET
	@Path("/getDesign")
	@Produces(MediaType.APPLICATION_JSON) 
	public JsWidget getDesign(@QueryParam("root") String root) {
		return StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().getDesign(root);
	}
	
	@GET
	@Path("/getSubDesign")
	@Produces(MediaType.APPLICATION_JSON) 
	public JsWidget getSubDesign(@QueryParam("div") String div) {
		return StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().getWidget(div);
	}
	
	@GET
	@Path("/addWidget")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addWidget(@QueryParam("id") List<String> ids, @QueryParam("div") String div) {
		StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().addWidget(div, ids);
		return "OK";
	}

	@GET
	@Path("/addDialog")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addDialog(@QueryParam("div") String div) {
		StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().addDialog(div);
		return "OK";
	}
	
	@GET
	@Path("/remove")
	@Produces(MediaType.TEXT_PLAIN) 
	public String remove(@QueryParam("div") String div) {
		StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().remove(div);
		return "OK";
	}
	
	@GET
	@Path("/move")
	@Produces(MediaType.TEXT_PLAIN) 
	public String move(@QueryParam("div") String div, @QueryParam("x") String x, @QueryParam("y") String y) {
		StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().move(div, x, y);
		return "OK";
	}

	@GET
	@Path("/addPanel")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addPanel(@QueryParam("div") String div) {
		StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().addPanel(div);
		return "OK";
	}

	@GET
	@Path("/addTab")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addTab(@QueryParam("div") String div) {
		StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().addTab(div);
		return "OK";
	}

	@GET
	@Path("/addRegisterShortcut")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addRegisterShortcut(@QueryParam("div") String div, @QueryParam("register") String register) {
		StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().addRegisterShortcut(div, register);
		return "OK";
	}
	
	
	@GET
	@Path("/clearLayout")
	@Produces(MediaType.TEXT_PLAIN) 
	public String clearLayout() {
		StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().clear();
		return "OK";
	}
	
	@GET
	@Path("/setLayout")
	@Produces(MediaType.TEXT_PLAIN) 
	public String move(@QueryParam("div") String div, @QueryParam("layout") String layout) {
		StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().updateProperty(div, "layout", layout);
		return "OK";
	}

	@GET
	@Path("/resize")
	@Produces(MediaType.TEXT_PLAIN) 
	public String resize(@QueryParam("div") String div, @QueryParam("width") String width, @QueryParam("height") String height) {
		StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().resize(div, width, height);
		return "OK";
	}
	
	@GET
	@Path("/updateGuiProperty")
	@Produces(MediaType.TEXT_PLAIN) 
	public String updateGuiProperty(@QueryParam("div") String div, @QueryParam("propertyType") String propertyType, @QueryParam("value") String value) {
		StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().updateProperty(div, propertyType, value);
		return "OK";
	}

	@GET
	@Path("/updateGuiBooleanProperty")
	@Produces(MediaType.TEXT_PLAIN) 
	public String updateGuiBooleanProperty(@QueryParam("div") String div, @QueryParam("propertyType") String propertyType, @QueryParam("value") Boolean value) {
		StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().updateBooleanProperty(div, propertyType, value);
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
		StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().cutPaste(newBaseDiv, itemDiv);
		return "OK";
	}
	
	@GET
	@Path("copyPaste")
	@Produces(MediaType.TEXT_PLAIN) 
	public String copyPaste(@QueryParam("newBaseDiv") String newBaseDiv, @QueryParam("itemDiv") String itemDiv) {
		StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().copyPaste(newBaseDiv, itemDiv);
		return "OK";
	}
	
	@GET
	@Path("getStyleClasses")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getStyleClasses(@QueryParam("type") String type) {
		return StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().getStyleClasses(type);
	}
	
	@GET
	@Path("getCustromDefinition")
	@Produces(MediaType.APPLICATION_JSON) 
	public Map<String, List<Pair>> getCustromDefinition() {
		return StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().getCustomDefinitions();
	}	
	
	@GET
	@Path("setCustomElement")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setCustomElement(@QueryParam("div") String div, @QueryParam("customId") String customId, @QueryParam("customValue") String customValue) {
		StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().setCustomElement(div, customId, customValue);
		return "OK";
	}
	
	@GET
	@Path("getFiles")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getFiles() {
		return StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().getFileList();
	}
	
	@GET
	@Path("createNewFile")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> createNewFile(@QueryParam("filename") String filename) {
		StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().createNewFile(filename);
		return StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().getFileList();
	}
	
	@GET
	@Path("removeFile")
	@Produces(MediaType.APPLICATION_JSON) 
	public String removeFile(@QueryParam("filename") String filename) {
		StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().removeFile(filename);
		return "OK";
	}
	
	@GET
	@Path("switchFile")
	@Produces(MediaType.APPLICATION_JSON) 
	public UiLayout switchFile(@QueryParam("filename") String filename) {
		return StaticInstances.getInstance().getBuilderModel().switchUiFile(filename);
	}
	
	@GET
	@Path("addArray")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addArray(@QueryParam("div") String div) {
		StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().addArray(div);
		return "OK";
	}

}

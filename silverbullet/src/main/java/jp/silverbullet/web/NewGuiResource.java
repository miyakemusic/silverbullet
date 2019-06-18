package jp.silverbullet.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.SilverBulletServer;
import jp.silverbullet.web.ui.part2.UiBuilder;
import jp.silverbullet.web.ui.part2.WidgetGeneratorHelper;
import jp.silverbullet.web.ui.part2.WidgetType;
import jp.silverbullet.web.ui.part2.UiBuilder.PropertyField;
import jp.silverbullet.web.ui.part2.Layout;
import jp.silverbullet.web.ui.part2.Pane;

@Path("/newGui")
public class NewGuiResource {
	@GET
	@Path("/getDesign")
	@Produces(MediaType.APPLICATION_JSON) 
	public Pane getDesign(@QueryParam("root") final String root, @QueryParam("link") final boolean link) {
		return SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().getRootPane(root, link);
	}	

	@GET
	@Path("/getDesignByName")
	@Produces(MediaType.APPLICATION_JSON) 
	public Pane getDesignByName(@QueryParam("name") final String name, @QueryParam("link") final boolean link) {
		return SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().getPaneByName(name, link);
	}
	
	@GET
	@Path("/getWidget")
	@Produces(MediaType.APPLICATION_JSON) 
	public Pane getWidget(@QueryParam("divid") final String divid) {
		return SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().getWidget(divid);
	}	
	
	@GET
	@Path("/setSize")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setSize(@QueryParam("divid") final String divid, @QueryParam("width") final String width, @QueryParam("height") final String height) {
		Pane widget = SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().getWidget(divid);
		widget.css("width", width).css("height", height);
		widget.fireLayoutChange();
		return "OK";
	}	
	
	@GET
	@Path("/setCss")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setCss(@QueryParam("divid") final String divid, @QueryParam("key") final String key, @QueryParam("value") final String value) {
		Pane widget = SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().getWidget(divid);
		widget.css(key, value);
		widget.fireCssChange(divid, key, value);
		return "OK";
	}	
	
	@GET
	@Path("/setId")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setId(@QueryParam("divid") final String divid, @QueryParam("id") final String id, @QueryParam("subId") final String subId) {
		Pane widget = SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().getWidget(divid);
		widget.setId(id, subId);
		widget.fireIdChange();
		return "OK";
	}
	
	@GET
	@Path("/setField")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setField(@QueryParam("divid") final String divid, @QueryParam("field") final String field) {
		Pane widget = SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().getWidget(divid);
		widget.field(PropertyField.valueOf(field));
		widget.fireFieldChange();
		return "OK";
	}
		
	@GET
	@Path("/getFieldTypes")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getFieldTypes() {
		return SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().getFieldTypes();
	}
	
	@GET
	@Path("/setLayout")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setLayout(@QueryParam("divid") final String divid, @QueryParam("layout") final String layout) {
		Pane widget = SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().getWidget(divid);
		widget.layout(Layout.valueOf(layout));
		widget.fireLayoutChange();
		return "OK";
	}
	
	@GET
	@Path("/getCssKeys")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getCssKeys() {
		return Arrays.asList("font", "font-weight", "font-size", "border-style", "border-width", "border-color", "border-radius", "color", 
				"background-color", "background-image", "background-size", "padding", "margin", "top", "left", "width", "height", 
				"vertical-align", "text-align", "position");
	}

	@GET
	@Path("/setWidgetType")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setWidgetType(@QueryParam("divid") final String divid, @QueryParam("type") final String type) {
		Pane widget = SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().getWidget(divid);
		widget.setType(type);
		
		widget.fireTypeChange();
//		System.out.println(divid + "->" + type);
		return "OK";
	}
	
	@GET
	@Path("/addWidget")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addWidget(@QueryParam("divid") final String divid) {
		Pane widget = SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().getWidget(divid);
		widget.createPane(Layout.HORIZONTAL).css("width", "100").css("height", "30");//.css("border-style", "solid");
		
		Pane parent = SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().getParentOf(divid);
		parent.fireLayoutChange();
		return "OK";
	}

	@GET
	@Path("/removeWidget")
	@Produces(MediaType.TEXT_PLAIN) 
	public String removeWidget(@QueryParam("divid") final String divid) {
		Pane widget = SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().getWidget(divid);
		Pane parent = SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().getParentOf(divid);
		parent.removeChild(widget);
		parent.fireLayoutChange();
		return "OK";
	}
	
	@GET
	@Path("/move")
	@Produces(MediaType.TEXT_PLAIN) 
	public String move(@QueryParam("divid") final String divid, @QueryParam("top") final String top, @QueryParam("left") final String left) {
		Pane widget = SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().getWidget(divid);
		Pane parent = SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().getParentOf(divid);
		
		if (parent.layout.equals(Layout.HORIZONTAL)) {
			int sum = 0;
			int index = -1;
			for (int i = 0; i < parent.widgets.size(); i++) {
				Pane w = parent.widgets.get(i);
				int width = Integer.valueOf(w.css("width"));
				
				if ((Double.valueOf(left.replace("px", "")) > sum) && (Double.valueOf(left.replace("px", "")) < (sum + width))) {
					index = i;
					break;
				}
				sum += width;
				
			}
			
			if (index >= 0) {
				parent.widgets.remove(widget);
				parent.widgets.add(index, widget);
			}
		}
		else if (parent.layout.equals(Layout.VERTICAL)) {
			
		}
		else if (parent.layout.equals(Layout.ABSOLUTE)) {
			widget.css("top", top).css("left", left);
		}		
		widget.fireLayoutChange();
		return "OK";
	}
	
	@GET
	@Path("/changeParent")
	@Produces(MediaType.TEXT_PLAIN) 
	public String move(@QueryParam("divid") final String divid, @QueryParam("parent") final String parentId) {
		Pane widget = SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().getWidget(divid);
		Pane parent = SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().getParentOf(divid);
		
		if (!parent.type.equals(WidgetType.Pane)) {
			return "OK";
		}
		
		Pane newParent = SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().getWidget(parentId);
		if (newParent.equals(parent) || !newParent.type.equals(WidgetType.Pane)) {
			return "OK";
		}
		parent.removeChild(widget);
		newParent.addChild(widget);
		parent.fireLayoutChange();
		return "OK";
	}
	
	@GET
	@Path("/buttonArray")
	@Produces(MediaType.TEXT_PLAIN) 
	public String buttonArray(@QueryParam("divid") final String divid) {
		Pane widget = SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().getWidget(divid);
		WidgetGeneratorHelper helper = new WidgetGeneratorHelper(SilverBulletServer.getStaticInstance().getBuilderModel().getPropertiesHolder2());
		helper.generateToggleButton(widget.id, widget);
		widget.fireLayoutChange();
		return "OK";
	}
	
	@GET
	@Path("/titledInput")
	@Produces(MediaType.TEXT_PLAIN) 
	public String titledInput(@QueryParam("divid") final String divid) {
		Pane widget = SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().getWidget(divid);
		WidgetGeneratorHelper helper = new WidgetGeneratorHelper(SilverBulletServer.getStaticInstance().getBuilderModel().getPropertiesHolder2());
		
		helper.generateTitledSetting(widget.id, widget);
		widget.id = "";
		widget.fireLayoutChange();
		return "OK";
	}
	
	@GET
	@Path("/widgetTypes")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getWidgetTypes () {
		List<String> ret = new ArrayList<>();
		for (WidgetType type : WidgetType.values()) {
			ret.add(type.toString());
		}
		return ret;
	}
	
	@GET
	@Path("/setOptional")
	@Produces(MediaType.APPLICATION_JSON)
	public String setOptional (@QueryParam("divid") final String divid, @QueryParam("optional") final String optional) {
		Pane widget = SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().getWidget(divid);
		widget.setOptional(optional);
		widget.fireFieldChange();
		return "OK";
	}	
	
	@GET
	@Path("/copySize")
	@Produces(MediaType.APPLICATION_JSON)
	public String copySize (@QueryParam("divid") final String divid) {
		Pane widget = SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().getWidget(divid);
	
		Pane parent = SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().getParentOf(divid);
		
		List<String> keys = Arrays.asList("width", "height", "padding", "margin", "font-size");
		for (Pane child : parent.widgets) {
			if (child.type.equals(widget.type)) {
				for (String key : keys) {
					child.css(key, widget.css(key));
				}				
			}
		}
		parent.fireFieldChange();
		return "OK";
	}
	
	@GET
	@Path("/getRootPanes")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getRootPanes() {
		return SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().getRootList();
	}
	
	@GET
	@Path("/addRootPane")
	@Produces(MediaType.TEXT_PLAIN)
	public String addRootPanes() {
		SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().createRoot();
		return "OK";
	}
	
	@GET
	@Path("/changeName")
	@Produces(MediaType.TEXT_PLAIN)
	public String changeName(@QueryParam("oldName") final String oldName, @QueryParam("newName") final String newName) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getUiBuilder().changeRootName(oldName, newName);
		return "OK";
	}

}

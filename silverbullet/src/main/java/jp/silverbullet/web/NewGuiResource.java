package jp.silverbullet.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.core.ui.part2.Layout;
import jp.silverbullet.core.ui.part2.Pane;
import jp.silverbullet.core.ui.part2.PaneWalkThrough;
import jp.silverbullet.core.ui.part2.UiBuilder;
import jp.silverbullet.core.ui.part2.WidgetGeneratorHelper;
import jp.silverbullet.core.ui.part2.WidgetType;
import jp.silverbullet.core.ui.part2.UiBuilder.PropertyField;

@Path("/{app}/newGui")
public class NewGuiResource {
	@GET
	@Path("/getDesign")
	@Produces(MediaType.APPLICATION_JSON) 
	public Pane getDesign(@PathParam("app") String app, @QueryParam("root") final String root, @QueryParam("link") final boolean link) {
		Pane pane = getUiBuilder(app).getRootPane(root, link);
//		new PaneWalkThrough() {
//
//			@Override
//			protected boolean handle(Pane widget, Pane parent) {
//				if (!widget.id.isEmpty()) {
//					System.out.println(widget.id + "." + widget.subId);
//				}
//				return true;
//			}
//			
//		}.walkThrough(pane, null);
		
		return pane;
	}

	private UiBuilder getUiBuilder(String app) {
		return SilverBulletServer.getStaticInstance().getBuilderModel(app).getUiBuilder();
	}	

	@GET
	@Path("/getDesignByName")
	@Produces(MediaType.APPLICATION_JSON) 
	public Pane getDesignByName(@PathParam("app") String app, @QueryParam("name") final String name, @QueryParam("link") final boolean link,
			@QueryParam("initPos") final boolean initPos) {
		
		Pane pane = getUiBuilder(app).getPaneByName(name, link, initPos);
		return pane;
	}
	
	@GET
	@Path("/getWidget")
	@Produces(MediaType.APPLICATION_JSON) 
	public Pane getWidget(@PathParam("app") String app, @QueryParam("divid") final String divid) {
		return getUiBuilder(app).getWidget(divid);
	}	
	
	@GET
	@Path("/setSize")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setSize(@PathParam("app") String app, @QueryParam("divid") final String divid, @QueryParam("width") final String width, @QueryParam("height") final String height) {
		Pane widget = getUiBuilder(app).getWidget(divid);
		widget.css("width", width).css("height", height);
		widget.fireLayoutChange();
		return "OK";
	}	
	
	@GET
	@Path("/setCss")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setCss(@PathParam("app") String app, @QueryParam("divid") final String divid, @QueryParam("key") final String key, @QueryParam("value") final String value) {
		Pane widget = getUiBuilder(app).getWidget(divid);
		widget.css(key, value);
		widget.fireCssChange(divid, key, value);
		return "OK";
	}	
	
	@GET
	@Path("/setId")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setId(@PathParam("app") String app, @QueryParam("divid") final String divid, @QueryParam("id") final String id, @QueryParam("subId") final String subId) {
		Pane widget = getUiBuilder(app).getWidget(divid);
		widget.setId(id, subId);
		widget.fireIdChange();
		return "OK";
	}
	
	@GET
	@Path("/setField")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setField(@PathParam("app") String app, @QueryParam("divid") final String divid, @QueryParam("field") final String field) {
		Pane widget = getUiBuilder(app).getWidget(divid);
		widget.field(PropertyField.valueOf(field));
		widget.fireFieldChange();
		return "OK";
	}
		
	@GET
	@Path("/getFieldTypes")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getFieldTypes(@PathParam("app") String app) {
		return getUiBuilder(app).getFieldTypes();
	}
	
	@GET
	@Path("/setLayout")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setLayout(@PathParam("app") String app, @QueryParam("divid") final String divid, @QueryParam("layout") final String layout) {
		Pane widget = getUiBuilder(app).getWidget(divid);
		widget.layout(Layout.valueOf(layout));
		widget.fireLayoutChange();
		return "OK";
	}
	
	@GET
	@Path("/getCssKeys")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getCssKeys(@PathParam("app") String app) {
		return Arrays.asList(
				"font", "font-family","font-weight", "font-size", "border-style", "border-width", "border-color", "border-radius", "color", 
				"background-color", "background-image", "background-size", "padding", "margin", "top", "left", "width", "height", 
				"vertical-align", "text-align", "position");
	}

	@GET
	@Path("/setWidgetType")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setWidgetType(@PathParam("app") String app, @QueryParam("divid") final String divid, @QueryParam("type") final String type) {
		Pane widget = getUiBuilder(app).getWidget(divid);
		widget.setType(type);
		
		widget.fireTypeChange();
//		System.out.println(divid + "->" + type);
		return "OK";
	}
	
	@GET
	@Path("/addWidget")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addWidget(@PathParam("app") String app, @QueryParam("divid") final String divid) {
		Pane widget = getUiBuilder(app).getWidget(divid);
		widget.createPane(Layout.HORIZONTAL).css("width", "100").css("height", "30");//.css("border-style", "solid");
		
		Pane parent = getUiBuilder(app).getParentOf(divid);
		parent.fireLayoutChange();
		return "OK";
	}

	@GET
	@Path("/removeWidget")
	@Produces(MediaType.TEXT_PLAIN) 
	public String removeWidget(@PathParam("app") String app, @QueryParam("divid") final String divid) {
		Pane widget = getUiBuilder(app).getWidget(divid);
		Pane parent = getUiBuilder(app).getParentOf(divid);
		parent.removeChild(widget);
		parent.fireLayoutChange();
		return "OK";
	}
	
	@GET
	@Path("/move")
	@Produces(MediaType.TEXT_PLAIN) 
	public String move(@PathParam("app") String app, @QueryParam("divid") final String divid, @QueryParam("top") final String top, @QueryParam("left") final String left) {
		Pane widget = getUiBuilder(app).getWidget(divid);
		Pane parent = getUiBuilder(app).getParentOf(divid);
		
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
	public String move(@PathParam("app") String app, @QueryParam("divid") final String divid, @QueryParam("parent") final String parentId) {
		Pane widget = getUiBuilder(app).getWidget(divid);
		Pane parent = getUiBuilder(app).getParentOf(divid);
		
		if (!parent.type.equals(WidgetType.Pane)) {
			return "OK";
		}
		
		Pane newParent = getUiBuilder(app).getWidget(parentId);
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
	public String buttonArray(@PathParam("app") String app, @QueryParam("divid") final String divid) {
		Pane widget = getUiBuilder(app).getWidget(divid);
		WidgetGeneratorHelper helper = new WidgetGeneratorHelper(SilverBulletServer.getStaticInstance().getBuilderModel(app).getPropertiesHolder2());
		helper.generateToggleButton(widget.id, widget);
		widget.fireLayoutChange();
		return "OK";
	}
	
	@GET
	@Path("/titledInput")
	@Produces(MediaType.TEXT_PLAIN) 
	public String titledInput(@PathParam("app") String app, @QueryParam("divid") final String divid) {
		Pane widget = getUiBuilder(app).getWidget(divid);
		WidgetGeneratorHelper helper = new WidgetGeneratorHelper(SilverBulletServer.getStaticInstance().getBuilderModel(app).getPropertiesHolder2());
		
		helper.generateTitledSetting(widget.id, widget);
		widget.id = "";
		widget.fireLayoutChange();
		return "OK";
	}
	
	@GET
	@Path("/widgetTypes")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getWidgetTypes (@PathParam("app") String app) {
		List<String> ret = new ArrayList<>();
		for (WidgetType type : WidgetType.values()) {
			ret.add(type.toString());
		}
		return ret;
	}
	
	@GET
	@Path("/setOptional")
	@Produces(MediaType.APPLICATION_JSON)
	public String setOptional (@PathParam("app") String app, @QueryParam("divid") final String divid, @QueryParam("optional") final String optional) {
		Pane widget = getUiBuilder(app).getWidget(divid);
		widget.setOptional(optional);
		widget.fireFieldChange();
		return "OK";
	}	
	
	@GET
	@Path("/copySize")
	@Produces(MediaType.APPLICATION_JSON)
	public String copySize (@PathParam("app") String app, @QueryParam("divid") final String divid) {
		Pane widget = getUiBuilder(app).getWidget(divid);
	
		Pane parent = getUiBuilder(app).getParentOf(divid);
		
		List<String> keys = Arrays.asList("display","width", "height", "padding", "margin", "font-size", "font-family");
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
	public List<String> getRootPanes(@PathParam("app") String app) {
		return getUiBuilder(app).getRootList();
	}
	
	@GET
	@Path("/addRootPane")
	@Produces(MediaType.TEXT_PLAIN)
	public String addRootPanes(@PathParam("app") String app) {
		getUiBuilder(app).createRoot();
		return "OK";
	}
	
	@GET
	@Path("/changeName")
	@Produces(MediaType.TEXT_PLAIN)
	public String changeName(@PathParam("app") String app, @QueryParam("oldName") final String oldName, @QueryParam("newName") final String newName) {
		getUiBuilder(app).changeRootName(oldName, newName);
		return "OK";
	}

	@GET
	@Path("/getRuntimeList")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getRuntimeList(@PathParam("app") String app) {
		return getUiBuilder(app).getNameList();
	}
}

package jp.silverbullet.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import jp.silverbullet.BuilderFx;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.engine.RequestRejectedException;
import jp.silverbullet.dependency.speceditor2.DependencyFormula;
import jp.silverbullet.property.ListDetailElement;
import jp.silverbullet.uidesigner.pane.LayoutConfiguration;
import jp.silverbullet.uidesigner.pane.SvPanelModel;
import jp.silverbullet.uidesigner.pane.UiElement;
import jp.silverbullet.uidesigner.widgets.Description;
import jp.silverbullet.uidesigner.widgets.WidgetFactoryFx;
import jp.silverbullet.web.obsolute.HtmlDi;
import jp.silverbullet.web.obsolute.HtmlOptionInfo;
import jp.silverbullet.web.obsolute.HtmlPane;
import jp.silverbullet.web.obsolute.HtmlUtil;
import jp.silverbullet.web.obsolute.HtmlWidgetFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/test")
public class TestResource {
	@GET
	@Path("/get")
	@Produces(MediaType.TEXT_HTML) 
	public String get () {
		String html = "<HTML>";
		html += "<BODY>";
		List<UiElement> elements = BuilderFx.getModel().getElements(BuilderFx.getModel().getTabName(0));
		
		html += search(0, elements);
		
		html += "</BODY>";
		html += "</HTML>";
		return html;
	}

	@GET
	@Path("/layout")
	@Produces(MediaType.APPLICATION_JSON) 
	public LayoutConfiguration getLayout() {
		return BuilderFx.getModel().getLayoutConfiguration(BuilderFx.getModel().getTabName(0));
	}
	
	@GET
	@Path("/caption")
	@Produces(MediaType.TEXT_PLAIN) 
	public String getCaption(@QueryParam("id") String id) {
		return BuilderFx.getModel().getBuilderModel().getProperty(id).getTitle();
	}
	
	@GET
	@Path("/button")
	@Produces(MediaType.TEXT_PLAIN) 
	public String buttonClicked(@QueryParam("id") final String id) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					BuilderFx.getModel().getBuilderModel().getDependency().requestChange(id, DependencyFormula.ANY);
				} catch (RequestRejectedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
			
		return "OK";
	}
	
	protected String search(int layer, List<UiElement> elements) {
		String html = "";
		
		for (UiElement e : elements) {
			if (e.getWidgetType().equals(UiElement.Label)) {
				
			}
			else if (e.getWidgetType().equals(UiElement.Pane)) {
				
			}
			else if (e.getWidgetType().equals(UiElement.Universal)) {
				
			}
			else {
				
			}
			
			String indent = "";
			for (int i = 0; i < layer; i++) {
				indent += "->";
			}
			SvProperty property = BuilderFx.getModel().getBuilderModel().getProperty(e.getId());
			String type =  e.getWidgetType();
			if (type.isEmpty()) {
				
				if (property.isActionProperty()) {
					type = "Button";
				}
				else if (property.isListProperty()) {
					type = "ComboBox";
				}
				else if (property.isBooleanProperty()) {
					type = "CheckBox";
				}
			}
			if (type.equals("Button")) {
				html += "<Button>" + property.getTitle() + "</Button><br>";
			}
			else if (type.equals("ComboBox")) {
				
			}
			else if (type.equals("CheckBox")) {
				html += "<input type=checkbox" + ">" + property.getTitle() + "<br>";
			}
		//	html += indent + e.getId() + ":" + e.getWidgetType() + "<br>";
			
			html += search(layer + 1, e.getLayout().getElements());
		}
		return html;
	}
	
	@GET
	@Path("/html")
	@Produces(MediaType.TEXT_HTML) 
	public String getHtml () {
		StringBuilder html = new StringBuilder();;
		html.append("<!DOCTYPE html>\n");
		html.append("<html>\n");
		html.append("<script type=\"text/javascript\" src=\"/lib/jquery-3.2.1.min.js\"></script>\n");
		html.append("<script type=\"text/javascript\">\n");
		html.append(gererateJavaScript());
		html.append("</script>\n");
		html.append("<head>\n");
		html.append("<meta charset=\"UTF-8\">\n");
		html.append("<title>Users</title>\n");
		html.append("</head>\n");
		html.append("<body>\n");
		html.append("<div id=\"area\"></div>\n");
		html.append("</body>\n");
		html.append("</html>\n");

		return html.toString();
	}

	private Integer paneNumber = 0;
	//private String currentPane = "area";
	private String gererateJavaScript() {
		paneNumber = 0;
		StringBuilder js = new StringBuilder();
		js.append("$(function() {\n");
		js.append("	$(document).ready(function(){\n");
		
		List<UiElement> elements = BuilderFx.getModel().getElements(BuilderFx.getModel().getTabName(1));
		js.append(createWidget("area", elements));
		
		js.append("	})\n");
		js.append("});\n");
		return js.toString();
	}

	protected String createWidget(String pane, List<UiElement> elements) {
		StringBuilder js = new StringBuilder();
		for (UiElement e : elements) {
			if (e.getWidgetType().equals(UiElement.Pane)) {
				String newPane = "Pane" + paneNumber;
				paneNumber++;
				js.append("$('#" + pane + "').append('<div style=\"border-style:solid;border-width:1;border-color:\"red\" id=" + newPane + ">" + "</div>');\n");
				js.append(createWidget(newPane, e.getLayout().getElements()));
			}
			else  if(e.getWidgetType().equals(UiElement.Universal)) {
				
			}
			else if (e.getWidgetType().equals(UiElement.Label)) {
				
			}
			else {
				js.append(createWidgetJs(pane, e));
			}
			
//			js.append(createWidget(pane, e.getLayout().getElements()));
		}
		return js.toString();
	}

	private String createWidgetJs(String pane, UiElement element) {
		String ret = "";
		String widgetType = element.getWidgetType();
		SvProperty prop = BuilderFx.getModel().getBuilderModel().getProperty(element.getId());
		Description description = new Description(element.getDescription());
		Description style = new Description(element.getStyle());
		if (widgetType.equals(SvPanelModel.COMBO_BOX)) {
			ret = createComboBox(pane, prop, description);
		}
		else if (widgetType.equals(SvPanelModel.RADIO_BUTTONS)){
			ret = createRadioButton(pane, prop, description);
		}
		else if (widgetType.equals(SvPanelModel.TABLE)) {
			ret = createTable(pane, prop, description);
		}
		else if (widgetType.equals(SvPanelModel.CHART_SCATTER)) {
			ret = createChart(pane, prop, description);
		}
		else if (widgetType.equals(SvPanelModel.TEXT_BOX)) {
			ret = createText(pane, prop, description);
		}
		else if (widgetType.equals(SvPanelModel.SLIDER)) {
			ret = createSlider(prop, description);
		}
		else if (widgetType.equals(SvPanelModel.TOGGLE_BUTTONS)) {
			ret = createToggleButtons(pane, prop, description);
		}
		else if (widgetType.equals(SvPanelModel.LABEL)) {
			ret = createLabel(pane, prop, description);
		}
		else if (widgetType.equals(SvPanelModel.ONE_BUTTON)) {
			ret = createOneButton(pane, prop, style, description);
		}
		else if (widgetType.equals(SvPanelModel.FUNCTIONKEY)) {
			ret = createFunctionKey(pane, prop, style, description);
		}
		else if (widgetType.equals(SvPanelModel.CHECK_BOX)) {
			ret = createCheckBox(pane, prop, description);
		}
		else if (widgetType.equals(SvPanelModel.BUTTON)) {
			ret = createButton(pane, prop, style, description);
		}
		else {
			ret = createLabel(pane, prop, description);
		}
		return ret;		
	}

	private String createButton(String pane, SvProperty prop, Description style,
			Description description) {

		StringBuilder js = new StringBuilder();
		String id = prop.getId();
		js.append("var id=\"" + id + "\";\n");
    	js.append("$('#" + pane + "').append('<button id=\' + id + \' style=\"" + getHtmlStyle(style) + "\"></button>');\n");
    	js.append("$('#' + id).text(\"" + prop.getTitle() + "\");\n");
    	if (description.isDefined(Description.HEIGHT)) {
    		js.append("$('#' + id).height(" + Double.valueOf(description.getValue(Description.HEIGHT)) + ");\n");
    	}
    	if (description.isDefined(Description.WIDTH)) {
    		js.append("$('#' + id).width(" + Double.valueOf(description.getValue(Description.WIDTH)) + ");\n");
    	}
    	js.append("$('#' + id).on('click', function() {\n");
    	js.append("	$.ajax({\n");
    	js.append("	   type: \"GET\", \n");
    	js.append("	   url: \"http://\" + window.location.host + \"/rest/test/button?id=\"  + id, \n");
    	js.append("	   	   success: function(msg){\n");
    	js.append("	   	   }\n");
    	js.append("	});\n");
    	js.append("});\n");
    	return js.toString();
	}

	private String getHtmlStyle(Description style) {
		String ret = "";
		for (String s : style.getList()) {
			if (s.contains("graphic")) {
				continue;
			}
			ret += s.replace("-fx-", "");
			if (ret.contains("size")) {
				ret += "px";
			}
			ret += ";";
		}
		if (ret.length() > 0) {
			ret = ret.substring(0, ret.length()-1);
		}
		return ret;
	}

	private String createCheckBox(String pane, SvProperty prop, Description description) {
		StringBuilder js = new StringBuilder();
		String id = prop.getId();
		js.append("var id=\"" + id + "\";\n");
		js.append("$('#" + pane + "').append('<input type=\"checkbox\" id=id >" + prop.getTitle() + "<br>');\n");
		return js.toString();
	}

	private String createFunctionKey(String pane, SvProperty prop, Description style,
			Description description) {

		StringBuilder js = new StringBuilder();
		String id = prop.getId();
		js.append("var id=\"" + id + "\";\n");
    	js.append("$('#" + pane + "').append(\'<button id=\' + id + \' style=\"" + getHtmlStyle(style) + "\"></button>\');\n");
    	js.append("$('#' + id).html('" + prop.getTitle() + "<br>" + 
    			"<font color=\"red\">" + prop.getSelectedListTitle() + "</font>" 
    			+ "');\n");
    	return js.toString();
	}

	private String createOneButton(String pane, SvProperty prop, Description style,
			Description description) {
		// TODO Auto-generated method stub
		return "";
	}

	private String createLabel(String pane, SvProperty prop, Description description) {
		StringBuilder js = new StringBuilder();
		js.append("$(\"#area\").append(\'<label id=" + wrapDoubleQuote(prop.getId()) + ">" + prop.getCurrentValue() + "</label\');\n");
		return js.toString();
	}

	private String createToggleButtons(String pane, SvProperty prop, Description description) {
		StringBuilder js = new StringBuilder();
		String divName = prop.getId();
		js.append("$('#" + pane + "').append('<div id=" + wrapDoubleQuote(divName) + ">"+ prop.getTitle() + ":</div>');\n");
		for (ListDetailElement e : prop.getAvailableListDetail()) {
			js.append("$('#" + divName + "').append('<button id=" + wrapDoubleQuote(e.getId()) + ">" +e.getTitle()  + "</button>\');\n");
		}
		return js.toString();
	}

	private String createSlider(SvProperty prop, Description description) {
		// TODO Auto-generated method stub
		return "";
	}

	private String createText(String pane, SvProperty prop, Description description) {
		StringBuilder js = new StringBuilder();
		String id = prop.getId();
		js.append("$('#" + pane + "').append('<span>" + prop.getTitle() + "</span>" + "<input type=\"text\" id=" + wrapDoubleQuote(id) + ">');\n");
		return js.toString();
	}

	private String createChart(String pane, SvProperty prop, Description description) {
		// TODO Auto-generated method stub
		return "";
	}

	private String createTable(String pane, SvProperty prop, Description description) {
		// TODO Auto-generated method stub
		return "";
	}

	private String createRadioButton(String pane, SvProperty prop, Description description) {
		StringBuilder js = new StringBuilder();
		String id = prop.getId();
		js.append("var id=\"" + id + "\";\n");
		String divName = id+"_DIV";
		js.append("$('#" + pane + "').append('<div id=" + "\"" + divName + "\"" + "></div>');\n");
		js.append("$('#" + divName + "').append('<label>" + prop.getTitle() + ":" + "</label>');\n");
		for (ListDetailElement e : prop.getAvailableListDetail()) {
			js.append("$('#" + divName + "').append('<input type=\"radio\" id=" + "\"" + wrapDoubleQuote(e.getId()) + "\"" + " name=" + "\"" + divName + "\"" + ">" + e.getTitle() + "');\n");
		}
		return js.toString();
	}

	private String createComboBox(String pane, SvProperty prop, Description description) {
		StringBuilder js = new StringBuilder();
		String id = prop.getId();
		js.append("var id=\"" + id + "\";\n");
		js.append("$('#" + pane + "').append('<select id=" + "\"" + id + "\"" + "></select>');\n");
		for (ListDetailElement e : prop.getAvailableListDetail()) {
			 js.append("$('#" + id + "').append( new Option(" + wrapDoubleQuote(e.getTitle()) + ", " + wrapDoubleQuote(e.getId()) + "));\n");
		}
		
		return js.toString();
	}

	protected String wrapDoubleQuote(String s) {
		return "\"" + s + "\"";
	}
	
	@GET
	@Path("/html2")
	@Produces(MediaType.TEXT_HTML) 
	public String getHtml2 () {
		StringBuilder html = new StringBuilder();;
		html.append("<!DOCTYPE html>\n");
		html.append("<html>\n");
		html.append("<script type=\"text/javascript\" src=\"/lib/jquery-3.2.1.min.js\"></script>\n");
		html.append("<script type=\"text/javascript\" src=\"/lib/jquery-ui.min.js\"></script>\n");
		html.append("<link rel=\"stylesheet\" href=\"/lib/jquery-ui.min.css\">");

		html.append("<script type=\"text/javascript\">\n");	
		
		html.append(gererateJavaScript2());
//		}
		html.append("</script>\n");
		html.append("<head>\n");
		html.append("<meta charset=\"UTF-8\">\n");
		html.append("<title>Users</title>\n");
		html.append("</head>\n");
		html.append("<body>\n");
//		html.append("<div id=\"area\"></div>\n");
		html.append("<div id=\"tab\">\n");
		html.append("<ul>\n");
		for (int i = 0; i < BuilderFx.getModel().getTabCount(); i++) {
			String tabName = BuilderFx.getModel().getTabName(i);
			html.append("  <li><a href=\"#" + tabName + "\">" + tabName + "</a></li>\n");
		}
		html.append("</ul>\n");

		for (int i = 0; i < BuilderFx.getModel().getTabCount(); i++) {
			String tabName = BuilderFx.getModel().getTabName(i);
			html.append("  <div id=" + HtmlUtil.wrap(tabName) + "></div>\n");
		}
		html.append("</div>");
		
		html.append("</body>\n");
		html.append("</html>\n");

		return html.toString();
	}

	private HtmlDi htmlDi = new HtmlDi() {

		@Override
		public String getTitle(String id) {
			return BuilderFx.getModel().getBuilderModel().getProperty(id).getTitle();
		}

		@Override
		public List<HtmlOptionInfo> getOptionInfo(String id) {
			List<HtmlOptionInfo> ret = new ArrayList<> ();
			for (ListDetailElement e : BuilderFx.getModel().getBuilderModel().getProperty(id).getAvailableListDetail()) {
				ret.add(new HtmlOptionInfo(e.getId(), e.getTitle()));
			}
			return ret;
		}

		@Override
		public String getValue(String id) {
			SvProperty prop = BuilderFx.getModel().getBuilderModel().getProperty(id);
			if (prop.isListProperty()) {
				return prop.getSelectedListTitle();
			}
			else {
				return prop.getCurrentValue();
			}
		}

		@Override
		public String getSelectedId(String id) {
			SvProperty prop = BuilderFx.getModel().getBuilderModel().getProperty(id);
			return prop.getCurrentValue();
		}
		
	};
	
	private String gererateJavaScript2() {
		paneNumber = 0;
		StringBuilder js = new StringBuilder();
		js.append("$(function() {\n");
		
		js.append("	$(document).ready(function(){\n");
		js.append("   $('#tab').tabs();\n");
		for (int i = 0; i < BuilderFx.getModel().getTabCount(); i++) {
			String tabName = BuilderFx.getModel().getTabName(i);
			
			List<UiElement> elements = BuilderFx.getModel().getElements(tabName);
			HtmlPane root = new HtmlPane(tabName, htmlDi);
			buildHtmlWidgetTree(root, elements);
			String script = root.getScript("root", true);
			
			js.append(script);
		}
		
		js.append("	})\n");
		
		try {
			List<String> lines = Files.readAllLines(Paths.get("C:/Projects/workspace/workspace-kepler/OtdrSimulator2/src/main/java/com/miyake/builder/web/jsfactory.js"));
			for (String line : lines) {
				js.append(line + "\n");
			}
		} catch (IOException e) {
		}

    	
    //	js.append("};\n");    	
		js.append("});\n");
		return js.toString();
	}

	private void buildHtmlWidgetTree(HtmlPane pane, List<UiElement> elements) {
		for (UiElement e : elements) {
			
			if (e.getWidgetType().equals(UiElement.Pane)) {
				String paneId = "Pane" + paneNumber++;
				HtmlPane newPane = new HtmlPane(paneId, htmlDi, e.getDescription());
				pane.add(newPane);
				buildHtmlWidgetTree(newPane, e.getLayout().getAllElements());
			}
			else {
				pane.add(HtmlWidgetFactory.create(e, htmlDi));
			}
		}
	//	return pane.getScript();
	}
	
	@GET
	@Path("/dependency")
	@Produces(MediaType.TEXT_PLAIN) 
	public String doDependency(@QueryParam("id") final String id, @QueryParam("value") final String value) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					BuilderFx.getModel().getBuilderModel().getDependency().requestChange(id, value);
				} catch (RequestRejectedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
			}
			
		});
		return "OK";
	}
	
	@GET
	@Path("/html3")
	@Produces(MediaType.TEXT_HTML) 
	public String getHtml3 () {
		StringBuilder html = new StringBuilder();;
		html.append("<!DOCTYPE html>\n");
		html.append("<html>\n");
		html.append("<script type=\"text/javascript\" src=\"/lib/jquery-3.2.1.min.js\"></script>\n");
		html.append("<script type=\"text/javascript\">\n");

		try {
			List<String> lines = Files.readAllLines(Paths.get("C:/Projects/workspace/workspace-kepler/OtdrSimulator2/src/main/java/com/miyake/builder/web/jsfactory.js"));
			for (String line : lines) {
				html.append(line);
			}
		} catch (IOException e) {
		}

		html.append("</script>\n");
		html.append("<head>\n");
		html.append("<meta charset=\"UTF-8\">\n");
		html.append("<title>Users</title>\n");
		html.append("</head>\n");
		html.append("<body>\n");
		html.append("<div id=\"area\"></div>\n");
		html.append("</body>\n");
		html.append("</html>\n");

		return html.toString();
	}
	
	@GET
	@Path("/property")
	@Produces(MediaType.APPLICATION_JSON) 
	public SvProperty getProperty (@QueryParam("id") final String id) {
		return BuilderFx.getModel().getBuilderModel().getProperty(id);
	}
	
	@GET
	@Path("/enabled")
	@Produces(MediaType.TEXT_PLAIN) 
	public String getEnabled (@QueryParam("id") final String id) {
		return String.valueOf(BuilderFx.getModel().getBuilderModel().getProperty(id).isEnabled());
	}
}

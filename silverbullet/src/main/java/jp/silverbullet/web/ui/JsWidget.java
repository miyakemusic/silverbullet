package jp.silverbullet.web.ui;

import java.util.ArrayList;
import java.util.List;

import jp.silverbullet.web.Pair;

public class JsWidget {

	public static final String FLOWLAYOUT = "Flow Layout";
	public static final String ABSOLUTELAYOUT = "Absolute Layout";
	public static final String VERTICALLAYOUT = "Vertical Layout";
	
	public static final String PANEL = "PANEL";
	public static final String COMBOBOX = "COMBOBOX";
	public static final String RADIOBUTTON = "RADIOBUTTON";
	public static final String TEXTFIELD = "TEXTFIELD";
	public static final String TOGGLEBUTTON = "TOGGLEBUTTON";
	public static final String ACTIONBUTTON = "ACTIONBUTTON";
	public static final String CHART = "CHART";
	public static final String TABLE = "TABLE";
	public static final String CHECKBOX = "CHECKBOX";
	public static final String GUI_DIALOG = "GUI_DIALOG";
	public static final String TAB = "TAB";
	public static final String LABEL = "LABEL";
	
	private String id = "";
	private String presentation = "";
	private String widgetType = "";
	private String width = "";
	private String height = "";
	private String top = "";
	private String left = "";
	private String layout = "";
	private List<JsWidget> children = new ArrayList<>();
	private int unique;
	private String styleClass;
	private String css;
	private String custom;
	private Integer index = 0;
	public JsWidget() {
		this.unique = this.hashCode();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getWidgetType() {
		return widgetType;
	}
	public void setWidgetType(String widgetType) {
		this.widgetType = widgetType;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}

	public List<JsWidget> getChildren() {
		return children;
	}
	public void setChildren(List<JsWidget> children) {
		this.children = children;
	}
	
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	
	public String getTop() {
		return top;
	}
	public void setTop(String top) {
		this.top = top;
	}
	public String getLeft() {
		return left;
	}
	public void setLeft(String left) {
		this.left = left;
	}
	
	public int getUnique() {
		return unique;
	}

	public void addChild(JsWidget child) {
		this.children.add(child);
	}
	
	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public void setStyleClass(String style) {
		this.styleClass = style;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		this.css = css;
	}

	public String getPresentation() {
		return presentation;
	}

	public void setPresentation(String presentation) {
		this.presentation = presentation;
	}

	public String getCustom() {
		return custom;
	}

	public void setCustom(String custom) {
		this.custom = custom;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}
	
}

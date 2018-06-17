package jp.silverbullet.web.ui;

import java.util.ArrayList;
import java.util.List;

public class JsWidget {

	public static final String PANEL = "PANEL";
	public static final String COMBOBOX = "COMBOBOX";
	public static final String TEXTFIELD = "TEXTFIELD";
	public static final String BUTTON = "BUTTON";
	
	private String id;
	private String widgetType;
	private String width;
	private String height;
	private String top;
	private String left;
	private List<JsWidget> children = new ArrayList<>();
	private int unique;
	
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
//		if (this.children == null) {
//			this.children = new JsWidget[1];
//			this.children[0] = child;
//		}
//		else {
//			List<JsWidget> tmp = new ArrayList<JsWidget>(Arrays.asList(this.children));
//			tmp.add(child);
//			this.children = tmp.toArray(new JsWidget[0]);
//		}
	}
}

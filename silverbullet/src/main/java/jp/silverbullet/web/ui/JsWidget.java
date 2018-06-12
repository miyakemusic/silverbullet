package jp.silverbullet.web.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsWidget {

	public static final String PANEL = "PANEL";
	public static final String COMBOBOX = "COMBOBOX";
	public static final String TEXTFIELD = "TEXTFIELD";
	public static final String BUTTON = "BUTTON";
	
	private String id;
	private String widgetType;
	private double width;
	private double height;
	private double top;
	private double left;
	private JsWidget[] children = null;
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
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}

	public JsWidget[] getChildren() {
		return children;
	}
	public void setChildren(JsWidget[] children) {
		this.children = children;
	}
	
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	
	public double getTop() {
		return top;
	}
	public void setTop(double top) {
		this.top = top;
	}
	public double getLeft() {
		return left;
	}
	public void setLeft(double left) {
		this.left = left;
	}
	
	public int getUnique() {
		return unique;
	}

	public void addChild(JsWidget child) {
		if (this.children == null) {
			this.children = new JsWidget[1];
			this.children[0] = child;
		}
		else {
			List<JsWidget> tmp = new ArrayList<JsWidget>(Arrays.asList(this.children));
			tmp.add(child);
			this.children = tmp.toArray(new JsWidget[0]);
		}
	}
}

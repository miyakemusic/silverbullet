package jp.silverbullet.web.ui;

import java.util.Arrays;
import java.util.List;

public class JsWidget {

	public static final String Panel = "PANEL";
	public static final String COMBOBOX = "COMBOBOX";
	public static final String TEXTFIELD = "TEXTFIELD";
	
	private String id;
	private String widgetType;
	private double width;
	private double height;
	private double top;
	private double left;
	private JsWidget[] children;
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
		List<JsWidget> tmp = Arrays.asList(this.children);
		tmp.add(child);
		this.children = tmp.toArray(new JsWidget[0]);
	}
}

package jp.silverbullet.web.ui;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JsWidget implements Cloneable {

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
	public static final String CHART_CANVASJS = "CHART_CANVASJS";
	public static final String TABLE = "TABLE";
	public static final String CHECKBOX = "CHECKBOX";
	public static final String GUI_DIALOG = "GUI_DIALOG";
	public static final String TAB = "TAB";
	public static final String LABEL = "LABEL";
	public static final String MESSAGEBOX = "MESSAGEBOX";
	public static final String CSSBUTTON = "CSSBUTTON";
	public static final String ROOT = "ROOT";
	public static final String REGISTERSHORTCUT = "REGISTERSHORTCUT";
	public static final String DATATABLE = "DATATABLE";
	
	private String id = "";
	private String presentation = "";
	private String widgetType = "";
	private String width = "";
	private String height = "";
	private String top = "";
	private String left = "";
	private String layout = "";
	private String fontsize = "";
	private List<JsWidget> children = new ArrayList<>();
	private int unique = -1;
	private String styleClass = "";
	private String css = "";
	private Integer index = 0;
	private Boolean editable = true;
	private Map<String, String> custom = new HashMap<>();
	private int parentDiv;
	
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

	@JsonIgnore
	public String getUniqueText() {
		return String.valueOf(this.unique);
	}
	
	public void addChild(JsWidget child) {
		this.children.add(child);
		child.setParentDiv(this.unique);
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

	public Map<String, String> getCustom() {
		return custom;
	}

	public void setCustom(Map<String, String> custom) {
		this.custom = custom;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
		
		if (this.getWidgetType().equals(JsWidget.PANEL)) {
			for (JsWidget child : this.getChildren()) {
				child.setIndex(index);
			}
		}
	}

	public String getFontsize() {
		return fontsize;
	}

	public void setFontsize(String fontsize) {
		this.fontsize = fontsize;
	}

	public String getCustomElement(String id) {
		String v = this.custom.get(id);
		if (v == null) {
			v = "";
		}
		return v;
	}

	public void setField(String propertyType, Object value, Class clazz) {
		Method method;
		try {
			method = this.getClass().getMethod("set" + StringUtils.capitalize(propertyType), clazz);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}

		Object ret; 
		try {
			ret = method.invoke(this, value);
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
		
	}

	public Boolean getEditable() {
		return editable;
	}

	public void setEditable(Boolean editable) {
		this.editable = editable;
	}
	
	public JsWidget clone() {
		try {
			JsWidget ret=  (JsWidget)super.clone();
			ret.children = new ArrayList<>();
			for (JsWidget child : this.getChildren()) {
				ret.children.add(child.clone());
			}
			ret.custom = new HashMap<>(this.custom);
			
			ret.unique = ret.hashCode();
			return ret;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setCustomElemnt(String key, String value) {
		this.custom.put(key, value);
	}

	public int getParentDiv() {
		return parentDiv;
	}

	public void setParentDiv(int parantDiv) {
		this.parentDiv = parantDiv;
	}

	public static List<String> getAllWidgetTypes() {
		return Arrays.asList(JsWidget.ROOT, JsWidget.TOGGLEBUTTON, JsWidget.CSSBUTTON, JsWidget.ACTIONBUTTON, 
				JsWidget.COMBOBOX, JsWidget.RADIOBUTTON, JsWidget.TEXTFIELD,
				JsWidget.CHART, JsWidget.CHART_CANVASJS, JsWidget.TABLE, JsWidget.DATATABLE, JsWidget.CHECKBOX, JsWidget.GUI_DIALOG, 
				JsWidget.PANEL, JsWidget.TAB, JsWidget.LABEL, JsWidget.MESSAGEBOX, JsWidget.REGISTERSHORTCUT);

	}
	
}

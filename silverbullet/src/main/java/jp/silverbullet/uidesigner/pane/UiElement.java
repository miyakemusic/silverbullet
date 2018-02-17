package jp.silverbullet.uidesigner.pane;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
@XmlRootElement
public class UiElement implements Cloneable {
	public static final String Pane = "Pane";
	public static final String Universal = "Universal";
	public static final String Label = "Label";

	@Override
	public UiElement clone() {
		try {
			return (UiElement)super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private String id = "";
	private String widgetType = "";
	private Object pointer;
	private String style = "-fx-padding:1;";
	private String description = "";

	@XmlTransient
	private boolean selected;
	@XmlTransient
	private UiElementListener listener;
	
	@XmlTransient
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		if (this.listener != null) {
			this.listener.onSelectChanged(selected);
		}
	}
	private LayoutConfiguration layout = new LayoutConfiguration();
	
	public LayoutConfiguration getLayout() {
		return layout;
	}

	public void setLayout(LayoutConfiguration layout) {
		this.layout = layout;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getWidgetType() {
		return widgetType;
	}

	public void setWidgetType(String widgetType) {
		this.widgetType = widgetType;
	}
	@XmlTransient
	public Object getPointer() {
		return pointer;
	}

	public void setPointer(Object pointer) {
		this.pointer = pointer;
	}

	public UiElement() {
		
	}

	public UiElement(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@XmlTransient
	public UiElementListener getListener() {
		return listener;
	}

	public void setListener(UiElementListener listener) {
		this.listener = listener;
	}

	public enum LayoutType {
		Flow,
		Vertical,
		Horizontal,
		Absolute
	}
		
	public LayoutType getLayoutType() {
		String[] tmp = this.description.split(";");
		for (String s : tmp) {
			if (s.startsWith("-layout:")) {
				return LayoutType.valueOf(s.replace("-layout:", "").trim());
			}
		}
		return null;
	}

	public void fireUpdate() {
		this.listener.onPropertyUpdated();
	}
}

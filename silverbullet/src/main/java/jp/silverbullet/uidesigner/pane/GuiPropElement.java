package jp.silverbullet.uidesigner.pane;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;

public class GuiPropElement {
	private SimpleStringProperty title;// = new SimpleStringProperty();
	private SimpleStringProperty value;// = new SimpleStringProperty();
	public GuiPropElement(String title, String value, ChangeListener<String> listener) {
		this.title = new SimpleStringProperty(title);
		this.value = new SimpleStringProperty(value);
		this.value.addListener(listener);
	}
	public GuiPropElement(){}
	public String getTitle() {
		return title.get();
	}
	public String getValue() {
		return value.get();
	}
	public void setTitle(String title) {
		this.title.set(title);
	}
	public void setValue(String value) {
		this.value.set(value);
	}
}

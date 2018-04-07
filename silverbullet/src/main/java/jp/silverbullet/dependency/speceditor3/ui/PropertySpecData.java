package jp.silverbullet.dependency.speceditor3.ui;

import javafx.beans.property.SimpleStringProperty;

public class PropertySpecData {
	private SimpleStringProperty title;
	private SimpleStringProperty value;
	
	public PropertySpecData(String title, String value) {
		this.title = new SimpleStringProperty(title);
		this.value = new SimpleStringProperty(value);
	}


	public String getTitle() {
		return title.get();
	}

	public String getValue() {
		return value.get();
	}
	
}

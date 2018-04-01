package jp.silverbullet.dependency.speceditor3.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jp.silverbullet.dependency.speceditor3.DependencyExpression;

public class DependencyTableRowData {
	private StringProperty element;
	private StringProperty value;
	private StringProperty condition;
	private DependencyExpression pointer;
	
	public DependencyTableRowData() {}
	public DependencyTableRowData(String element, String value, String condition, DependencyExpression pointer) {
		this.element = new SimpleStringProperty(element);
		this.value = new SimpleStringProperty(value);
		this.condition = new SimpleStringProperty(condition);
		this.pointer = pointer;
	}
	public String getElement() {
		return element.get();
	}
	public String getValue() {
		return value.get();
	}
	public String getCondition() {
		return condition.get();
	}
	public void setElement(StringProperty element) {
		this.element = element;
	}
	public void setValue(StringProperty value) {
		this.value = value;
	}
	public void setCondition(StringProperty condition) {
		this.condition = condition;
	}
	public DependencyExpression getPointer() {
		return pointer;
	}
	
}

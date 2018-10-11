package jp.silverbullet.dependency;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlRootElement()
public class DependencyTableRowData {
	private StringProperty element;
	private StringProperty value;
	private StringProperty condition;
	private StringProperty confirmation;
	
	@JsonIgnore
	private DependencyExpression pointer;
	
	public DependencyTableRowData() {}
	public DependencyTableRowData(String element, String value, String condition, boolean confirmation, DependencyExpression pointer) {
		this.element = new SimpleStringProperty(element);
		this.value = new SimpleStringProperty(value);
		this.condition = new SimpleStringProperty(condition);
		this.confirmation = new SimpleStringProperty(String.valueOf(confirmation));
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
	public String getConfirmation() {
		return confirmation.get();
	}
	public void setConfirmation(StringProperty confirmation) {
		this.confirmation = confirmation;
	}
	
}

package jp.silverbullet.dependency;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlRootElement()
public class DependencyTableRowData {
	private String element;
	private String value;
	private String condition;
	private String confirmation;
	
	@JsonIgnore
	private DependencyExpression pointer;
	
	public DependencyTableRowData() {}
	public DependencyTableRowData(String element, String value, String condition, boolean confirmation, DependencyExpression pointer) {
		this.element = element;
		this.value = value;
		this.condition = condition;
		this.confirmation = String.valueOf(confirmation);
		this.pointer = pointer;
	}
	public String getElement() {
		return element;
	}
	public String getValue() {
		return value;
	}
	public String getCondition() {
		return condition;
	}
	public void setElement(String element) {
		this.element = element;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public DependencyExpression getPointer() {
		return pointer;
	}
	public String getConfirmation() {
		return confirmation;
	}
	public void setConfirmation(String confirmation) {
		this.confirmation = confirmation;
	}
	
}

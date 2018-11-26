package jp.silverbullet.dependency2;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Expression {

	private String value;
	private String trigger;
	private boolean valueCalculationEnabled;
	private String condition;
	
	public Expression() {}
	
	public Expression(String value, String trigger, String condition) {
		this.value = value;
		this.trigger = trigger;
		this.condition = condition;
	}

	public boolean qualifies(String id) {
		return IdCollector.collectIds(this.trigger).contains(id);
	}

	public String getTrigger() {
		return this.trigger;
	}

	public String getValue() {
		return value;
	}

	@JsonIgnore
	public boolean isElse() {
		return this.trigger.equals(DependencySpec.Else);
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setValueCalculationEnabled(boolean b) {
		this.valueCalculationEnabled= b;
	}

	public boolean isValueCalculationEnabled() {
		return valueCalculationEnabled;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
	
}

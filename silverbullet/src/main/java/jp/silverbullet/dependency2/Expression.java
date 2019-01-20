package jp.silverbullet.dependency2;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Expression implements Cloneable {

	private String value = "";
	private String trigger = "";
	private boolean valueCalculationEnabled = false;
	private String condition = "";
	
	public Expression() {}
	
	public Expression(String value, String trigger, String condition) {
		this.value = value;
		this.trigger = trigger;
		this.condition = condition;
	}

	public boolean qualifies(String id) {
		return IdUtility.collectIds(this.trigger).contains(id);
	}

	public String getTrigger() {
		return this.trigger;
	}

	public String getValue() {
		return value;
	}

	@JsonIgnore
	public boolean isElse() {
		return this.trigger.equalsIgnoreCase(DependencySpec.Else);
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

	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}

	@JsonIgnore
	public boolean isConditionEnabled() {
		return (this.condition != null) && !this.condition.isEmpty();
	}

	@Override
	protected Expression clone() {
		try {
			return (Expression)super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void changeId(String prevId, String newId) {
		this.condition = IdUtility.replaceId(this.condition, prevId, newId);
		this.trigger = IdUtility.replaceId(this.trigger, prevId, newId);
		this.value =IdUtility.replaceId(this.value, prevId, newId);	
	}

	
}

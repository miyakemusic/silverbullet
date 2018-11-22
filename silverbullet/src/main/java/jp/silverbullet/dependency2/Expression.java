package jp.silverbullet.dependency2;

public class Expression {

	private String value;
	private String trigger;
	private boolean valueCalculationEnabled;
	
	public Expression(String value, String trigger) {
		this.value = value;
		this.trigger = trigger;
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
	
}

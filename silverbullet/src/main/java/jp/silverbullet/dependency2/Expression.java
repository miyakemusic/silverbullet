package jp.silverbullet.dependency2;

import jp.silverbullet.dependency.IdCollector;

public class Expression {

	private String value;
	private String trigger;
	
	public Expression(String value, String trigger) {
		this.value = value;
		this.trigger = trigger;
	}

	public boolean qualifies(String id, String value) {
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
	
}

package jp.silverbullet.dependency2;

import java.util.List;

public class DependencySpec {

	public static final String True = "True";
	public static final String False = "False";
	public static final String Value = "Value";
	public static final String Enable = "Enable";
	public static final String Min = "Min";
	public static final String Max = "Max";
	
	public static final String Else = "*Else";
	private String id;
	private OptionEnableHolder optionEnableHolder = new OptionEnableHolder();
	
	public DependencySpec(String id) {
		this.id = id;
	}
	
	public void addOptionEnabled(String option, String enabled, String trigger, String condition) {
		this.optionEnableHolder.add(option, enabled, trigger, condition);
	}
	
	public void addOptionEnabled(String option, String enabled, String trigger) {
		addOptionEnabled(option, enabled, trigger, "");
	}

	public void addEnable(String enabled, String trigger, String condition) {
		this.optionEnableHolder.add(DependencySpec.Enable, enabled, trigger, condition);
	}
	
	public void addEnable(String enabled, String trigger) {
		addEnable(enabled, trigger, "");
	}

	public void addOptionSelect(String option, String trigger, String condition) {
		this.optionEnableHolder.add(DependencySpec.Value, option, trigger, condition);
	}
	
	public void addOptionSelect(String option, String trigger) {
		addOptionSelect(option, trigger, "");
	}
		
	public void addCalculation(String calculation, String condition) {
		List<String> ids = IdCollector.collectIds(calculation);
		for (String id : ids) {
			this.optionEnableHolder.addValueCalculation(DependencySpec.Value, calculation, "$" + id + "==" + "$" + id, condition);
		}		
	}
	public void addCalculation(String calculation) {
		addCalculation(calculation, "");
	}
	
	public void addValue(String value, String trigger, String condition) {
		this.optionEnableHolder.add(DependencySpec.Value, value, trigger, condition);
	}

	public void addValue(String value, String trigger) {
		addValue(value, trigger, "");
	}
	
	public String getId() {
		return id;
	}

	public ExpressionHolder qualifies(String id2) {
		return optionEnableHolder.qualifies(id2);
	}

	public void addMin(double min, String trigger, String condition) {
		this.optionEnableHolder.add(DependencySpec.Min, String.valueOf(min), trigger, condition);
	}
	
	public void addMin(double min, String trigger) {
		addMin(min, trigger, "");
	}

	public void addMax(double max, String trigger, String condition) {
		this.optionEnableHolder.add(DependencySpec.Max, String.valueOf(max), trigger, condition);
	}
	
	public void addMax(double max, String trigger) {
		addMax(max, trigger, "");
	}
}

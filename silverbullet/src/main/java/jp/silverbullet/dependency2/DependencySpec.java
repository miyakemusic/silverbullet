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

	public void addOptionEnabled(String option, String enabled, String trigger) {
		this.optionEnableHolder.add(option, enabled, trigger);
	}

	public void addEnable(String enabled, String trigger) {
		this.optionEnableHolder.add(DependencySpec.Enable, enabled, trigger);
	}

	public void addOptionSelect(String option, String trigger) {
		this.optionEnableHolder.add(DependencySpec.Value, option, trigger);
	}
	
	public void addOptionSelect(String option, String trigger, String condition) {
		this.optionEnableHolder.add(DependencySpec.Value, option, trigger);
	}
	
	public void addCalculation(String calculation) {
		List<String> ids = IdCollector.collectIds(calculation);
		for (String id : ids) {
			this.optionEnableHolder.addValueCalculation(DependencySpec.Value, calculation, "$" + id + "==" + "$" + id);
		}
	}
	
	public void addValue(String value, String trigger) {
		this.optionEnableHolder.add(DependencySpec.Value, value, trigger);
	}

	public String getId() {
		return id;
	}

	public ExpressionHolder qualifies(String id2) {
		return optionEnableHolder.qualifies(id2);
	}

	public void addMin(double min, String trigger) {
		this.optionEnableHolder.add(DependencySpec.Min, String.valueOf(min), trigger);
	}

	public void addMax(double max, String trigger) {
		this.optionEnableHolder.add(DependencySpec.Max, String.valueOf(max), trigger);
	}
}

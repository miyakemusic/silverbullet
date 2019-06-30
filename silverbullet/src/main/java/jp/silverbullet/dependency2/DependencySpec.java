package jp.silverbullet.dependency2;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DependencySpec {

	public static final String True = "true";
	public static final String False = "false";
	
	public static final String Else = "*Else";
	
	public static final String Value = "Value";
	public static final String Enable = "Enable";
	public static final String OptionEnable = "OptionEnable";
	public static final String Min = "Min";
	public static final String Max = "Max";
	public static final String ArraySize = "ArraySize";
	public static final String Title = "Title";
	public static final String Unit = "Unit";
	public static final String Null = "---";
		
	public static final String Trigger = "Trigger";
	public static final String Condition = "Condition";
	

	
	private String id;
	private DependencySpecDetail dependencySpecDetail = new DependencySpecDetail();
	
	public DependencySpec() {}
	
	public DependencySpec(String id) {
		this.id = id;
	}
	
	public void addOptionEnabled(String option, String enabled, String trigger, String condition) {
		this.dependencySpecDetail.add(DependencySpec.OptionEnable, option, enabled, trigger, condition);
	}
	
	public DependencySpec addOptionEnabled(String option, String enabled, String trigger) {
		addOptionEnabled(option, enabled, trigger, "");
		return this;
	}

	public void addEnable(String enabled, String trigger, String condition) {
		this.dependencySpecDetail.add(DependencySpec.Enable, enabled, trigger, condition);
	}
	
	public void addEnable(String enabled, String trigger) {
		addEnable(enabled, trigger, "");
	}

	public void addOptionSelect(String option, String trigger, String condition) {
		this.dependencySpecDetail.add(DependencySpec.Value, option, trigger, condition);
	}
	
	public void addOptionSelect(String option, String trigger) {
		addOptionSelect(option, trigger, "");
	}
		
	public void addCalculation(String calculation, String condition) {
		List<String> ids = IdUtility.collectIds(calculation);
		for (String id : ids) {
			this.dependencySpecDetail.addValueCalculation(DependencySpec.Value, calculation, "$" + id + "==" + "$" + id, condition);
		}		
	}
	public void addCalculation(String calculation) {
		addCalculation(calculation, "");
	}
	
	public DependencySpec addValue(String value, String trigger, String condition) {
		this.dependencySpecDetail.add(DependencySpec.Value, value, trigger, condition);
		return this;
	}

	public DependencySpec addValue(String value, String trigger, String condition, boolean silentChange) {
		this.dependencySpecDetail.add(DependencySpec.Value, value, trigger, condition).silentChange(silentChange);
		return this;
	}
	
	public DependencySpec addValue(String value, String trigger) {
		addValue(value, trigger, "");
		return this;
	}
	
	public String getId() {
		return id;
	}

	public ExpressionHolder qualifies(String id2) {
		return dependencySpecDetail.qualifies(id2);
	}

	public void addMin(double min, String trigger, String condition) {
		addMin(String.valueOf(min), trigger, condition);
		//this.dependencySpecDetail.add(DependencySpec.Min, String.valueOf(min), trigger, condition);
	}
	
	public void addMin(String min, String trigger) {
		addMin(min, trigger, "");
	}
	
	public void addMin(String min, String trigger, String condition) {
		this.addMin(min, trigger, condition, false);
	}
	
	public void addMin(String min, String trigger, String condition, boolean silentChange) {
		this.dependencySpecDetail.add(DependencySpec.Min, min, trigger, condition).silentChange(silentChange);
	}
	
	public DependencySpec addMin(double min, String trigger) {
		addMin(min, trigger, "");
		return this;
	}

	public void addMax(String max, String trigger) {
		addMax(max, trigger, "");
	}
	
	public void addMax(String max, String trigger, String condition) {
		this.addMax(max, trigger, condition, false);
	}
	
	public void addMax(String max, String trigger, String condition, boolean silentChange) {
		this.dependencySpecDetail.add(DependencySpec.Max, max, trigger, condition).silentChange(silentChange);
	}
	
	public void addMax(double max, String trigger, String condition) {
		this.addMax(String.valueOf(max), trigger, condition);
	}
	
	public DependencySpec addMax(double max, String trigger) {
		addMax(max, trigger, "");
		return this;
	}

	public void addArraySize(int size, String trigger, String condition) {
		this.dependencySpecDetail.add(DependencySpec.ArraySize, String.valueOf(size), trigger, condition);
	}
	
	public List<Expression> getExpression(String targetElement) {
		targetElement = convertTargetElement(targetElement);
		return this.dependencySpecDetail.get(targetElement);
	}

	private String convertTargetElement(String targetElement) {
		if (targetElement.startsWith(this.id)) {
			targetElement = DependencySpec.OptionEnable + "#" + targetElement;
		}
		return targetElement;
	}

	@JsonIgnore
	public List<String> getTargetOptions() {
		return this.dependencySpecDetail.getTargetOptions();
	}

	public boolean containsTarget(String targetElement) {
		return this.dependencySpecDetail.containsTarget(targetElement);
	}

	@JsonIgnore
	public Set<String> getTriggerIds() {
		return this.dependencySpecDetail.getTriggerIds();
	}

	public DependencySpecDetail getDependencySpecDetail() {
		return dependencySpecDetail;
	}

	public void setDependencySpecDetail(DependencySpecDetail dependencySpecDetail) {
		this.dependencySpecDetail = dependencySpecDetail;
	}

	public void update(String element, Integer row, String field, String value) {
		if (element.startsWith(id)) {
			element = DependencySpec.OptionEnable + "#" + element;
		}
		this.dependencySpecDetail.update(element, row, field, value);
	}

	public void copySpec(String fromTargetElement, String toTargetElement) {
		this.dependencySpecDetail.copySpec(this.convertTargetElement(fromTargetElement), this.convertTargetElement(toTargetElement));
	}

	public void setId(String id) {
		this.id = id;
	}

	public void changeId(String prevId, String newId) {
		this.dependencySpecDetail.changeId(prevId, newId);
	}

	public void clear() {
		this.dependencySpecDetail.clear();
	}

	public void clear(String targetElement, String triggerId) {
		this.dependencySpecDetail.clear(targetElement, triggerId);
	}

	public void addSpecs(String targetElement, List<Expression> specs) {
		for (Expression exp : specs) {
			this.dependencySpecDetail.add(targetElement, exp.getValue(), exp.getTrigger(), exp.getCondition());
		}
	}



}

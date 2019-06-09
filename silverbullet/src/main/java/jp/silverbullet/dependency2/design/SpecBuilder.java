package jp.silverbullet.dependency2.design;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.silverbullet.dependency2.DependencySpec;
import jp.silverbullet.dependency2.DependencySpecHolder;
import jp.silverbullet.property2.PropertyDef2;

public abstract class SpecBuilder {
	public void buildSpec() {
		DependencySpecHolder holder = getDependencySpecHolder();
		holder.clear();

		// enable
		for (int priority : this.getDefinedPriorities()) {
			List<String> idsSamePriority = this.getPriorities().get(priority);
			
			List<String> idsLowerPriority = getLowerPriorityIds(priority);
			
			for (String triggerId : idsSamePriority) {
				// Calculates same priority IDs
				for (String targetId : idsSamePriority) {
					if (!targetId.equals(triggerId) && (isMainId(targetId) && isMainId(triggerId))) {
						if (isList(targetId) && isList(triggerId)) {
							calculateSamePriorityIds(triggerId, targetId);
						}
						else if (isNumeric(targetId) && isList(triggerId)) {
							calculateLowerPriorityIds(triggerId, targetId);
						}
					}
				}
				// Calculates lower priority IDs
				for (String targetId : idsLowerPriority) {
					calculateLowerPriorityIds(triggerId, targetId);
				}
			}
		}
		
		// Value
		for (String targetId: this.getData().getValueTargetIds()) {
			for (String triggerId : this.getData().getValueTriggerId(targetId)) {
				String value = this.getData().getValue(triggerId, targetId);
				if (value.isEmpty()) {
					continue;
				}
				
				PropertyDef2 targetProp = this.getPropertyDef(targetId);
				PropertyDef2 triggerProp = this.getPropertyDef(this.getMainId(triggerId));
				
				if (targetProp.isNumeric()) {
					if (value.equals(">")) {
						this.getDependencySpecHolder().getSpec(targetId).addValue("$" + triggerId, "$" + targetId + "<$" + triggerId);
					}
					else if (value.equals("<")) {
						this.getDependencySpecHolder().getSpec(targetId).addValue("$" + triggerId, "$" + targetId + ">$" + triggerId);						
					}
					else {
						this.getDependencySpecHolder().getSpec(targetId).addValue(value, "$" + this.getMainId(triggerId) + "==" + attachSign(triggerId));
					}
				}
				else if (targetProp.isList()) {
					if (triggerProp.isList()) {
						if (this.isOptionId(triggerId)) {
							String value2 = value;
							String condition = "";
							if (value.contains("(")) {
								String[] tmp = value.split("()");
								value2 = value.split("\\(")[0];
							}
							this.getDependencySpecHolder().getSpec(targetId).addValue(value2, "$" + this.getMainId(triggerId) + "==%" + triggerId, condition);
						}
					}
					else if (triggerProp.isNumeric()) {
						String val2 = "";
						String triggerCond = "";
						if (value.contains(":")) {
							String[] tmp = value.split(":");
							val2 = tmp[0];
							if (tmp[1].equals("*")) {
								triggerCond = "$" + this.getMainId(triggerId) + "==" +  "$" + this.getMainId(triggerId);
							}
							else {
								triggerCond = "$" + this.getMainId(triggerId) + "==" + tmp[1];
							}
							
						}
						this.getDependencySpecHolder().getSpec(targetId).addValue(val2, triggerCond);
					}
					else {
						System.out.println();
					}
				}
			}
		}
	}
	
	protected abstract Map<Integer, List<String>> getPriorities();

	private List<String> getLowerPriorityIds(int priority) {
		Set<String> ret = new HashSet<>();
		for (int p : this.getDefinedPriorities()) {
			if (p < priority) {
				ret.addAll(this.getPriorities().get(p));
			}
		}
		return new ArrayList<String>(ret);
	}

	private String attachSign(String triggerId) {
		if (this.isMainId(triggerId)) {
			return "$" + triggerId;
		}
		return "%" + triggerId;
	}
	
	private void calculateSamePriorityIds(String triggerId, String targetId) {
		List<String> targetOptions = this.getPropertyDef(targetId).getOptionIds();
		List<String> triggerOptions = this.getPropertyDef(triggerId).getOptionIds();

		if (!hasRelation(triggerId, targetId)) {
			return;
		}
		
		targetOptions.add(targetId);
		triggerOptions.add(triggerId);
		
		for (String targetOption : targetOptions) {
			if (targetOption.equals(this.getMainId(targetOption))) { // ignore main ID
				continue;
			}
			for (String triggerOption : triggerOptions) {
				if (triggerOption.equals(this.getMainId(triggerOption))) { // ignore main ID
					continue;
				}
				String enabledCondition = "";
				for (String targetOption2 : targetOptions) {
					if (targetOption.equals(targetOption2)) {
						continue;
					}
					
					if (this.getData().contains(targetOption2, triggerOption)) {
						enabledCondition += "($" + targetId + "!=%" + targetOption2+ ")&&";
					}
				}
				if (enabledCondition.length() > 0) {
					enabledCondition = enabledCondition.substring(0, enabledCondition.length()-2);
				}
				
				if (this.getData().contains(targetOption, triggerOption)) {
					this.getDependencySpecHolder().getSpec(targetId).addValue(targetOption, 
							"$" + triggerId + "==%" + triggerOption, enabledCondition);					
				}
			}
		}
	}
	
	private void calculateLowerPriorityIds(String triggerId, String targetId) {
		List<String> targetOptions = this.getPropertyDef(targetId).getOptionIds();
		List<String> triggerOptions = this.getPropertyDef(triggerId).getOptionIds();
		targetOptions.add(targetId);
		triggerOptions.add(triggerId);
				
		List<String> usedOptions = new ArrayList<String>(getData().getEnableRelation().keySet());
		
		for (String targetOption : targetOptions) {
			boolean optionEnabledTouched = false;
			boolean enabledTouched = false;
//			boolean valueTouched = false;
			for (String triggerOption : triggerOptions) {
				if (isMainId(triggerOption) && this.hasRelation(targetId, triggerId)) {
					if (this.getPropertyDef(triggerId).isBoolean()) {
						if (this.getData().contains(targetOption, triggerOption)) {
				
							getDependencySpecHolder().getSpec(targetId).addOptionEnabled(targetOption, DependencySpec.True, 
									"$" + triggerId + "==true", "");		
							getDependencySpecHolder().getSpec(targetId).addOptionEnabled(targetOption, DependencySpec.False, 
									"$" + triggerId + "==false", "");								
						}
						else {
						//	String triggerExpression = "$" + triggerId + "==" + "$" + triggerId;
						//	getDependencySpecHolder().getSpec(targetId).addOptionEnabled(targetOption, DependencySpec.False, 
						//			triggerExpression, "");	
						}
					}
				}
				else {
					if (getData().contains(targetOption, triggerOption)) {
						String condition = createCondition(targetOption, triggerOption, usedOptions);
						
						String triggerExpression = "$" + triggerId + "==%" + triggerOption;
						if (isMainId(targetOption)) {
							getDependencySpecHolder().getSpec(targetId).addEnable(DependencySpec.True, 
									triggerExpression, condition);
							enabledTouched = true;
						}
						else {
							getDependencySpecHolder().getSpec(targetId).addOptionEnabled(targetOption, DependencySpec.True, 
									triggerExpression, condition);
							optionEnabledTouched = true;
						}
					}
				}
			}
			if (optionEnabledTouched) {
				getDependencySpecHolder().getSpec(targetId).addOptionEnabled(targetOption, DependencySpec.False, 
						DependencySpec.Else);		
			}
			if (enabledTouched) {
				getDependencySpecHolder().getSpec(targetId).addEnable(DependencySpec.False, DependencySpec.Else);
			}

		}
		
	}

	private String createCondition(String targetOption, String triggerOption, List<String> triggerOptions) {
		String triggerMainId = this.getMainId(triggerOption);
		String targetMainId = this.getMainId(targetOption);
		String ret = "";
	
		if (triggerMainId.equals(targetMainId)) {
			return "";
		}
		for (String option : triggerOptions) {
			String id = this.getMainId(option);
			
			if (!this.getData().contains(targetOption, option)) {
				continue;
			}
			if (id.equals(triggerMainId)/* || id.equals(targetMainId)*/) {
				continue;
			}
			if (this.getPriority(id) >= this.getPriority(triggerMainId)) {
				if (this.isBoolean(id)) {
					ret += "($" + id + "==" + "true" + ")||";
				}
				else {
					ret += "($" + id + "==%" + option + ")||";
				}
			}
		}
		if (ret.isEmpty()) {
			return "";
		}
		return ret.substring(0, ret.length()-2);
	}
	
	private boolean hasRelation(String targetId, String triggerId) {
		List<String> targetOptions = this.getPropertyDef(targetId).getOptionIds();
		List<String> triggerOptions = this.getPropertyDef(triggerId).getOptionIds();
		targetOptions.add(targetId);
		triggerOptions.add(triggerId);
		
		boolean enabled = false;
		for (String triggerOption :triggerOptions) {
			for (String targetOption : targetOptions) {
				if (this.getData().getList(triggerOption).contains(targetOption)) {
					enabled = true;
				}
			}
		}
		return enabled;
	}
	
	public int getPriority(String id) {
		return this.getData().getPriority(id);
	}
	
	protected boolean isBoolean(String id) {
		return this.getPropertyDef(id).isBoolean();
	}

	protected boolean isNumeric(String id) {
		return this.getPropertyDef(id).isNumeric();
	}

	protected boolean isList(String id) {
		return this.getPropertyDef(id).isList();
	}
	
	protected abstract boolean isOptionId(String triggerId);

	protected abstract PropertyDef2 getPropertyDef(String id);

	protected abstract List<Integer> getDefinedPriorities();

	protected abstract String getMainId(String id);

	protected abstract DependencySpecHolder getDependencySpecHolder();

	protected abstract boolean isMainId(String id);
	
	protected abstract RestrictionData2 getData();
}

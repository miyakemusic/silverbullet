package jp.silverbullet.dependency2.design;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.silverbullet.JsonPersistent;
import jp.silverbullet.dependency2.DependencySpec;
import jp.silverbullet.dependency2.DependencySpecHolder;
import jp.silverbullet.dependency2.DependencySpecRebuilder;
import jp.silverbullet.dependency2.Expression;
import jp.silverbullet.property2.PropertyDef2;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.web.ui.PropertyGetter;

public abstract class RestrictionMatrix {
	abstract protected DependencySpecHolder getDependencySpecHolder() ;

	abstract protected void resetMask();

	public enum AxisType {
		Y,
		X
	}
	public List<String> xTitle = new ArrayList<>();
	public List<String> yTitle = new ArrayList<>();
	public List<String> yValueTitle = new ArrayList<>();
	
	public RestrictionMatrixElement[][] enableMatrix;
	public String[][] valueMatrix;
	private Set<String> triggers = new HashSet<>();
	private Set<String> targets = new HashSet<>();
	private RestrictionData2 spec = new RestrictionData2();
	private Set<RestrictionMatrixListener> listenres = new HashSet<>();
	private Map<String, String> idMap = new HashMap<>(); // key is Option
	
	
	public RestrictionMatrix() {
		initValue();
	}

	protected abstract RuntimeProperty getRuntimeProperty(String id, int index);

	protected abstract RuntimeProperty getRuntimeProperty(String id);

	public void save(String folder) {
		new JsonPersistent().saveJson(this.spec, folder + "/restriction.json");
	}
	
	private void save() {
		JsonPersistent json = new JsonPersistent();
		json.saveJson(this.spec, "restriction.json");
	}
	
	public void load(String folder) {
		JsonPersistent json = new JsonPersistent();
		this.spec = json.loadJson(RestrictionData2.class, folder + "/restriction.json");
		initValue();
	}
	
	public void initValue() {
		collectId();
		createMatrix();
	}
	
	private void createMatrix() {
		
		this.xTitle.clear();
		this.yTitle.clear();
		
		this.yValueTitle.clear();
		
		for (String trigger : triggers) {
			PropertyDef2 triggerProp = getPropertyDef(trigger);
			this.xTitle.add(triggerProp.getId());
			this.xTitle.addAll(triggerProp.getOptionIds());
		}
		
		for (String target :targets) {
			PropertyDef2 targetProp = getPropertyDef(target);
			this.yTitle.add(targetProp.getId());
			this.yTitle.addAll(targetProp.getOptionIds());
			this.yValueTitle.add(targetProp.getId());
		}
		
		this.enableMatrix = new RestrictionMatrixElement[yTitle.size()][xTitle.size()];
		this.valueMatrix = new String[yValueTitle.size()][xTitle.size()];
		
		for (int r = 0; r < this.yTitle.size(); r++) {
			String targetId = this.yTitle.get(r);
			for (int c = 0; c < this.xTitle.size(); c++) {
				String triggerId = this.xTitle.get(c);
				
				// Enable
				if (spec.getList(targetId).contains(triggerId)) {
					this.enableMatrix[r][c] = new RestrictionMatrixElement(true, spec.getCondition(triggerId, targetId));
				}
				else {
					this.enableMatrix[r][c] = new RestrictionMatrixElement();
				}
			}
		}
		
		calculateCondition(new ArrayList<String>(this.idMap.keySet()));
		
		for (int r = 0; r < yValueTitle.size(); r++) {
			String targetId = yValueTitle.get(r);
			for (int c = 0; c < this.xTitle.size(); c++) {
				String triggerId = this.xTitle.get(c);
		
				// Value
				this.valueMatrix[r][c] = spec.getValue(triggerId, targetId);
			}
		}
	}

	protected abstract PropertyDef2 getPropertyDef(String trigger);

	public void updateEnabled(int row, int col, boolean checked) {
		String option1 = this.yTitle.get(row);
		String option2 = this.xTitle.get(col);
		this.spec.set(option1, option2, checked);

		List<String> list = new ArrayList<String>(this.spec.getList(option1));
		if (list.size() <= 1) {
			return;
		}	
		
		calculateCondition(list);
		
		initValue();
		fireUpdateMatrix();
	}
	
	public void updateValue(int row, int col, String value) {
		String target = this.yTitle.get(row);
		String trigger = this.xTitle.get(col);
		this.spec.setValue(trigger, target, value);		
//		this.spec.setValue(target, this.getMainId(trigger), trigger + ":" + value);
		this.createMatrix();
	}

	private void calculateCondition(List<String> list) {
		for (int row = 0; row < list.size(); row++) {
			String option1 = list.get(row);
			
			String mainId1 = this.getMainId(option1);
			for (int col = 0; col < list.size(); col++) {
				String text = "";
				String option2 = list.get(col);
				String mainId2 = this.getMainId(option2);

				if (!this.spec.getList(option1).contains(option2)) {
					continue;
				}
				if (mainId1.equals(mainId2)) {
					continue;
				}
				for (int k = 0; k < list.size(); k++) {
					String option3 = list.get(k);

					if (k == col) {
						continue;
					}
					if (getMainId(option3).equals(mainId1) || getMainId(option3).equals(mainId2))  {
						continue;
					}
					text += "($" + getMainId(option3) + "==%" +  option3 + ")||";
				}
//				this.data2.setCondition(option1, option2, text.substring(0, text.length()-2));
			}	
		}
	}
	
	private void fireUpdateMatrix() {
		for (RestrictionMatrixListener listener : this.listenres) {
			listener.onMatrixChanged(this.enableMatrix);
		}
	}

	public void build() {
		resetMask();
//		this.collectId();
		DependencySpecHolder holder = getDependencySpecHolder();
		holder.clear();

		// enable
		for (int priority : this.getDefinedPriorities()) {
			List<String> idsSamePriority = this.getPriorities().get(priority);
			
			List<String> idsLowerPriority = getLowerPriorityIds(priority);
			
			for (String triggerId : idsSamePriority) {
				// Calculates same priority IDs
				for (String targetId : idsSamePriority) {
					if (!targetId.equals(triggerId)) {
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
		for (String targetId: this.spec.getValueTargetIds()) {
			for (String triggerId : this.spec.getValueTriggerId(targetId)) {
				String value = this.spec.getValue(triggerId, targetId);
				if (value.isEmpty()) {
					continue;
				}
				
				PropertyDef2 targetProp = this.getPropertyDef(targetId);
				PropertyDef2 triggerProp = this.getPropertyDef(this.getMainId(triggerId));
				
				if (targetProp.isNumeric()) {
					if (value.equals("<")) {
						this.getDependencySpecHolder().getSpec(targetId).addValue("$" + triggerId, "$" + targetId + "<$" + triggerId);
					}
					else if (value.equals(">")) {
						this.getDependencySpecHolder().getSpec(targetId).addValue("$" + triggerId, "$" + targetId + ">$" + triggerId);						
					}
					else {
						this.getDependencySpecHolder().getSpec(targetId).addValue(value, "$" + this.getMainId(triggerId) + "==%" + triggerId);
					}
				}
				else if (targetProp.isList()) {
					if (triggerProp.isList()) {
						if (this.isOptionId(triggerId)) {
							String value2 = value;
							String condition = "";
							if (value.contains("(")) {
								String[] tmp = value.split("()");
								value2 = value.split("(")[0];
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
//		for (String triggerId : this.spec.getValueTriggerIds()) {
//			for (String targetId: this.spec.getValueTargetId(triggerId)) {
//				PropertyDef2 targetProp = this.getPropertyDef(targetId);
//				PropertyDef2 triggerProp = this.getPropertyDef(this.getMainId(triggerId));
//				if (targetProp.isNumeric()&& triggerProp.isList()) {
//					String value = this.spec.getValue(triggerId, targetId);
//					if (!value.isEmpty()) {
//						this.getDependencySpecHolder().getSpec(this.getMainId(triggerId)).addValue(triggerId, "$" + targetId + "==" + value);
//					}
//				}
//			}
//		}
	}
	
	private boolean isOptionId(String optionId) {
		return this.idMap.keySet().contains(optionId);
	}

	private boolean isList(String id) {
		return this.getPropertyDef(id).isList();
	}

	private boolean isNumeric(String id) {
		return this.getPropertyDef(id).isNumeric();
	}
	
	private void calculateLowerPriorityIds(String triggerId, String targetId) {
		List<String> targetOptions = this.getPropertyDef(targetId).getOptionIds();
		List<String> triggerOptions = this.getPropertyDef(triggerId).getOptionIds();
		targetOptions.add(targetId);
		triggerOptions.add(triggerId);
				
		List<String> usedOptions = new ArrayList<String>(spec.getEnableRelation().keySet());
		
		for (String targetOption : targetOptions) {
			boolean optionEnabledTouched = false;
			boolean enabledTouched = false;
//			boolean valueTouched = false;
			for (String triggerOption : triggerOptions) {
				if (isMainId(triggerOption, triggerId) && this.hasRelation(targetId, triggerId)) {
					if (this.getPropertyDef(triggerId).isBoolean()) {
						if (this.spec.contains(targetOption, triggerOption)) {
				
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
					if (spec.contains(targetOption, triggerOption)) {
						String condition = createCondition(targetOption, triggerOption, usedOptions);
						
						String triggerExpression = "$" + triggerId + "==%" + triggerOption;
						if (isMainId(targetOption, targetId)) {
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
					
					if (this.spec.contains(targetOption2, triggerOption)) {
						enabledCondition += "($" + targetId + "!=%" + targetOption2+ ")&&";
					}
				}
				if (enabledCondition.length() > 0) {
					enabledCondition = enabledCondition.substring(0, enabledCondition.length()-2);
				}
				
				if (this.spec.contains(targetOption, triggerOption)) {
					this.getDependencySpecHolder().getSpec(targetId).addValue(targetOption, 
							"$" + triggerId + "==%" + triggerOption, enabledCondition);					
				}
			}
		}
	}

	private boolean isOption(String targetId, String targetOption) {
		return this.getPropertyDef(targetId).getOptionIds().contains(targetOption);
	}

	private boolean hasRelation(String targetId, String triggerId) {
		List<String> targetOptions = this.getPropertyDef(targetId).getOptionIds();
		List<String> triggerOptions = this.getPropertyDef(triggerId).getOptionIds();
		targetOptions.add(targetId);
		triggerOptions.add(triggerId);
		
		boolean enabled = false;
		for (String triggerOption :triggerOptions) {
			for (String targetOption : targetOptions) {
				if (this.spec.getList(triggerOption).contains(targetOption)) {
					enabled = true;
				}
			}
		}
		return enabled;
	}

	private List<String> getLowerPriorityIds(int priority) {
		Set<String> ret = new HashSet<>();
		for (int p : this.getDefinedPriorities()) {
			if (p < priority) {
				ret.addAll(this.getPriorities().get(p));
			}
		}
		return new ArrayList<String>(ret);
	}
	 
	private boolean isMainId(String optionId, String mainId) {
		return optionId.equals(mainId);
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
			
			if (!this.spec.contains(targetOption, option)) {
				continue;
			}
			if (id.equals(triggerMainId)/* || id.equals(targetMainId)*/) {
				continue;
			}
			if (this.getPriority(id) >= this.getPriority(triggerMainId)) {
				ret += "($" + id + "==%" + option + ")||";
			}
		}
		if (ret.isEmpty()) {
			return "";
		}
		return ret.substring(0, ret.length()-2);
	}

	public Set<String> getTriggers() {
		return this.triggers;
	}

	public Set<String> getTargets() {
		return this.targets;
	}

	public void switchTriggerTarget() {
		Set<String> tmp = this.targets;
		this.targets = this.triggers;
		this.triggers = tmp;
		this.initValue();
	}

	public void add(String id, AxisType type) {
		if (type.equals(AxisType.X)) {
			this.triggers.add(id);
		}
		else if (type.equals(AxisType.Y)) {
			this.targets.add(id);
		}
		this.initValue();
	}

	public void hide(String id, AxisType type) {
		if (type.equals(AxisType.X)) {
			this.triggers.remove(id);
		}
		else if (type.equals(AxisType.Y)) {
			this.targets.remove(id);
		}
		this.initValue();
	}

	private String getMainId(String option) {
		return idMap.get(option);
	}
	
	public void collectId() {
		this.idMap.clear();

		Set<String> options = new HashSet<>();
		options.addAll(spec.getEnableRelation().keySet());
		spec.getValues().values().forEach(a -> options.addAll(a.keySet()));
		
		for (String option : options) {
			for (PropertyDef2 prop : this.getAllPropertieDefs()) {
				if (prop.getOptionIds().contains(option) || prop.getId().equals(option)) {
					idMap.put(option, prop.getId());
				}
			}
		}		
		
	}

	protected abstract List<PropertyDef2> getAllPropertieDefs();

	private Set<String> getUsedIds() {
		Set<String> ret = new HashSet<String>(idMap.values());
		ret.addAll(spec.getValues().keySet());
//		spec.getValues().values().forEach(a -> ret.addAll(a.keySet()));
		return ret;
	}
	
	public void showAll() {
		this.spec.clean();
		this.targets.clear();
		this.triggers.clear();
		this.triggers.addAll(getUsedIds());
		this.targets.addAll(getUsedIds());
		
		this.initValue();
	}

	public int getPriority(String id) {
		return this.spec.getPriority(id);
	}
	
	public void setPriority(String id, int value) {
		this.spec.setPriority(id, value);
//		this.build();
	}

	public List<Integer> getDefinedPriorities() {
		List<Integer> ret = new ArrayList<>();
		
		for (String id : this.getUsedIds()) {
			int priority = Integer.valueOf(this.getPriority(id));
			if (!ret.contains(priority)) {
				ret.add(priority);
			}
		}
		Collections.sort(ret, Comparator.reverseOrder());
		
		return ret;
	}
	
	public Map<Integer, List<String>> getPriorities() {
		//List<KeyValue> ret = new ArrayList<>();
		Map<Integer, List<String>> ret = new HashMap<>();
		
		for (String id : this.getUsedIds()) {
			//ret.add(new KeyValue(id, String.valueOf(this.getPriority(id))));
			int priority = Integer.valueOf(this.getPriority(id));
			if (!ret.containsKey(priority)) {
				ret.put(priority, new ArrayList<String>());
			}
			ret.get(priority).add(id);
		}
		return ret;
//		Collections.sort(ret, new Comparator<KeyValue>() {
//			@Override
//			public int compare(KeyValue arg0, KeyValue arg1) {
//				return Integer.valueOf(arg1.getValue()) -  Integer.valueOf(arg0.getValue());
//			}
//		});
	}

	public void addListener(RestrictionMatrixListener restrictionMatrixListener) {
		this.listenres.add(restrictionMatrixListener);
	}
}

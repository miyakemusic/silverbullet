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
	public RestrictionMatrixElement[][] value;
	private Set<String> triggers = new HashSet<>();
	private Set<String> targets = new HashSet<>();
	private RestrictionData2 data2 = new RestrictionData2();
	private Set<RestrictionMatrixListener> listenres = new HashSet<>();
	private Map<String, String> idMap = new HashMap<>();
		
	public RestrictionMatrix() {
		initValue();
	}

	protected abstract RuntimeProperty getRuntimeProperty(String id, int index);

	protected abstract RuntimeProperty getRuntimeProperty(String id);

	public void save(String folder) {
		new JsonPersistent().saveJson(this.data2, folder + "/restriction.json");
	}
	
	private void save() {
		JsonPersistent json = new JsonPersistent();
		json.saveJson(this.data2, "restriction.json");
	}
	
	public void load(String folder) {
		JsonPersistent json = new JsonPersistent();
		this.data2 = json.loadJson(RestrictionData2.class, folder + "/restriction.json");
		initValue();
	}
	
	private void initValue() {
		collectId();
		this.value = createMatrix();
	}
	
	private RestrictionMatrixElement[][] createMatrix() {
		
		this.xTitle.clear();
		this.yTitle.clear();
		
		for (String trigger : triggers) {
			PropertyDef2 triggerProp = getPropertyDef(trigger);
			this.xTitle.add(triggerProp.getId());
			this.xTitle.addAll(triggerProp.getOptionIds());
		}
		
		for (String target :targets) {
			PropertyDef2 targetProp = getPropertyDef(target);
			this.yTitle.add(targetProp.getId());
			this.yTitle.addAll(targetProp.getOptionIds());
		}
		
		RestrictionMatrixElement[][] ret = new RestrictionMatrixElement[yTitle.size()][xTitle.size()];
		for (int r = 0; r < this.yTitle.size(); r++) {
			String targetId = this.yTitle.get(r);
			for (int c = 0; c < this.xTitle.size(); c++) {
				String triggerId = this.xTitle.get(c);
				if (data2.getList(targetId).contains(triggerId)) {
					ret[r][c] = new RestrictionMatrixElement(true, data2.getCondition(triggerId, targetId));
				}
				else {
					ret[r][c] = new RestrictionMatrixElement();
				}
			}
		}
		calculateCondition(new ArrayList<String>(this.idMap.keySet()));
		return ret;
	}

	protected abstract PropertyDef2 getPropertyDef(String trigger);

	public void updateEnabled(int row, int col, boolean checked) {
		String option1 = this.yTitle.get(row);
		String option2 = this.xTitle.get(col);
		this.data2.set(option1, option2, checked);

		List<String> list = new ArrayList<String>(this.data2.getList(option1));
		if (list.size() <= 1) {
			return;
		}	
		
		calculateCondition(list);
		
		initValue();
		fireUpdateMatrix();
	}

	private void calculateCondition(List<String> list) {
		for (int row = 0; row < list.size(); row++) {
			String option1 = list.get(row);
			
			String mainId1 = this.getMainId(option1);
			for (int col = 0; col < list.size(); col++) {
				String text = "";
				String option2 = list.get(col);
				String mainId2 = this.getMainId(option2);

				if (!this.data2.getList(option1).contains(option2)) {
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
			listener.onMatrixChanged(this.value);
		}
	}

	public void build() {	
		resetMask();

		DependencySpecHolder holder = getDependencySpecHolder();
//		holder.clear();
		
		Set<String> usedId = this.getUsedIds();
		// This is tentative code
		for (String trigger : usedId) {
			for (String target : usedId) {
				if (trigger.equals(target)) {
					continue;
				}
				//holder.getSpec(target).clear(DependencySpec.OptionEnable, trigger);
				holder.getSpec(target).clear();
			}		
		}
		
		for (String trigger : usedId) {
			List<String> triggerOptions = this.getPropertyDef(trigger).getOptionIds(); // option IDs
			triggerOptions.add(trigger); // main ID
			for (String target : usedId) {
				if (trigger.equals(target)) {
					continue;
				}

				List<String> targetOptions = this.getPropertyDef(target).getOptionIds(); // option IDs
				targetOptions.add(target); // main ID
				
				int priority =  this.getPriority(target) - this.getPriority(trigger);
				
				List<String> targetOptions2 = null;
				List<String> triggerOptions2 = null;
				String target2;
				String trigger2;
				if (priority > 0) {
					targetOptions2 = triggerOptions;
					triggerOptions2 = targetOptions;
					target2 = trigger;
					trigger2 = target;
				}
				else {
					targetOptions2 = targetOptions;
					triggerOptions2 = triggerOptions;
					target2 = target;
					trigger2 = trigger;					
				}
				boolean enabled = false;
				for (String targetOption2 : targetOptions2) {
					boolean optionEnabledTouched = false;
					boolean enabledTouched = false;
					for (String triggerOption2 : triggerOptions2) {
						if (data2.getList(targetOption2).contains(triggerOption2)) {
							String condition = createCondition(targetOption2, triggerOption2, new ArrayList<String>(data2.getAllData().keySet()));
							
							String triggerExpression = "$" + trigger2 + "==%" + triggerOption2;
							if (isMainId(targetOption2, target2)) {
								getDependencySpecHolder().getSpec(target2).addEnable(DependencySpec.True, triggerExpression, condition);
								enabledTouched = true;
							}
							else {
								getDependencySpecHolder().getSpec(target2).addOptionEnabled(targetOption2, DependencySpec.True, 
										triggerExpression, condition);
								optionEnabledTouched = true;
							}

							
							enabled |= true;
						}
					}
					if (optionEnabledTouched) {
						getDependencySpecHolder().getSpec(target2).addOptionEnabled(targetOption2, DependencySpec.False, 
								DependencySpec.Else);		
					}
					if (enabledTouched) {
						getDependencySpecHolder().getSpec(target2).addEnable(DependencySpec.False, DependencySpec.Else);
					}
				}
				if (enabled && (priority == 0)) {
					setBiDirectional(trigger2, target2);
				}
			}
		}
		save();
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
			
			if (!this.data2.contains(targetOption, option)) {
				continue;
			}
			if (id.equals(triggerMainId)/* || id.equals(targetMainId)*/) {
				continue;
			}
			if (this.getPriority(id) > this.getPriority(triggerMainId)) {
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

	public void alwaysTrue() {
		resetMask();
		
		for (String trigger : triggers) {
			for (String target : targets) {
				setBiDirectional(trigger, target);
			}
		}
		save();
	}

	private void setBiDirectional(String trigger, String target) {
		DependencySpecRebuilder rebuilder = new DependencySpecRebuilder(this.getDependencySpecHolder(), new PropertyGetter() {
			@Override
			public RuntimeProperty getProperty(String id) {
				return getRuntimeProperty(id);
			}

			@Override
			public RuntimeProperty getProperty(String id, int index) {
				return getRuntimeProperty(id, index);
			}
		});
		
		rebuilder.handleOneSpec(target);
		
		DependencySpecHolder holder = this.getDependencySpecHolder();
		holder.getSpec(target).clear(DependencySpec.OptionEnable, trigger);
		holder.getSpec(target).clear(DependencySpec.Value, trigger);
		List<Expression> newTargetSpecs = rebuilder.getNewHolder().getSpec(target).getExpression(DependencySpec.Value);
//		holder.getSpec(target).getExpression(DependencySpec.Value).addAll(newTargetSpecs);
		holder.getSpec(target).addSpecs(DependencySpec.Value, newTargetSpecs);
		
		holder.getSpec(trigger).clear(DependencySpec.Value, target);
		List<Expression> newTriggerSpecs = rebuilder.getNewHolder().getSpec(trigger).getExpression(DependencySpec.Value);
//		holder.getSpec(trigger).getExpression(DependencySpec.Value).addAll(newTriggerSpecs);
		holder.getSpec(trigger).addSpecs(DependencySpec.Value, newTriggerSpecs);
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
	
	private void collectId() {
		this.idMap.clear();
		for (String option : data2.getAllData().keySet()) {
			for (PropertyDef2 prop : this.getAllPropertieDefs()) {
				if (prop.getOptionIds().contains(option) || prop.getId().equals(option)) {
					idMap.put(option, prop.getId());
				}
			}
		}		
	}

	protected abstract List<PropertyDef2> getAllPropertieDefs();

	private Set<String> getUsedIds() {
		return new HashSet<String>(idMap.values());
	}
	
	public void showAll() {
		this.data2.clean();
		this.targets.clear();
		this.triggers.clear();
		this.triggers.addAll(getUsedIds());
		this.targets.addAll(getUsedIds());
		
		this.initValue();
	}

	public int getPriority(String id) {
		return this.data2.getPriority(id);
	}
	
	public void setPriority(String id, int value) {
		this.data2.setPriority(id, value);
		this.build();
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

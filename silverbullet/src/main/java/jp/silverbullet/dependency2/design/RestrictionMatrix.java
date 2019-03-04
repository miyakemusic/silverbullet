package jp.silverbullet.dependency2.design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.silverbullet.JsonPersistent;
import jp.silverbullet.StaticInstances;
import jp.silverbullet.dependency2.DependencySpec;
import jp.silverbullet.dependency2.DependencySpecHolder;
import jp.silverbullet.dependency2.DependencySpecRebuilder;
import jp.silverbullet.dependency2.Expression;
import jp.silverbullet.property2.PropertyDef2;
import jp.silverbullet.property2.PropertyHolder2;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.property2.RuntimePropertyStore;
import jp.silverbullet.web.KeyValue;
import jp.silverbullet.web.ui.PropertyGetter;

public class RestrictionMatrix {
	private PropertyHolder2 getPropertyHolder() {
		// Bad design!! don't use Singleton!! do DI later!!
		return StaticInstances.getInstance().getBuilderModel().getPropertiesHolder2();
	}
	
	private RuntimePropertyStore getRuntimePropertyStore() {
		// Bad design!! don't use Singleton!! do DI later!!
		return StaticInstances.getInstance().getBuilderModel().getRuntimePropertyStore();
	}
	
	private DependencySpecHolder getDependencySpecHolder() {
		// Bad design!! don't use Singleton!! do DI later!!
		return StaticInstances.getInstance().getBuilderModel().getDependencySpecHolder2();
	}

	private void resetMask() {
		// Bad design!! don't use Singleton!! do DI later!!
		StaticInstances.getInstance().getBuilderModel().getRuntimePropertyStore().resetMask();
	}

	public List<String> rowTitle = new ArrayList<>();
	public List<String> colTitle = new ArrayList<>();
	public RestrictionMatrixElement[][] value;
	private Set<String> triggers = new HashSet<>();
	private Set<String> targets = new HashSet<>();
	private RestrictionData2 data2 = new RestrictionData2();
	private Map<String, Integer> priority = new HashMap<>();
	
	private static RestrictionMatrix instance;
	
	public static RestrictionMatrix getInstance() {
		if (instance == null) {
			instance = new RestrictionMatrix();
		}
		return instance;
	}
	
	private RestrictionMatrix() {
		this.triggers.add("ID_DISTANCERANGE");
		this.targets.add("ID_PULSEWIDTH");
		this.load();
		initValue();
	}

	private void save() {
		JsonPersistent json = new JsonPersistent();
		json.saveJson(this.data2, "restriction.json");
	}
	
	private void load() {
		JsonPersistent json = new JsonPersistent();
		this.data2 = json.loadJson(RestrictionData2.class, "restriction.json");
	}
	
	private void initValue() {
		this.value = createMatrix();
	}
	
	private RestrictionMatrixElement[][] createMatrix() {
		PropertyHolder2 propHolder = getPropertyHolder();
		
		this.rowTitle.clear();
		this.colTitle.clear();
		
		for (String trigger : triggers) {
			PropertyDef2 triggerProp = propHolder.get(trigger);
			this.rowTitle.addAll(triggerProp.getOptionIds());
		}
		
		for (String target :targets) {
			PropertyDef2 targetProp = propHolder.get(target);
			this.colTitle.addAll(targetProp.getOptionIds());
		}
		
		RestrictionMatrixElement[][] ret = new RestrictionMatrixElement[colTitle.size()][rowTitle.size()];
		for (int r = 0; r < this.colTitle.size(); r++) {
			String targetId = this.colTitle.get(r);
			for (int c = 0; c < this.rowTitle.size(); c++) {
				String triggerId = this.rowTitle.get(c);
				if (data2.getList(targetId).contains(triggerId)) {
					ret[r][c] = new RestrictionMatrixElement(true, "");
				}
				else {
					ret[r][c] = new RestrictionMatrixElement();
				}
			}
		}
		return ret;
	}

	public void updateEnabled(int row, int col, boolean checked) {
		this.data2.set(this.colTitle.get(row), this.rowTitle.get(col), checked);
		initValue();
	}
	
	public void build() {		
		resetMask();

		DependencySpecHolder holder = getDependencySpecHolder();
		
		for (String target : this.targets) {
			holder.getSpec(target).clear(DependencySpec.OptionEnable, target);
		}
		for (String trigger : this.triggers) {
			holder.getSpec(trigger).clear(DependencySpec.OptionEnable, trigger);
		}
		
		for (String trigger : this.triggers) {
			List<String> triggerOptions = this.getPropertyHolder().get(trigger).getOptionIds();
			for (String target :this.targets) {
				List<String> targetOptions = this.getPropertyHolder().get(target).getOptionIds();
				
				int priority =  this.getPriority(target) - this.getPriority(trigger);
				
				
				if (priority < 0) {
					List<String> tmp = targetOptions;
					targetOptions = triggerOptions;
					triggerOptions = tmp;
				}
				for (String targetOption : targetOptions) {
					boolean touched = false;
					for (String triggerOption : triggerOptions) {
						if (data2.getList(targetOption).contains(triggerOption)) {
							getDependencySpecHolder().getSpec(target).addOptionEnabled(targetOption, DependencySpec.True, 
									"$" + trigger + "==%" + triggerOption);
							touched = true;
						}
					}
					if (touched) {
						getDependencySpecHolder().getSpec(target).addOptionEnabled(targetOption, DependencySpec.False, 
								DependencySpec.Else);		
					}
				}
				
//				if (priority > 0) {
//					for (String targetOption : targetOptions) {
//						boolean touched = false;
//						for (String triggerOption : triggerOptions) {
//							if (data2.getList(targetOption).contains(triggerOption)) {
//								getDependencySpecHolder().getSpec(target).addOptionEnabled(targetOption, DependencySpec.True, 
//										"$" + trigger + "==%" + triggerOption);
//								touched = true;
//							}
//						}
//						if (touched) {
//							getDependencySpecHolder().getSpec(target).addOptionEnabled(targetOption, DependencySpec.False, 
//									DependencySpec.Else);		
//						}
//					}
//				}
//				else if (priority < 0) {
//					for (String triggerOption : triggerOptions) {
//						boolean touched = false;
//						for (String targetOption : targetOptions) {
//							if (data2.getList(triggerOption).contains(targetOption)) {
//								getDependencySpecHolder().getSpec(trigger).addOptionEnabled(triggerOption, DependencySpec.True, 
//										"$" + target + "==%" + targetOption);
//								touched = true;
//							}
//						}
//						if (touched) {
//							getDependencySpecHolder().getSpec(trigger).addOptionEnabled(triggerOption, DependencySpec.False, 
//									DependencySpec.Else);		
//						}
//					}					
//				}
//				else {
//					
//				}
			}
		}
		save();
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
		
		DependencySpecHolder holder = getDependencySpecHolder();
		DependencySpecRebuilder rebuilder = new DependencySpecRebuilder(holder, new PropertyGetter() {
			@Override
			public RuntimeProperty getProperty(String id) {
				return getRuntimePropertyStore().get(id);
			}

			@Override
			public RuntimeProperty getProperty(String id, int index) {
				return getRuntimePropertyStore().get(id, index);
			}
		
		});
		
		for (String trigger : triggers) {
			for (String target : targets) {
				rebuilder.handleOneSpec(target);
				
				holder.getSpec(target).clear(DependencySpec.OptionEnable, trigger);
				holder.getSpec(target).clear(DependencySpec.Value, trigger);
				List<Expression> newTargetSpecs = rebuilder.getNewHolder().getSpec(target).getExpression(DependencySpec.Value);
				holder.getSpec(target).getExpression(DependencySpec.Value).addAll(newTargetSpecs);
				
				holder.getSpec(trigger).clear(DependencySpec.Value, target);
				List<Expression> newTriggerSpecs = rebuilder.getNewHolder().getSpec(trigger).getExpression(DependencySpec.Value);
				holder.getSpec(trigger).getExpression(DependencySpec.Value).addAll(newTriggerSpecs);
			}
		}
		save();
	}

	public void add(String id, String type) {
		if (type.equals("trigger")) {
			if (!this.triggers.contains(id)) {
				this.triggers.add(id);
			}
		}
		else if (type.equals("target")) {
			if (!this.targets.contains(id)) {
				this.targets.add(id);
			}
		}
		this.initValue();
	}

	public void hide(String id, String type) {
		if (type.equals("trigger")) {
			this.triggers.remove(id);
		}
		else if (type.equals("target")) {
			this.targets.remove(id);
		}
		this.initValue();
	}

	private Set<String> getUsedIds() {
		Set<String> ret = new HashSet<>();
		for (String option : data2.getAllData().keySet()) {
			for (PropertyDef2 prop : this.getPropertyHolder().getProperties()) {
				if (prop.getOptionIds().contains(option)) {
					ret.add(prop.getId());
					break;
				}
			}
		}		
		return ret;
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
		if (this.priority.containsKey(id)) {
			return this.priority.get(id);
		}
		else {
			return 0;
		}
	}
	
	public void setPriority(String id, int value) {
		this.priority.put(id, value);
	}

	public List<KeyValue> getPriorities() {
		List<KeyValue> ret = new ArrayList<>();
		for (String id : this.getUsedIds()) {
			ret.add(new KeyValue(id, String.valueOf(this.getPriority(id))));
		}
		return ret;
	}
}

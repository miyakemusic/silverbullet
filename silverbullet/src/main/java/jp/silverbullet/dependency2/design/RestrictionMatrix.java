package jp.silverbullet.dependency2.design;

import java.util.ArrayList;
import java.util.List;
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
	private List<String> triggers = new ArrayList<>();
	private List<String> targets = new ArrayList<>();
	private RestrictionData stored = new RestrictionData();
	private RestrictionData2 data2 = new RestrictionData2();
	
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
//		this.value[row][col].enabled = checked;
		
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
					else {
					//	getDependencySpecHolder().getSpec(target).addOptionEnabled(targetOption, DependencySpec.False, 
					//			"$" + trigger +"==" + "$" + trigger);				
					}
				}
			}
		}
		
		
		//////////
//		String currentMainTriggerId = getMainId(this.rowTitle.get(0));
//		boolean triggerIdChanged = false;
//		for (int r = 0; r < this.colTitle.size(); r++) {
//			String target = this.colTitle.get(r);
//			String targetMainId = getMainId(target);
//			boolean touched = false;
//			for (int c = 0; c < this.rowTitle.size(); c++) {
//				String trigger = this.rowTitle.get(c);
//				String triggerMainId = getMainId(trigger);
//				
//				if (!currentMainTriggerId.equals(triggerMainId)) {
//					currentMainTriggerId = triggerMainId;
//					triggerIdChanged = true;
//				}
//				if (this.value[r][c].enabled) {
//					touched = true;
//					getDependencySpecHolder().getSpec(targetMainId).addOptionEnabled(target, DependencySpec.True, 
//							"$" + triggerMainId + "==%" + trigger);
//				}
//				
//				if (triggerIdChanged) {
//					if (touched) {
//						getDependencySpecHolder().getSpec(targetMainId).addOptionEnabled(target, DependencySpec.False, 
//								DependencySpec.Else);		
//					}
//					else {
//						getDependencySpecHolder().getSpec(targetMainId).addOptionEnabled(target, DependencySpec.False, 
//								"$" + triggerMainId +"==" + "$" + triggerMainId);				
//					}
//					triggerIdChanged = false;
//				}
//			}
//
//		}
		save();
	}


	public void setCombination(String trigger, String target) {
//		this.stored.put(this.trigger + ";" + this.target, this.value);
//		if (this.stored.containsKey(this.target + ";" + this.trigger)) {
//			this.stored.remove(this.target + ";" + this.trigger);
//		}
//		this.trigger = trigger;
//		this.target = target;
		
		this.initValue();
	}

	public List<String> getTriggers() {
		return this.triggers;
	}

	public List<String> getTargets() {
		return this.targets;
	}

	public void switchTriggerTarget() {
	//	setCombination(this.target, this.trigger);
		List<String> tmp = this.targets;
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

	public void remove(String id, String type) {
		if (type.equals("trigger")) {
			this.triggers.remove(id);
		}
		else if (type.equals("target")) {
			this.targets.remove(id);
		}
		this.initValue();
	}

}

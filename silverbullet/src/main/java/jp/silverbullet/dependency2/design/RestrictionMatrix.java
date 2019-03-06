package jp.silverbullet.dependency2.design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.util.concurrent.ListeningExecutorService;

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
	private DependencySpecHolder holder;
	private DependencySpecRebuilder rebuilder;
	private Set<RestrictionMatrixListener> listenres = new HashSet<>();
	private Map<String, String> idMap = new HashMap<>();
	
	private static RestrictionMatrix instance;
	
	public static RestrictionMatrix getInstance() {
		if (instance == null) {
			instance = new RestrictionMatrix();
		}
		return instance;
	}
	
	private RestrictionMatrix() {
		
		holder = getDependencySpecHolder();
		rebuilder = new DependencySpecRebuilder(holder, new PropertyGetter() {
			@Override
			public RuntimeProperty getProperty(String id) {
				return getRuntimePropertyStore().get(id);
			}

			@Override
			public RuntimeProperty getProperty(String id, int index) {
				return getRuntimePropertyStore().get(id, index);
			}
		
		});
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
		this.collectId();
	}
	
	private void initValue() {
		collectId();
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
					ret[r][c] = new RestrictionMatrixElement(true, data2.getCondition(triggerId, targetId));
				}
				else {
					ret[r][c] = new RestrictionMatrixElement();
				}
			}
		}
		return ret;
	}

	public void updateEnabled(int row, int col, boolean checked) {
		String option1 = this.colTitle.get(row);
		String option2 = this.rowTitle.get(col);
		this.data2.set(option1, option2, checked);
		
		String id1 = getMainId(option1);
		String id2 = getMainId(option2);
		
		List<String> list = new ArrayList<String>(this.data2.getList(option1));
		if (list.size() <= 1) {
			return;
		}
		
		
		for (int i = 0; i < list.size(); i++) {
			String text = "";
			for (int j = 0; j < list.size(); j++) {
				if (i == j) {
					continue;
				}
				text += list.get(j) + ";";
			}
			this.data2.setCondition(option1, list.get(i), text);
		}
		
		initValue();
		fireUpdateMatrix();
	}
	
	private void fireUpdateMatrix() {
		for (RestrictionMatrixListener listener : this.listenres) {
			listener.onMatrixChanged(this.value);
		}
	}

	public void build() {	
		resetMask();

		DependencySpecHolder holder = getDependencySpecHolder();
		
		Set<String> usedId = this.getUsedIds();
		// This is tentative code
		for (String trigger : usedId) {
			for (String target : usedId) {
				if (trigger.equals(target)) {
					continue;
				}
				holder.getSpec(target).clear(DependencySpec.OptionEnable, trigger);
			}		
		}
		
		for (String trigger : usedId) {
			List<String> triggerOptions = this.getPropertyHolder().get(trigger).getOptionIds();
			for (String target : usedId) {
				if (trigger.contains(target)) {
					continue;
				}

				List<String> targetOptions = this.getPropertyHolder().get(target).getOptionIds();
				
				int priority =  this.getPriority(target) - this.getPriority(trigger);
				
				if (priority > 0) {
					List<String> tmp = targetOptions;
					targetOptions = triggerOptions;
					triggerOptions = tmp;
					
					String tmp2 = target;
					target = trigger;
					trigger = tmp2;
				}
				//holder.getSpec(target).clear(DependencySpec.OptionEnable, target);
				
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
				if (priority == 0) {
					//holder.getSpec(trigger).clear(DependencySpec.OptionEnable, trigger);
					setBiDirectional(trigger, target);
				}
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
		
		for (String trigger : triggers) {
			for (String target : targets) {
				setBiDirectional(trigger, target);
			}
		}
		save();
	}

	private void setBiDirectional(String trigger, String target) {
		rebuilder.handleOneSpec(target);
		
		holder.getSpec(target).clear(DependencySpec.OptionEnable, trigger);
		holder.getSpec(target).clear(DependencySpec.Value, trigger);
		List<Expression> newTargetSpecs = rebuilder.getNewHolder().getSpec(target).getExpression(DependencySpec.Value);
		holder.getSpec(target).getExpression(DependencySpec.Value).addAll(newTargetSpecs);
		
		holder.getSpec(trigger).clear(DependencySpec.Value, target);
		List<Expression> newTriggerSpecs = rebuilder.getNewHolder().getSpec(trigger).getExpression(DependencySpec.Value);
		holder.getSpec(trigger).getExpression(DependencySpec.Value).addAll(newTriggerSpecs);
	}

	public void add(String id, String type) {
		if (type.equals("trigger")) {
			this.triggers.add(id);
		}
		else if (type.equals("target")) {
			this.targets.add(id);
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

	private String getMainId(String option) {
		return idMap.get(option);
	}
	
	private void collectId() {
		this.idMap.clear();
		for (String option : data2.getAllData().keySet()) {
			for (PropertyDef2 prop : this.getPropertyHolder().getProperties()) {
				if (prop.getOptionIds().contains(option)) {
					idMap.put(option, prop.getId());
				}
			}
		}		
	}

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
	}

	public List<KeyValue> getPriorities() {
		List<KeyValue> ret = new ArrayList<>();
		for (String id : this.getUsedIds()) {
			ret.add(new KeyValue(id, String.valueOf(this.getPriority(id))));
		}
		return ret;
	}

	public void addListener(RestrictionMatrixListener restrictionMatrixListener) {
		this.listenres.add(restrictionMatrixListener);
	}
}

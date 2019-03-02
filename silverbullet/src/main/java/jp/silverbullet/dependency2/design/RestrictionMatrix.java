package jp.silverbullet.dependency2.design;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.silverbullet.JsonPersistent;
import jp.silverbullet.StaticInstances;
import jp.silverbullet.dependency2.DependencySpec;
import jp.silverbullet.dependency2.DependencySpecHolder;
import jp.silverbullet.dependency2.DependencySpecRebuilder;
import jp.silverbullet.dependency2.Expression;
import jp.silverbullet.property2.PropertyDef2;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.web.ui.PropertyGetter;

public class RestrictionMatrix {

	public List<String> rowTitle;
	public List<String> colTitle;
	public RestrictionMatrixElement[][] value;
	private String trigger;
	private String target;
	private RestrictionData stored = new RestrictionData();
	
	private static RestrictionMatrix instance;
	
	public static RestrictionMatrix getInstance() {
		if (instance == null) {
			instance = new RestrictionMatrix();
		}
		return instance;
	}
	
	private RestrictionMatrix() {
		this.trigger = "ID_DISTANCERANGE";
		this.target = "ID_PULSEWIDTH";
		this.load();
		initValue();
	}

	private void save() {
		JsonPersistent json = new JsonPersistent();
		json.saveJson(stored, "restriction.json");
	}
	
	private void load() {
		JsonPersistent json = new JsonPersistent();
		this.stored = json.loadJson(RestrictionData.class, "restriction.json");
	}
	
	private void initValue() {
		// Bad design!! don't use Singleton!! do DI later!!
		PropertyDef2 triggerProp = StaticInstances.getInstance().getBuilderModel().getPropertiesHolder2().get(trigger);
		
		// Bad design!! don't use Singleton!! do DI later!!
		PropertyDef2 targetProp = StaticInstances.getInstance().getBuilderModel().getPropertiesHolder2().get(target);
		
		this.rowTitle = triggerProp.getOptionIds();
		this.colTitle = targetProp.getOptionIds();
		
		if (this.stored.get(this.trigger + ";" + this.target) != null) {
			this.value = this.stored.get(this.trigger + ";" + this.target);
		}
		if (this.stored.get(this.target + ";" + this.trigger) != null) {
			RestrictionMatrixElement[][] original = this.stored.get(this.target + ";" + this.trigger);
			this.value = new RestrictionMatrixElement[colTitle.size()][rowTitle.size()];
			for (int r = 0; r < original.length; r++) {
				for (int c = 0; c < original[0].length; c++) {
					this.value[c][r] = original[r][c];
				}
			}			
		}
		else if (this.stored.get(this.trigger + ";" + this.target) == null) {
			this.value = new RestrictionMatrixElement[colTitle.size()][rowTitle.size()];
			for (int r = 0; r < this.colTitle.size(); r++) {
				for (int c = 0; c < this.rowTitle.size(); c++) {
					this.value[r][c] = new RestrictionMatrixElement();
				}
			}
		}


	}

	public void updateEnabled(int row, int col, boolean checked) {
		this.value[row][col].enabled = checked;
	}
	
	public void build() {		
		resetMask();
		// Bad design!! don't use Singleton!! do DI later!!
		DependencySpecHolder holder = getDependencySpecHolder();
		
		holder.getSpec(trigger).clear(DependencySpec.OptionEnable, target);
		holder.getSpec(target).clear(DependencySpec.OptionEnable, trigger);
		
		for (int r = 0; r < this.colTitle.size(); r++) {
			String option1 = this.colTitle.get(r);
			boolean touched = false;
			for (int c = 0; c < this.rowTitle.size(); c++) {
				if (this.value[r][c].enabled) {
					touched = true;
					
					String option2 = this.rowTitle.get(c);

					getDependencySpecHolder().getSpec(target).addOptionEnabled(option1, DependencySpec.True, 
							"$" + trigger + "==%" + option2);
				}
			}
			if (touched) {
				// Bad design!! don't use Singleton!! do DI later!!
				getDependencySpecHolder().getSpec(target).addOptionEnabled(option1, DependencySpec.False, 
						DependencySpec.Else);		
			}
			else {
				
				getDependencySpecHolder().getSpec(target).addOptionEnabled(option1, DependencySpec.False, 
						"$" + trigger +"==" + "$" + trigger);				
			}
		}
		save();
	}

	private DependencySpecHolder getDependencySpecHolder() {
		// Bad design!! don't use Singleton!! do DI later!!
		DependencySpecHolder holder = StaticInstances.getInstance().getBuilderModel().getDependencySpecHolder2();
		return holder;
	}

	private void resetMask() {
		// Bad design!! don't use Singleton!! do DI later!!
		StaticInstances.getInstance().getBuilderModel().getRuntimePropertyStore().resetMask();
	}

	public void setCombination(String trigger, String target) {
		this.stored.put(this.trigger + ";" + this.target, this.value);
		if (this.stored.containsKey(this.target + ";" + this.trigger)) {
			this.stored.remove(this.target + ";" + this.trigger);
		}
		this.trigger = trigger;
		this.target = target;
		
		this.initValue();
	}

	public String getTrigger() {
		return this.trigger;
	}

	public String getTarget() {
		return this.target;
	}

	public void switchTriggerTarget() {
		setCombination(this.target, this.trigger);
		this.initValue();
	}

	public void alwaysTrue() {
		resetMask();
		
		DependencySpecHolder holder = getDependencySpecHolder();
		DependencySpecRebuilder rebuilder = new DependencySpecRebuilder(holder, new PropertyGetter() {
			@Override
			public RuntimeProperty getProperty(String id) {
				return StaticInstances.getInstance().getBuilderModel().getRuntimePropertyStore().get(id);
			}

			@Override
			public RuntimeProperty getProperty(String id, int index) {
				return StaticInstances.getInstance().getBuilderModel().getRuntimePropertyStore().get(id, index);
			}
		
		});
		rebuilder.handleOneSpec(this.target);
		
		holder.getSpec(this.target).clear(DependencySpec.OptionEnable, this.trigger);
		holder.getSpec(this.target).clear(DependencySpec.Value, this.trigger);
		List<Expression> newTargetSpecs = rebuilder.getNewHolder().getSpec(this.target).getExpression(DependencySpec.Value);
		holder.getSpec(this.target).getExpression(DependencySpec.Value).addAll(newTargetSpecs);
		
		holder.getSpec(this.trigger).clear(DependencySpec.Value, this.target);
		List<Expression> newTriggerSpecs = rebuilder.getNewHolder().getSpec(this.trigger).getExpression(DependencySpec.Value);
		holder.getSpec(this.trigger).getExpression(DependencySpec.Value).addAll(newTriggerSpecs);

		
		//		holder.getSpec(this.target).addSpecs();
//		holder.addSpec();
//		holder.addSpec(rebuilder.getNewHolder().getSpec(this.trigger));
	//	StaticInstances.getInstance().getBuilderModel().
		save();
	}
}

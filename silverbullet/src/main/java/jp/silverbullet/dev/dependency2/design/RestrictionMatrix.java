package jp.silverbullet.dev.dependency2.design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.silverbullet.core.dependency2.DependencySpecHolder;
import jp.silverbullet.core.property2.PropertyDef2;
import jp.silverbullet.core.property2.RuntimeProperty;

public abstract class RestrictionMatrix {
//	public static final String ACTION_AT_DIRECT = "ActionAtDirect";
	public static final String ACTION = "Action";


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
//	public String[][] valueMatrix;
	public DependencyRelation[][] relationMatrix;
	private Set<String> triggers = new HashSet<>();
	private Set<String> targets = new HashSet<>();
	private RestrictionData2 spec = new RestrictionData2();
	private Set<RestrictionMatrixListener> listenres = new HashSet<>();
	private Map<String, String> idMap = new HashMap<>(); // key is Option
	
	
	public RestrictionMatrix() {
//		initValue();
	}

	protected abstract RuntimeProperty getRuntimeProperty(String id, int index);

	protected abstract RuntimeProperty getRuntimeProperty(String id);

	protected abstract String getMainId(String option);
	
	public void initValue() {
		collectId();
		createMatrix();
		createCandidates();
	}
	
	private void createCandidates() {
		for (String trigger : this.xTitle) {
			
			PropertyDef2 triggerProp = getPropertyDef(this.getMainId(trigger));
			if (triggerProp == null) {
				System.out.println("triggerProp is NULL:" + trigger);
			}
			boolean triggerIsMain = trigger.equals(triggerProp.getId());
			
			int c = this.xTitle.indexOf(trigger);
			for (String target : this.yValueTitle) {
				PropertyDef2 targetProp = getPropertyDef(this.getMainId(target));
				if (targetProp == null) {
					System.out.println("targetProp is NULL:" + trigger);
				}
				
				int r = this.yValueTitle.indexOf(target);
				DependencyRelation dr = this.relationMatrix[r][c];
				dr.candidates.clear();
				
				
				if (targetProp.isList()) {
					if (!triggerIsMain || triggerProp.isAction()) {
						dr.candidates.add("");
						dr.candidates.addAll(targetProp.getOptionIds());
					}
					else {
						if (triggerProp.isList()) {
							dr.candidates.add("");
							dr.candidates.add("=");
						}
						
					}
				}
				else if (targetProp.isAction()) {
					if (triggerProp.isList() && !triggerIsMain) {
						dr.candidates.add("");
						dr.candidates.add(ACTION);
//						dr.candidates.add(ACTION_AT_DIRECT);
					}
				}
				
			}
		}
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
		this.relationMatrix = new DependencyRelation[yValueTitle.size()][xTitle.size()];
		
		for (int r = 0; r < this.yTitle.size(); r++) {
			String targetId = this.yTitle.get(r);
			for (int c = 0; c < this.xTitle.size(); c++) {
				String triggerId = this.xTitle.get(c);
				
				// Enable
				if (spec.getList(targetId).contains(triggerId)) {
					this.enableMatrix[r][c] = new RestrictionMatrixElement(true, ""/*spec.getCondition(triggerId, targetId)*/);
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
				this.relationMatrix[r][c] = spec.getValue(triggerId, targetId);
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
		this.createMatrix();
		this.fireUpdateMatrix();
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

	public void setTriggers(Set<String> triggers2) {
		this.triggers = triggers2;
	}

	public void setTargets(Set<String> targets2) {
		this.targets = targets2;
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

//	private String getMainId(String option) {
//		String ret = idMap.get(option);
//		if (ret == null) {
//			
//		}
//		return ret;
//	}
	
	public void collectId() {
		this.idMap.clear();

		Set<String> options = new HashSet<>();
		options.addAll(spec.getEnableRelation().keySet());
		//spec.getRelations().values().forEach(a -> options.addAll(a.keySet()));
		for (String id : spec.getRelations().keySet()) {
			Map<String, DependencyRelation> d = spec.getRelations().get(id);
			options.add(id);
			options.addAll(d.keySet());
		}
		for (String option : options) {
			for (PropertyDef2 prop : this.getAllPropertieDefs()) {
				if (prop.getOptionIds().contains(option) || prop.getId().equals(option)) {
					idMap.put(option, prop.getId());
					
					if (prop.isList()) {
						for (String o : prop.getOptionIds()) {
							idMap.put(o, prop.getId());
						}
					}
				}
			}
		}		
		
	}

	protected abstract List<PropertyDef2> getAllPropertieDefs();

	private Set<String> getUsedIds() {
		Set<String> ret = new HashSet<String>(idMap.values());
		ret.addAll(spec.getRelations().keySet());
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

	public void addListener(RestrictionMatrixListener restrictionMatrixListener) {
		this.listenres.add(restrictionMatrixListener);
	}

	public void setData(RestrictionData2 data) {
		this.spec = data;
	}

	public void updateValue(String rowId, String colId, String value) {
		this.updateValue(yTitle.indexOf(rowId), xTitle.indexOf(colId), value);
	}
}

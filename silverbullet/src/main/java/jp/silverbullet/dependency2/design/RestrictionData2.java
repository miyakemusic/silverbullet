package jp.silverbullet.dependency2.design;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class RestrictionData2 {
	private Map<String, Set<String>> enableRelation = new HashMap<>();
	private Map<String, Integer> priority = new HashMap<>();
//	private Map<String, String> condition = new HashMap<>();
//	private Map<String, Boolean> blockPropagation = new HashMap<>();
//	@JsonIgnore
//	public Map<String, Map<String, String>> values = new HashMap<>();
	private Map<String, Map<String, DependencyRelation>> relations = new HashMap<>();
	

	public void setValueCondition(String trigger, String target, String condition) {
		this.setValue(trigger, target, this.getValue(trigger, target).relation, condition);
	}
	
	public void setValueBlockPropagation(String trigger, String target, Boolean enabled) {
		this.getValue(trigger, target).blockPropagation = enabled;
	}
	
	public synchronized void setValue(String trigger, String target, String value) {
		this.setValue(trigger, target, value, this.getValue(trigger, target).condition);
	}
	
	public synchronized void setValue(String trigger, String target, String value, String condition) {
/*		if (!this.relations.containsKey(target)) {
			this.relations.put(target, new HashMap<String, DependencyRelation>());
		}
		if (!this.relations.containsKey(trigger)) {
			this.relations.put(trigger, new HashMap<String, DependencyRelation>());
		}
		this.relations.get(target).put(trigger, new DependencyRelation(value, condition));
*/
		String prev = this.getValue(trigger, target).relation;
		
		DependencyRelation relation = this.getValue(trigger, target);
		relation.relation = value;
		relation.condition = condition;
				
		if (value.startsWith(">")) {
			//this.relations.get(trigger).put(target, new DependencyRelation(value.replace(">", "<")));
			this.getValue(target, trigger).relation = value.replace(">", "<");
		}
		else if (value.startsWith("<")) {
			//this.relations.get(trigger).put(target, new DependencyRelation(value.replace("<", ">")));
			this.getValue(target, trigger).relation = value.replace("<", ">");
		}
		else if (value.startsWith("=")) {
			//this.relations.get(trigger).put(target, new DependencyRelation(value));
			this.getValue(target, trigger).relation = value;
		}
		else if (value.isEmpty() && isSyncValue(prev)) {
			this.getValue(target, trigger).relation = value;
		}
	}
	
	private static final List<String> syncValues = Arrays.asList(">", "<", "=");
	private boolean isSyncValue(String v) {
		return syncValues.contains(v);
	}

	public synchronized DependencyRelation getValue(String triggerId, String targetId) {
		if (!this.relations.containsKey(targetId)) {
			this.relations.put(targetId, new LinkedHashMap<String, DependencyRelation>());
		}
		
		if (!this.relations.get(targetId).containsKey(triggerId)) {
			this.relations.get(targetId).put(triggerId, new DependencyRelation());
		}
		DependencyRelation ret = this.relations.get(targetId).get(triggerId);

		return ret;
	}
	
	public synchronized void set(String trigger, String target, boolean checked) {
		if (checked) {
			this.get(trigger).add(target);
			this.get(target).add(trigger);
		}
		else {
			remove(trigger, target);
			remove(target, trigger);
		}
		
	}

	private synchronized void remove(String trigger, String target) {
		this.get(trigger).remove(target);
		if (this.get(trigger).size() == 0) {
			this.enableRelation.remove(trigger);
		}
	}

	private Set<String> get(String id) {
		if (!this.enableRelation.containsKey(id)) {
			this.enableRelation.put(id, new HashSet<String>());
		}
		return this.enableRelation.get(id);
	}

	public Map<String, Set<String>> getEnableRelation() {
		return enableRelation;
	}
//
//	public Map<String, Map<String, String>> getValues() {
//		return values;
//	}

	public Map<String, Map<String, DependencyRelation>> getRelations() {
		return relations;
	}

	public Set<String> getList(String id) {
		if (this.enableRelation.containsKey(id)) {
			return this.enableRelation.get(id);
		}
		return new HashSet<String>();
	}

	public void clean() {
		Iterator<Entry<String, Set<String>>> it = this.enableRelation.entrySet().iterator();
		while(it.hasNext()) {
			if (it.next().getValue().size() == 0) {
				it.remove();
			}
		}
	}

	public Map<String, Integer> getPriority() {
		return priority;
	}
//
//	public void setCondition(String option1, String option2, String text) {
//		this.condition.put(option1 + ";" + option2, text);
//	}
//
//	public String getCondition(String option1, String option2) {
//		String key = option1 + ";" + option2;
//		if (this.condition.containsKey(key)) {
//			return this.condition.get(key);
//		}
//		else {
//			return this.condition.get(option2 + ";" + option1);
//		}
//	}
	
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

	public boolean contains(String option1, String option2) {
		Set<String> r = this.enableRelation.get(option1);
		if (r == null) {
			return false;
		}
		return r.contains(option2);
	}

	@JsonIgnore
	public Set<String> getValueTargetIds() {
		return this.relations.keySet();
	}

	public Set<String> getValueTriggerId(String targetId) {
		return this.relations.get(targetId).keySet();
	}

	@JsonIgnore
	public  Set<String> getValueTriggerIds() {
		Set<String> ret = new HashSet<>();
		this.relations.values().forEach(a -> ret.addAll(a.keySet()));
		return ret;
	}

	public Set<String> getValueTargetId(String triggerId) {
		Set<String> ret = new HashSet<>();
		for (String targetId : this.relations.keySet()) {
			for (String triggerOption : this.relations.get(targetId).keySet()) {
				if (triggerOption.contains(triggerId)) {
					ret.add(targetId);
				}
			}
		}
		return ret;
	}

	public Set<String> getUserdIds() {
		Set<String> ret = new HashSet<>();
		
		Map<String, Map<String, DependencyRelation>> values = this.getRelations();
		for (String id : values.keySet()) {
			ret.add(id);
			Map<String, DependencyRelation> id2VsValue = values.get(id);
			for (String id2 : id2VsValue.keySet()) {
				ret.add(id2);
			}
		}
		
		for (String id : this.enableRelation.keySet()) {
			ret.add(id);
			for (String id2 : this.enableRelation.get(id)) {
				ret.add(id2);
			}
		}
		return ret;
	}

	public boolean getBlockPropagation(String targetId, String triggerId) {
		return this.getValue(triggerId, targetId).blockPropagation;
	}


//	public void tmp() {
//		for (String id : this.values.keySet()) {
//			Map<String, String> v = this.values.get(id);
//			
//			Map<String, DependencyRelation> depRel = new HashMap<>();
//			this.relations.put(id, depRel);
//			for (String id2: v.keySet()) {
//				String r = v.get(id2);
//				String[] tmp = r.split("@");
//				if (tmp.length == 2) {
//					depRel.put(id2, new DependencyRelation(tmp[0], tmp[1]));
//				}
//				else {
//					depRel.put(id2, new DependencyRelation(tmp[0]));
//				}
//			}
//		}
//	}
	
}

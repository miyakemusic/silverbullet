package jp.silverbullet.dependency2.design;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class RestrictionData2 {
	private Map<String, Set<String>> enableRelation = new HashMap<>();
	private Map<String, Integer> priority = new HashMap<>();
	private Map<String, String> condition = new HashMap<>();
	private Map<String, Map<String, String>> values = new HashMap<>();
	
	public synchronized void setValue(String trigger, String target, String value) {
		if (!this.values.containsKey(target)) {
			this.values.put(target, new HashMap<String, String>());
		}
		if (!this.values.containsKey(trigger)) {
			this.values.put(trigger, new HashMap<String, String>());
		}
		this.values.get(target).put(trigger, value);
		
		if (value.equals(">")) {
			this.values.get(trigger).put(target, "<");
		}
		else if (value.equals("<")) {
			this.values.get(trigger).put(target, ">");
		}
		else if (value.equals("=")) {
			this.values.get(trigger).put(target, "=");
		}
	}
	
	public synchronized String getValue(String triggerId, String targetId) {
		if (!this.values.containsKey(targetId)) {
			return "";
		}
		String ret = this.values.get(targetId).get(triggerId);
		if (ret == null) {
			return "";
		}
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

	public Map<String, Map<String, String>> getValues() {
		return values;
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

	public void setCondition(String option1, String option2, String text) {
		this.condition.put(option1 + ";" + option2, text);
	}

	public String getCondition(String option1, String option2) {
		String key = option1 + ";" + option2;
		if (this.condition.containsKey(key)) {
			return this.condition.get(key);
		}
		else {
			return this.condition.get(option2 + ";" + option1);
		}
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

	public boolean contains(String option1, String option2) {
		Set<String> r = this.enableRelation.get(option1);
		if (r == null) {
			return false;
		}
		return r.contains(option2);
	}

	@JsonIgnore
	public Set<String> getValueTargetIds() {
		return this.values.keySet();
	}

	public Set<String> getValueTriggerId(String targetId) {
		return this.values.get(targetId).keySet();
	}

	@JsonIgnore
	public  Set<String> getValueTriggerIds() {
		Set<String> ret = new HashSet<>();
		this.values.values().forEach(a -> ret.addAll(a.keySet()));
		return ret;
	}

	public Set<String> getValueTargetId(String triggerId) {
		Set<String> ret = new HashSet<>();
		for (String targetId : this.values.keySet()) {
			for (String triggerOption : this.values.get(targetId).keySet()) {
				if (triggerOption.contains(triggerId)) {
					ret.add(targetId);
				}
			}
		}
		return ret;
	}

	public Set<String> getUserdIds() {
		Set<String> ret = new HashSet<>();
		
		Map<String, Map<String, String>> values = this.getValues();
		for (String id : values.keySet()) {
			ret.add(id);
			Map<String, String> id2VsValue = values.get(id);
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

//	public void addPriorityIfNotExists(String mainId) {
//		if (!this.priority.keySet().contains(mainId)) {
//			this.setPriority(mainId, 0);
//		}
//	}
	
}

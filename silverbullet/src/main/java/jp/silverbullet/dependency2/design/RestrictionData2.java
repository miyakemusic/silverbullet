package jp.silverbullet.dependency2.design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class RestrictionData2 {
	private Map<String, Set<String>> allData = new HashMap<>();
	private Map<String, Integer> priority = new HashMap<>();
	private Map<String, String> condition = new HashMap<>();
	
	
	public void set(String trigger, String target, boolean checked) {
		if (checked) {
			this.get(trigger).add(target);
			this.get(target).add(trigger);
		}
		else {
			remove(trigger, target);
			remove(target, trigger);
		}
		
	}

	private void remove(String trigger, String target) {
		this.get(trigger).remove(target);
		if (this.get(trigger).size() == 0) {
			this.allData.remove(trigger);
		}
	}

	private Set<String> get(String id) {
		if (!this.allData.containsKey(id)) {
			this.allData.put(id, new HashSet<String>());
		}
		return this.allData.get(id);
	}

	public Map<String, Set<String>> getAllData() {
		return allData;
	}

	public Set<String> getList(String id) {
		if (this.allData.containsKey(id)) {
			return this.allData.get(id);
		}
		return new HashSet<String>();
	}

	public void clean() {
		Iterator<Entry<String, Set<String>>> it = this.allData.entrySet().iterator();
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

	
}

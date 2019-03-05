package jp.silverbullet.dependency2.design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RestrictionData2 {
	private Map<String, List<String>> allData = new HashMap<>();
	
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

	private List<String> get(String id) {
		if (!this.allData.containsKey(id)) {
			this.allData.put(id, new ArrayList<String>());
		}
		return this.allData.get(id);
	}

	public Map<String, List<String>> getAllData() {
		return allData;
	}

	public List<String> getList(String id) {
		if (this.allData.containsKey(id)) {
			return this.allData.get(id);
		}
		return new ArrayList<String>();
	}

	public void clean() {
		Iterator<Entry<String, List<String>>> it = this.allData.entrySet().iterator();
		while(it.hasNext()) {
			if (it.next().getValue().size() == 0) {
				it.remove();
			}
		}
	}

	public void setCondition(String option1, String option2, String condition) {
		// TODO Auto-generated method stub
		
	}
}

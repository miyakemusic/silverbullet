package jp.silverbullet.dependency2.design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestrictionData2 {
	private Map<String, List<String>> allData = new HashMap<>();
	
	public void set(String trigger, String target, boolean checked) {
		if (checked) {
			this.get(trigger).add(target);
			this.get(target).add(trigger);
		}
		else {
			this.get(trigger).remove(target);
			this.get(target).remove(trigger);
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
}

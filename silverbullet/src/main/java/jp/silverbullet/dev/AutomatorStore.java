package jp.silverbullet.dev;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutomatorStore {
	private Map<String, String> data = new HashMap<>();

	public void put(String name, String script) {
		this.data.put(name, script);
	}

	public List<String> nameList() {
		return new ArrayList<String>(data.keySet());
	}
}

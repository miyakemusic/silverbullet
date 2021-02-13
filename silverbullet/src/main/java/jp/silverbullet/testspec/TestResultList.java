package jp.silverbullet.testspec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestResultList {
	public Map<String, List<String>> list = new HashMap<>();

	public void add(String portId, String testMethod) {
		if (!list.containsKey(portId)) {
			list.put(portId, new ArrayList<String>());
		}
		list.get(portId).add(testMethod);
	}

	public boolean done(String portid, String testMethod) {
		List<String> methods = this.list.get(portid);
		if (methods == null) {
			return false;
		}
		return methods.contains(testMethod);
	}
}

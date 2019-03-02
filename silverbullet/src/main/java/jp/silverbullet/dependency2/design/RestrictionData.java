package jp.silverbullet.dependency2.design;

import java.util.HashMap;
import java.util.Map;

public class RestrictionData {
	public Map<String, RestrictionMatrixElement[][]> stored = new HashMap<>();

	public RestrictionMatrixElement[][] get(String name) {
		return this.stored.get(name);
	}

	public void put(String name, RestrictionMatrixElement[][] value) {
		this.stored.put(name, value);
	}

	public boolean containsKey(String name) {
		return this.stored.containsKey(name);
	}

	public void remove(String name) {
		this.stored.remove(name);
	}
}

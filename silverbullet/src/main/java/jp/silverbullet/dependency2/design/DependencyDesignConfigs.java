package jp.silverbullet.dependency2.design;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DependencyDesignConfigs {
	
	public Map<String, DependencyDesignConfig> configs = new HashMap<>();

	public DependencyDesignConfig get(String name) {
		return this.configs.get(name);
	}

	public Collection<? extends String> keySet() {
		return this.configs.keySet();
	}

	public boolean containsKey(String name) {
		return this.configs.containsKey(name);
	}

	public void put(String name, DependencyDesignConfig dependencyDesignConfig) {
		this.configs.put(name, dependencyDesignConfig);
	}
}

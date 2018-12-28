package jp.silverbullet.property2;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class RuntimeProperties {
	private String version = "0.01";
	private Map<String, RuntimeProperty> runtimeProperties = new LinkedHashMap<>();

	public Map<String, RuntimeProperty> getRuntimeProperties() {
		return runtimeProperties;
	}

	public void setRuntimeProperties(Map<String, RuntimeProperty> runtimeProperties) {
		this.runtimeProperties = runtimeProperties;
	}

	public Set<String> keySet() {
		return this.runtimeProperties.keySet();
	}

	public void put(String id, RuntimeProperty runtimeProperty) {
		this.runtimeProperties.put(id, runtimeProperty);
	}

	public RuntimeProperty get(String id) {
		return this.runtimeProperties.get(id);
	}

	public Collection<? extends RuntimeProperty> values() {
		return this.runtimeProperties.values();
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void remove(String id, int index) {
		this.runtimeProperties.remove(RuntimeProperty.createIdText(id, index));
	}
	
	
}

package jp.silverbullet.core;

import java.util.LinkedHashMap;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class IdValues {
	private LinkedHashMap<String, String> allData = new LinkedHashMap<>();
	
	public void add(String id, String value) {
		this.allData.put(id, value);
	}
	
	public LinkedHashMap<String, String> getAllData() {
		return allData;
	}

	public void setAllData(LinkedHashMap<String, String> allData) {
		this.allData = allData;
	}

	public Set<String> allIds() {
		return this.allData.keySet();
	}
	
	public String value(String id) {
		return this.allData.get(id);
	}
}

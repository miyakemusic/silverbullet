package jp.silverbullet.property2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PropertyType {
	private LinkedHashMap<String, StringArray> definitions = new LinkedHashMap<>();

	public Map<String, StringArray> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(LinkedHashMap<String, StringArray> definitions) {
		this.definitions = definitions;
	}

	public List<String> getAllArguments() {
		Set<String> set = new LinkedHashSet<String>();
		for (StringArray arr : this.definitions.values()) {
			set.addAll(arr.list);
		}
		return new ArrayList<String>(set);
	}
	
	public List<String> getArguments(String type) {
		if (this.definitions.keySet().contains(type)) {
			return this.definitions.get(type).list;
		}
		else {
			return this.getAllArguments();
		}
	}

	public int getIndex(String type, String key) {
		return this.getArguments(type).indexOf(key);
	}

	public boolean containsType(String type) {
		return this.definitions.keySet().contains(type);
	}

	public void addDefinition(String type, List<String> list) {
		StringArray arr = new StringArray();
		arr.list.addAll(list);
		this.definitions.put(type, arr);
	}
}

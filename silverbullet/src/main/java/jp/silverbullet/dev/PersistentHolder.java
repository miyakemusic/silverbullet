package jp.silverbullet.dev;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import jp.silverbullet.core.JsonPersistent;

public class PersistentHolder {
	private static final String PERSISTENT_JSON = "persistent.json";
	//private List<String> ids = new ArrayList<>();
	private Map<String, LinkedHashSet<String>> ids = new HashMap<>();
	private Map<String, String> paths = new HashMap<>();
	
	public Map<String, LinkedHashSet<String>> getIds() {
		return ids;
	}
	public void setIds(Map<String, LinkedHashSet<String>> ids) {
		this.ids = ids;
	}
	
	public Map<String, String> getPaths() {
		return paths;
	}
	public void setPaths(Map<String, String> paths) {
		this.paths = paths;
	}
	public PersistentHolder() {

	}
	public void addTrigger(String id) {
		ids.put(id, new LinkedHashSet<String>());
		//this.ids.add(id);
	}

	public List<String> triggerList() {
		return new ArrayList<String>(this.ids.keySet());
	}
	public void load(String folder) {
		try {
			PersistentHolder obj = new JsonPersistent().loadJson(PersistentHolder.class, folder + "/" + PERSISTENT_JSON);
			this.ids = obj.ids;
			this.paths = obj.paths;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void save(String folder) {
		try {
			new JsonPersistent().saveJson(this, folder + "/" + PERSISTENT_JSON);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public List<String> remove(String id) {
		ids.remove(id);
		return triggerList();
	}
	public void addStoredIs(String triggerId, String storedId) {
		this.ids.get(triggerId).add(storedId);
	}
	public List<String> storedId(String triggerId) {
		LinkedHashSet<String> idSet = this.ids.get(triggerId);
		if (idSet == null) {
			return new ArrayList<String>();
		}
		List<String> ret = new ArrayList<String>(idSet);
//		if (ret == null) {
//			ret = new ArrayList<String>();
//		}
		return ret;
	}
	public void pathId(String triggerId, String pathId) {
		paths.put(triggerId, pathId);
	}
	public String path(String triggerId) {
		String ret = this.paths.get(triggerId);
		if (ret == null) {
			ret = "";
		}
		return ret;
	}

}

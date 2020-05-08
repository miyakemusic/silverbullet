package jp.silverbullet.dev;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.silverbullet.core.JsonPersistent;

public class PersistentHolder {
	private static final String PERSISTENT_JSON = "persistent.json";
	private List<String> ids = new ArrayList<>();
	
	public PersistentHolder() {

	}
	public void add(String id) {
		this.ids.add(id);
	}

	public List<String> getList() {
		return this.ids;
	}
	public void load(String folder) {
		try {
			PersistentHolder obj = new JsonPersistent().loadJson(PersistentHolder.class, folder + "/" + PERSISTENT_JSON);
			this.ids = obj.ids;
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		return ids;
	}

}

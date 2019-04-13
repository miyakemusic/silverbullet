package jp.silverbullet;

import java.util.HashMap;
import java.util.Map;

public class BlobStore {
	private Map<String, Object> data = new HashMap<>();
	
	public String put(String id, Object object) {
		this.data.put(id, object);
		return id;
	}

	public Object get(String id) {
		return this.data.get(id);
	}
}

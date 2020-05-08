package jp.silverbullet.web;

import java.util.List;
import java.util.Map;

import jp.silverbullet.core.dependency2.ChangedItemValue;

public class ChangesJson {
	private Map<String, List<ChangedItemValue>> changes;
	
	public ChangesJson() {}
	
	public ChangesJson(Map<String, List<ChangedItemValue>> changes) {
		this.changes = changes;
	}

	public Map<String, List<ChangedItemValue>> getChanges() {
		return changes;
	}

	public void setChanges(Map<String, List<ChangedItemValue>> changes) {
		this.changes = changes;
	}

}

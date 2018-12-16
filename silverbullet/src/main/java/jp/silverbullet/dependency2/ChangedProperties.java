package jp.silverbullet.dependency2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ChangedProperties implements CachedPropertyStoreListener {
	private Set<Id> ids = new HashSet<>();
	private List<Id> startIds;
	
	public ChangedProperties(List<Id> startIds) {
		this.startIds = startIds;
	}

	@Override
	public void onChanged(Id id, ChangedItemValue changedItemValue2) {
		if (!startIds.contains(id)) {
			ids.add(id);
		}
	}

	public List<Id> getIds() {
		return new ArrayList<Id>(ids);
	}

	public void clear() {
		this.ids.clear();
	}

}

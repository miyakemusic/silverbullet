package jp.silverbullet.dependency2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.silverbullet.dependency.CachedPropertyStoreListener;
import jp.silverbullet.dependency.ChangedItemValue;

public class ChangedProperties implements CachedPropertyStoreListener {
	private Set<String> ids = new HashSet<>();
	private List<String> startIds;
	
	public ChangedProperties(List<String> startIds) {
		this.startIds = startIds;
	}

	@Override
	public void onChanged(String id, ChangedItemValue changedItemValue2) {
		if (!startIds.contains(id)) {
			ids.add(id);
		}
	}

	public List<String> getIds() {
		return new ArrayList<String>(ids);
	}

	public void clear() {
		this.ids.clear();
	}

}

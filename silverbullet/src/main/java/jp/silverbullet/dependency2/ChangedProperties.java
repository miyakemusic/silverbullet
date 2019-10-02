package jp.silverbullet.dependency2;

import java.util.HashSet;
import java.util.Set;


public class ChangedProperties implements CachedPropertyStoreListener {
	private Set<Id> ids = new HashSet<>();
	private Set<Id> startIds;
	
	public ChangedProperties(Set<Id> startIds) {
		this.startIds = startIds;
	}

	@Override
	public void onChanged(Id id, ChangedItemValue changedItemValue2) {
		if (!changedItemValue2.getElement().equals(DependencySpec.Value)) {
			return;
		}
		if (!contains(startIds, id) && !contains(ids, id)) {
			ids.add(id);
		}
	}

	private boolean contains(Set<Id> startIds2, Id id) {
		for (Id idd : startIds2) {
			if (idd.toString().equals(id.toString())) {
				return true;
			}
		}
		return false;
	}

	public Set<Id> getIds() {
		return ids;
		//return new ArrayList<Id>(ids);
	}

//	public void clear() {
//		this.ids.clear();
//	}

}

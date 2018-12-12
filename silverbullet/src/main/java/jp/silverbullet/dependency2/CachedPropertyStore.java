package jp.silverbullet.dependency2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.silverbullet.SvProperty;
import jp.silverbullet.SvPropertyListener;
import jp.silverbullet.dependency.ChangedItemValue;
import jp.silverbullet.dependency.DepPropertyStore;
import jp.silverbullet.dependency.DependencyTargetElement;

public class CachedPropertyStore implements DepPropertyStore {
	private Map<String, SvProperty> cached = new HashMap<>();
	private DepPropertyStore original;
	private List<String> debugLog = new ArrayList<>();
	private Map<String, List<ChangedItemValue>> changedHistory = new LinkedHashMap<>();
	
	private SvPropertyListener listener = new SvPropertyListener() {
		@Override
		public void onValueChanged(String id, int index, String value) {
			appendChange(new Id(id, index), new ChangedItemValue(DependencyTargetElement.Value, value));
		}

		@Override
		public void onEnableChanged(String id, int index, boolean b) {
			appendChange(new Id(id, index), new ChangedItemValue(DependencyTargetElement.Enabled, String.valueOf(b)));
		}

		@Override
		public void onFlagChanged(String id, int index, Flag flag) {
			if (flag.equals(Flag.MAX)) {
				appendChange(new Id(id, index), new ChangedItemValue(DependencyTargetElement.Max, flag.toString()));
			}
			else if (flag.equals(Flag.MIN)) {
				appendChange(new Id(id, index), new ChangedItemValue(DependencyTargetElement.Min, flag.toString()));
			}
		}

		@Override
		public void onVisibleChanged(String id, int index, Boolean b) {
			appendChange(new Id(id, index), new ChangedItemValue(DependencyTargetElement.Visible, String.valueOf(b)));
		}

		@Override
		public void onListMaskChanged(String id, int index, String value) {
			appendChange(new Id(id, index), new ChangedItemValue(DependencyTargetElement.ListItemEnabled, value));
		}

		@Override
		public void onTitleChanged(String id, int index, String title) {
			// TODO Auto-generated method stub
			
		}
		
	};
	private Set<CachedPropertyStoreListener> cachedPropertyStoreListeners = new HashSet<>();
	
	public CachedPropertyStore(DepPropertyStore originalStore) {
		original = originalStore;
	}
	
	protected void appendChange(Id id, ChangedItemValue changedItemValue2) {
		getHistory(id.toString()).add(changedItemValue2);
		this.debugLog.add(id + ":" + changedItemValue2.toString());
		
		for (CachedPropertyStoreListener listener : cachedPropertyStoreListeners) {
			listener.onChanged(id, changedItemValue2);
		}
	}

	public List<String> getDebugLog() {
		return debugLog;
	}

	protected List<ChangedItemValue> getHistory(String id) {
		if (!this.changedHistory.containsKey(id)) {
			this.changedHistory.put(id, new ArrayList<ChangedItemValue>());
		}
		return this.changedHistory.get(id);
	}
	
	@Override
	public SvProperty getProperty(String id) {
		if (!cached.containsKey(id)) {
			SvProperty property = original.getProperty(id).clone();
			cached.put(id, property);
			property.addListener(listener);
		}
		return cached.get(id);
	}

	@Override
	public void add(SvProperty property) {
		this.original.add(property);
	}
	
	public void commit() {		
		for (String id : this.cached.keySet()) {
			if (!this.changedHistory.containsKey(id)) {
				continue;
			}
			for (ChangedItemValue item : this.getHistory(id)) {
				SvProperty propertyOriginal = this.original.getProperty(id);
				SvProperty tmpChangedProperty = this.cached.get(id);
				if (item.getElement().equals(DependencyTargetElement.Max)) {
					propertyOriginal.setMax(tmpChangedProperty.getMax());
				}
				else if (item.getElement().equals(DependencyTargetElement.Min)) {
					propertyOriginal.setMin(tmpChangedProperty.getMin());
				}
				else if (item.getElement().equals(DependencyTargetElement.Value)) {
					propertyOriginal.setCurrentValue(tmpChangedProperty.getCurrentValue());
				}
				else if (item.getElement().equals(DependencyTargetElement.Enabled)) {
					propertyOriginal.setEnabled(tmpChangedProperty.isEnabled());
				}
				else if (item.getElement().equals(DependencyTargetElement.Visible)) {
					propertyOriginal.setVisible(tmpChangedProperty.isVisible());
				}
				else if (item.getElement().equals(DependencyTargetElement.ListItemEnabled)) {
					propertyOriginal.setListMask(tmpChangedProperty.getListMask());
				}
			}
		}
	}

	public Map<String, List<ChangedItemValue>> getChangedHistory() {
		return changedHistory;
	}

	public String getMessage(String id) {
		String ret = "";
		for (String key : this.changedHistory.keySet()) {
			if (!key.equals(id)) {
				List<ChangedItemValue> item = this.changedHistory.get(key);
				ret += this.cached.get(key).getTitle()  + " ("+  key+ ")" +  ":\n";
				for (ChangedItemValue v : item) {
					ret += " " + v.getElement() + " -> " + v.getValue() + "\n";
				}
			}
		}
		return ret;
	}

	public List<String> getChangedIds() {
		return new ArrayList<String>(this.changedHistory.keySet());
	}

	public List<ChangedItemValue> getChanged(String id) {
		return this.changedHistory.get(id);
	}

	public void clearHistory() {
		this.changedHistory.clear();
	}

	private void addValue(String id, String value) {
		List<ChangedItemValue> remove = new ArrayList<>();
		for (ChangedItemValue v : getHistory(id)) {
			if (v.getElement().equals(DependencyTargetElement.Value)) {
				remove.add(v);
			}
		}
		getHistory(id).removeAll(remove);
		getHistory(id).add(new ChangedItemValue(DependencyTargetElement.Value, value));
	}

	public void addCachedPropertyStoreListener(CachedPropertyStoreListener cachedPropertyStoreListener) {
		cachedPropertyStoreListeners.add(cachedPropertyStoreListener);
	}
	
	public void removeCachedPropertyStoreListener(CachedPropertyStoreListener cachedPropertyStoreListener) {
		cachedPropertyStoreListeners.remove(cachedPropertyStoreListener);
	}
}

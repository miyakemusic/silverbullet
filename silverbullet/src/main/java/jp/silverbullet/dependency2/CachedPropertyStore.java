package jp.silverbullet.dependency2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.silverbullet.SvPropertyListener;
import jp.silverbullet.dependency2.ChangedItemValue;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.property2.RuntimePropertyStore;
import jp.silverbullet.web.ui.PropertyGetter;

public class CachedPropertyStore implements PropertyGetter {
	private Map<String, RuntimeProperty> cached = new HashMap<>();
	private PropertyGetter  original;
	private List<String> debugLog = new ArrayList<>();
	private Map<String, List<ChangedItemValue>> changedHistory = new LinkedHashMap<>();
	
	private SvPropertyListener listener = new SvPropertyListener() {
		@Override
		public void onValueChange(String id, int index, String value) {
			appendChange(new Id(id, index), new ChangedItemValue(DependencySpec.Value, value));
		}

		@Override
		public void onEnableChange(String id, int index, boolean b) {
			appendChange(new Id(id, index), new ChangedItemValue(DependencySpec.Enable, String.valueOf(b)));
		}

		@Override
		public void onFlagChange(String id, int index, Flag flag) {
			if (flag.equals(Flag.MAX)) {
				appendChange(new Id(id, index), new ChangedItemValue(DependencySpec.Max, flag.toString()));
			}
			else if (flag.equals(Flag.MIN)) {
				appendChange(new Id(id, index), new ChangedItemValue(DependencySpec.Min, flag.toString()));
			}
			else if (flag.equals(Flag.SIZE)) {
				appendChange(new Id(id, index), new ChangedItemValue(DependencySpec.ArraySize, flag.toString()));
			}
			else if (flag.equals(Flag.UNIT)) {
				appendChange(new Id(id, index), new ChangedItemValue(DependencySpec.Unit, flag.toString()));
			}
		}

		@Override
		public void onListMaskChange(String id, int index, String optionId, boolean mask) {
			appendChange(new Id(id, index), new ChangedItemValue(DependencySpec.OptionEnable, optionId + "," + String.valueOf(mask)));
		}

		@Override
		public void onTitleChange(String id, int index, String title) {
			appendChange(new Id(id, index), new ChangedItemValue(DependencySpec.Title, title));

		}
		
	};
	private Set<CachedPropertyStoreListener> cachedPropertyStoreListeners = new HashSet<>();
	
	public CachedPropertyStore(PropertyGetter  originalStore) {
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
	public RuntimeProperty getProperty(String id) {
		if (!id.contains(RuntimeProperty.INDEXSIGN)) {
			id = RuntimeProperty.createIdText(id, 0);
		}
		if (!cached.containsKey(id)) {
			RuntimeProperty property = original.getProperty(id).clone();
			cached.put(id, property);
			property.addListener(listener);
		}
		return cached.get(id);
	}

	@Override
	public RuntimeProperty getProperty(String id, int index) {
		id = RuntimeProperty.createIdText(id, index);
		if (!cached.containsKey(id)) {
			RuntimeProperty property = original.getProperty(id).clone();
			cached.put(id, property);
			property.addListener(listener);
		}
		return cached.get(id);
	}
//	@Override
//	public void add(RuntimeProperty property) {
	//	this.original.add(property);
//	}
	
	public void commit() {		
		for (String id : this.cached.keySet()) {
			if (!this.changedHistory.containsKey(id)) {
				continue;
			}
			for (ChangedItemValue item : this.getHistory(id)) {
				RuntimeProperty propertyOriginal = this.original.getProperty(id);
				RuntimeProperty tmpChangedProperty = this.cached.get(id);
				if (item.getElement().equals(DependencySpec.Max)) {
					propertyOriginal.setMax(tmpChangedProperty.getMax());
				}
				else if (item.getElement().equals(DependencySpec.Min)) {
					propertyOriginal.setMin(tmpChangedProperty.getMin());
				}
				else if (item.getElement().equals(DependencySpec.Value)) {
					propertyOriginal.setCurrentValue(tmpChangedProperty.getCurrentValue());
				}
				else if (item.getElement().equals(DependencySpec.Title)) {
					propertyOriginal.setTitle(tmpChangedProperty.getTitle());
				}
				else if (item.getElement().equals(DependencySpec.Enable)) {
					propertyOriginal.setEnabled(tmpChangedProperty.isEnabled());
				}

				else if (item.getElement().equals(DependencySpec.OptionEnable)) {
					propertyOriginal.setListMask(tmpChangedProperty.getListMask());
				}
				else if (item.getElement().equals(DependencySpec.ArraySize)) {
					propertyOriginal.setSize(tmpChangedProperty.getSize());
				}
				else if (item.getElement().equals(DependencySpec.Unit)) {
					propertyOriginal.setUnit(tmpChangedProperty.getUnit());
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

	public void addCachedPropertyStoreListener(CachedPropertyStoreListener cachedPropertyStoreListener) {
		cachedPropertyStoreListeners.add(cachedPropertyStoreListener);
	}
	
	public void removeCachedPropertyStoreListener(CachedPropertyStoreListener cachedPropertyStoreListener) {
		cachedPropertyStoreListeners.remove(cachedPropertyStoreListener);
	}
}

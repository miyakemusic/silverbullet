package jp.silverbullet.dependency2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.silverbullet.dependency2.ChangedItemValue;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.property2.RuntimePropertyListener;
import jp.silverbullet.web.ui.PropertyGetter;

public class CachedPropertyStore implements PropertyGetter {
	private Map<String, RuntimeProperty> cached = new HashMap<>();
	private PropertyGetter  original;
	private List<String> debugLog = new ArrayList<>();
	private Map<String, List<ChangedItemValue>> changedHistory = new LinkedHashMap<>();
	private Set<IdValue> confirmationMessage = new LinkedHashSet<>();
	private Set<CachedPropertyStoreListener> cachedPropertyStoreListeners = new HashSet<>();
	private boolean confirmation = true;
	
	private RuntimePropertyListener listener = new RuntimePropertyListener() {
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
	private boolean blockPropagation;
	
	public CachedPropertyStore(PropertyGetter  originalStore) {
		original = originalStore;
	}
	
	protected void appendChange(Id id, ChangedItemValue changedItemValue2) {
		changedItemValue2.blockPropagation = this.blockPropagation;
		getHistory(id.toString()).add(changedItemValue2);
		this.debugLog.add(id + ":" + changedItemValue2.toString());
		
		appendConfirmLog(id, changedItemValue2);
		
		for (CachedPropertyStoreListener listener : cachedPropertyStoreListeners) {
			listener.onChanged(id, changedItemValue2);
		}
	}

	private void appendConfirmLog(Id id, ChangedItemValue changedItemValue2) {
		if (this.confirmation && changedItemValue2.getElement().equals(DependencySpec.Value.toString())) {
			IdValue item = new IdValue(id.toString(), changedItemValue2.getValue());
			for (IdValue d : this.confirmationMessage) {
				if (item.equals(d)) {
					return;
				}
			}
			this.confirmationMessage.add(item);
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


	abstract class BlockPropagationOmitter {
		public BlockPropagationOmitter(Map<String, List<ChangedItemValue>> changed) {
			for (String id : changed.keySet()) {
				for (ChangedItemValue v : changed.get(id)) {
					if (!v.blockPropagation) {
						handle(id, v);	
					}
				}
			}			
		}

		protected abstract void handle(String id, ChangedItemValue v);
	}
	public Map<String, List<ChangedItemValue>> getChangedHistoryWithMaskingBlockPropagation() {
		Map<String, List<ChangedItemValue>> ret = new LinkedHashMap<>();
		new BlockPropagationOmitter(this.changedHistory) {
			@Override
			protected void handle(String id, ChangedItemValue v) {
				if (!ret.keySet().contains(id)) {
					ret.put(id, new ArrayList<ChangedItemValue>());
				}
				ret.get(id).add(v);
			}
		};		
		return ret;
	}
	
//	public String getMessage(String id) {
//		String ret = "";
//		for (String key : this.changedHistory.keySet()) {
//			if (!key.equals(id)) {
//				List<ChangedItemValue> item = this.changedHistory.get(key);
//				ret += this.cached.get(key).getTitle()  + " ("+  key+ ")" +  ":\n";
//				for (ChangedItemValue v : item) {
//					ret += " " + v.getElement() + " -> " + v.getValue() + "\n";
//				}
//			}
//		}
//		return ret;
//	}

	public List<String> getChangedIds() {
		return new ArrayList<String>(this.changedHistory.keySet());
	}

	public List<String> getChangedIdsWithMaskingBlockPropagation() {
		List<String> ret = new ArrayList<>();
		new BlockPropagationOmitter(this.changedHistory) {
			@Override
			protected void handle(String id, ChangedItemValue v) {
				ret.add(id);
			}
		};
		return ret;
	}
	
//	public List<ChangedItemValue> getChanged(String id) {
//		return this.changedHistory.get(id);
//	}
//
//	public void clearHistory() {
//		this.changedHistory.clear();
//		this.confirmationMessage.clear();
//	}

	public void addCachedPropertyStoreListener(CachedPropertyStoreListener cachedPropertyStoreListener) {
		cachedPropertyStoreListeners.add(cachedPropertyStoreListener);
	}
	
	public void removeCachedPropertyStoreListener(CachedPropertyStoreListener cachedPropertyStoreListener) {
		cachedPropertyStoreListeners.remove(cachedPropertyStoreListener);
	}

	public void setConfirmation(boolean b) {
		this.confirmation = b;
	}

	public Set<IdValue> getConfirmationMessage() {
		return confirmationMessage;
	}

	public void clearDebugLog() {
		this.debugLog.clear();
	}

	public void setBlockPropagation(boolean blockPropagation) {
		this.blockPropagation = blockPropagation;
	}


}

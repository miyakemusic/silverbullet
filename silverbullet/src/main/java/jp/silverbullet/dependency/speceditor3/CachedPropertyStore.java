package jp.silverbullet.dependency.speceditor3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import jp.silverbullet.SvProperty;
import jp.silverbullet.SvPropertyListener;

public class CachedPropertyStore implements DepPropertyStore {
	private Map<String, SvProperty> cached = new HashMap<>();
	private DepPropertyStore original;
//	private List<DependencyChangedLog> logs = new ArrayList<>();
	private Map<String, List<ChangedItemValue2>> changedHistory = new LinkedHashMap<>();
	
	private SvPropertyListener listener = new SvPropertyListener() {
		@Override
		public void onValueChanged(String id, String value) {
//			logs.add(new DependencyChangedLog(id, DependencyTargetElement.Value, value));
			addValue(id, value);
		}

		@Override
		public void onEnableChanged(String id, boolean b) {
//			logs.add(new DependencyChangedLog(id, DependencyTargetElement.Enabled, new Boolean(b).toString()));
			getHistory(id).add(new ChangedItemValue2(DependencyTargetElement.Enabled, String.valueOf(b)));
		}

		@Override
		public void onFlagChanged(String id, Flag flag) {
			if (flag.equals(Flag.MAX)) {
				getHistory(id).add(new ChangedItemValue2(DependencyTargetElement.Max, flag.toString()));
			}
			else if (flag.equals(Flag.MIN)) {
				getHistory(id).add(new ChangedItemValue2(DependencyTargetElement.Min, flag.toString()));
			}
		}

		@Override
		public void onVisibleChanged(String id, Boolean b) {
//			logs.add(new DependencyChangedLog(id, DependencyTargetElement.Visible, new Boolean(b).toString()));
			getHistory(id).add(new ChangedItemValue2(DependencyTargetElement.Visible, String.valueOf(b)));
		}

		@Override
		public void onListMaskChanged(String id, String value) {
//			logs.add(new DependencyChangedLog(id, DependencyTargetElement.ListItemEnabled, value));
			getHistory(id).add(new ChangedItemValue2(DependencyTargetElement.ListItemEnabled, value));
		}

		@Override
		public void onTitleChanged(String id, String title) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	public CachedPropertyStore(DepPropertyStore originalStore) {
		original = originalStore;
	}
	
	protected List<ChangedItemValue2> getHistory(String id) {
		if (!this.changedHistory.containsKey(id)) {
			this.changedHistory.put(id, new ArrayList<ChangedItemValue2>());
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

//	public List<DependencyChangedLog> getLogs() {
//		return logs;
//	}
//
//	public void clearLogs() {
//		this.logs.clear();
//	}
	
	public void commit() {		
		for (String id : this.cached.keySet()) {
			if (!this.changedHistory.containsKey(id)) {
				continue;
			}
			for (ChangedItemValue2 item : this.getHistory(id)) {
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

	public Map<String, List<ChangedItemValue2>> getChangedHistory() {
		return changedHistory;
	}

	public String getMessage(String id) {
		String ret = "";
		for (String key : this.changedHistory.keySet()) {
			if (!key.equals(id)) {
				List<ChangedItemValue2> item = this.changedHistory.get(key);
				ret += this.cached.get(key).getTitle()  + " ("+  key+ ")" +  ":\n";
				for (ChangedItemValue2 v : item) {
					ret += " " + v.getElement() + " -> " + v.getValue() + "\n";
				}
			}
		}
		return ret;
	}

	public List<String> getChangedIds() {
		return new ArrayList<String>(this.changedHistory.keySet());
	}

	public List<ChangedItemValue2> getChanged(String id) {
		return this.changedHistory.get(id);
	}

	public void clearHistory() {
		this.changedHistory.clear();
	}

	private void addValue(String id, String value) {
		List<ChangedItemValue2> remove = new ArrayList<>();
		for (ChangedItemValue2 v : getHistory(id)) {
			if (v.getElement().equals(DependencyTargetElement.Value)) {
				remove.add(v);
			}
		}
		getHistory(id).removeAll(remove);
		getHistory(id).add(new ChangedItemValue2(DependencyTargetElement.Value, value));
	}
}

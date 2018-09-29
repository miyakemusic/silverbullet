package jp.silverbullet.trashdependency.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.silverbullet.SvProperty;
import jp.silverbullet.SvPropertyListener;
import jp.silverbullet.SvPropertyStore;
import jp.silverbullet.trash.speceditor2.DependencySpecDetail;
import jp.silverbullet.trash.unknown.ChangedItemValue;


public class TentativePropertyStore {
		
	private Map<String, List<ChangedItemValue>> changedHistory = new LinkedHashMap<>();
	
	private Map<String, SvProperty> properties = new HashMap<>();
	private SvPropertyStore propertiesStore;
	private SvPropertyListener listener = new SvPropertyListener() {

		@Override
		public void onValueChanged(String id, String value) {
			getHistory(id).add(new ChangedItemValue(DependencySpecDetail.VALUE, value));
		}

		@Override
		public void onEnableChanged(String id, boolean b) {
			getHistory(id).add(new ChangedItemValue(DependencySpecDetail.ENABLED, String.valueOf(b)));
		}

		@Override
		public void onFlagChanged(String id, Flag flag) {
			if (flag.equals(Flag.MAX)) {
				getHistory(id).add(new ChangedItemValue(DependencySpecDetail.MAX, flag.toString()));
			}
			else if (flag.equals(Flag.MIN)) {
				getHistory(id).add(new ChangedItemValue(DependencySpecDetail.MIN, flag.toString()));
			}
		}

		@Override
		public void onVisibleChanged(String id, Boolean b) {
			getHistory(id).add(new ChangedItemValue(DependencySpecDetail.VISIBLE, String.valueOf(b)));
		}

		@Override
		public void onListMaskChanged(String id, String value) {
			getHistory(id).add(new ChangedItemValue(DependencySpecDetail.LISTMASK, value));
		}

		@Override
		public void onTitleChanged(String id, String title) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	public TentativePropertyStore(SvPropertyStore propertiesStore) {
		this.propertiesStore = propertiesStore;
	}

	protected List<ChangedItemValue> getHistory(String id) {
		if (!this.changedHistory.containsKey(id)) {
			this.changedHistory.put(id, new ArrayList<ChangedItemValue>());
		}
		return this.changedHistory.get(id);
	}

	public SvProperty getProperty(String id) {
		if (!this.properties.keySet().contains(id)) {
			SvProperty original = this.propertiesStore.getProperty(id);
			SvProperty clone = original.clone();
			clone.addListener(listener);
			this.properties.put(id, clone);
		}
		return this.properties.get(id);
	}

	public void commit() {		
		for (String id : this.properties.keySet()) {
			if (!this.changedHistory.containsKey(id)) {
				continue;
			}
			for (ChangedItemValue item : this.getHistory(id)) {
				SvProperty propertyOriginal = this.propertiesStore.getProperty(id);
				SvProperty tmpChangedProperty = this.properties.get(id);
				if (item.element.equals(DependencySpecDetail.MAX)) {
					propertyOriginal.setMax(tmpChangedProperty.getMax());
				}
				else if (item.element.equals(DependencySpecDetail.MIN)) {
					propertyOriginal.setMin(tmpChangedProperty.getMin());
				}
				else if (item.element.equals(DependencySpecDetail.VALUE)) {
					propertyOriginal.setCurrentValue(tmpChangedProperty.getCurrentValue());
				}
				else if (item.element.equals(DependencySpecDetail.ENABLED)) {
					propertyOriginal.setEnabled(tmpChangedProperty.isEnabled());
				}
				else if (item.element.equals(DependencySpecDetail.VISIBLE)) {
					propertyOriginal.setVisible(tmpChangedProperty.isVisible());
				}
				else if (item.element.equals(DependencySpecDetail.LISTMASK)) {
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
				ret += this.properties.get(key).getTitle()  + " ("+  key+ ")" +  ":\n";
				for (ChangedItemValue v : item) {
					ret += " " + v.element + " -> " + v.value + "\n";
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
}

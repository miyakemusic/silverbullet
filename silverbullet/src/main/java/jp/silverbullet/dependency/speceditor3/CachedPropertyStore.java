package jp.silverbullet.dependency.speceditor3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.silverbullet.SvProperty;
import jp.silverbullet.SvPropertyListener;

public class CachedPropertyStore implements DepProperyStore {
	private Map<String, SvProperty> cached = new HashMap<>();
	private DepProperyStore original;
	private List<DependencyChangedLog> logs = new ArrayList<>();
	
	private SvPropertyListener listener = new SvPropertyListener() {
		@Override
		public void onValueChanged(String id, String value) {
			logs.add(new DependencyChangedLog(id, DependencyTargetElement.Value, value));
		}

		@Override
		public void onEnableChanged(String id, boolean b) {
			logs.add(new DependencyChangedLog(id, DependencyTargetElement.Enabled, new Boolean(b).toString()));
		}

		@Override
		public void onFlagChanged(String id, Flag flag) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onVisibleChanged(String id, Boolean b) {
			logs.add(new DependencyChangedLog(id, DependencyTargetElement.Visible, new Boolean(b).toString()));
		}

		@Override
		public void onListMaskChanged(String id, String value) {
			logs.add(new DependencyChangedLog(id, DependencyTargetElement.ListItemEnabled, value));
		}

		@Override
		public void onTitleChanged(String id, String title) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	public CachedPropertyStore(DepProperyStore originalStore) {
		original = originalStore;
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

	public List<DependencyChangedLog> getLogs() {
		return logs;
	}

	public void clearLogs() {
		this.logs.clear();
	}
	
	
}

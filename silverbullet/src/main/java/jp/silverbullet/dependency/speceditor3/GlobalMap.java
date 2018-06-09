package jp.silverbullet.dependency.speceditor3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;

import jp.silverbullet.dependency.speceditor3.ui.DependencyEditorModel;
import jp.silverbullet.dependency.speceditor3.ui.GlobalMapListener;
import jp.silverbullet.property.PropertyHolder;

public class GlobalMap {
	private Map<String, DNode> map = new HashMap<>();
	@JsonIgnore
	private Set<GlobalMapListener> listeners = new HashSet<GlobalMapListener>();
	private int maxLevelInde = 0;
	private DependencyEditorModel dependecyEditorModel;
	
	public GlobalMap(DependencyEditorModel dependencyEditorModel) {
		this.dependecyEditorModel = dependencyEditorModel;
		build();
	}

	private void build() {
		this.map.clear();
		for (String id : dependecyEditorModel.getDependencySpecHolder().getSpecs().keySet()) {
			DependencySpec2 spec = dependecyEditorModel.getDependencySpecHolder().getSpecs().get(id);
			DNode node = getNode(id);
			for (String s : spec.getTriggerIds()) {
				connect(node, s);
			}
		}
		
		for (DNode node : map.values()) {
			node.setLevel(0, new ArrayList<>());
		}	
		List<String> independent = new ArrayList<>();
		for (String key : map.keySet()) {
			DNode node = map.get(key);
			this.maxLevelInde = Math.max(node.getLevel(), this.maxLevelInde);
			if (node.independent()) {
				independent.add(key);
			}
		}
		for (String key : independent) {
			this.map.remove(key);
		}
		
	}

	private void connect(DNode node, String s) {
		DNode trigger = getNode(s.split("\\.")[0]);
		node.getParents().add(trigger);
		trigger.getChildren().add(node);
	}

	private DNode getNode(String id) {
		if (!map.containsKey(id)) {
			map.put(id, new DNode(id));
		}
		return map.get(id);
	}

	public int getLevelCount() {
		return maxLevelInde + 1;
	}

	public List<DNode> getNodes(int level) {
		List<DNode> ret = new ArrayList<DNode>();
		for (DNode node : map.values()) {
			if (node.getLevel() == level) {
				ret.add(node);
			}
		}
		return ret;
	}

	public void addListener(GlobalMapListener globalMapListener) {
		this.listeners.add(globalMapListener);
	}

	public void setSelectedId(String id) {
		dependecyEditorModel.setSelectedId(id);
		for (GlobalMapListener listener : this.listeners) {
			listener.onIdChange(id);
		}
	}

	public void update() {
		this.build();
		for (GlobalMapListener listener : this.listeners) {
			listener.onUpdated();
		}
	}

	public PropertyHolder getPropertyHolder() {
		return this.dependecyEditorModel.getPropertyHolder();
	}

	public DependencyEditorModel getDependencyEditorModel() {
		return this.dependecyEditorModel;
	}
}


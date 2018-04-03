package jp.silverbullet.dependency.speceditor3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalMap {
	private Map<String, DNode> map = new HashMap<>();
	private int levelCount = 0;
	
	public GlobalMap(DependencySpecHolder2 holder) {
		for (String id : holder.getSpecs().keySet()) {
			DependencySpec2 spec = holder.getSpecs().get(id);
			
			DNode node = getNode(id);
			for (String s : spec.getTriggerIds()) {
				connect(node, s);
			}
		}
		
		for (DNode node : map.values()) {
			//System.out.println(node.getParents().size() + ":" + node.getId());
			node.setLevel(0);
		}
		
		for (DNode node : map.values()) {
			this.levelCount = Math.max(node.getLevel(), this.levelCount);
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
		return levelCount;
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
}


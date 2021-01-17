package jp.silverbullet.testspec;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TsNode {

	private Map<String, TsNode> subNodes = new LinkedHashMap<>();
	private String id;
	
	public TsNode() {}
	
	public TsNode(String id) {
		this.id = id;
	}
	
	public TsNode add(String id, TsNode node) {
		this.subNodes.put(id, node);
		return this;
	}

	public Map<String, TsNode> getSubNodes() {
		return subNodes;
	}

	public String getId() {
		return id;
	}

	public void setSubNodes(Map<String, TsNode> subNodes) {
		this.subNodes = subNodes;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int allNodesCount() {
		if (this.subNodes.size() == 0) {
			return 1;
		}
		int ret = 0;
		for (TsNode node : this.subNodes.values()) {
			ret += node.allNodesCount();
		}
		return ret;
	}
	
	
}

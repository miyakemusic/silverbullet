package jp.silverbullet.testspec;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TsNode {

	
	private Map<String, TsPort> outputs = new LinkedHashMap<>();
	private Map<String, TsPort> inputs = new LinkedHashMap<>();
	
	
	private String id;
	
	public TsNode() {}
	
	public TsNode(String id) {
		this.id = id;
	}
	
//	public TsNode add(String id, TsPort port) {
//		this.outputs.put(id, port);
//		return this;
//	}

	public Map<String, TsPort> getOutputs() {
		return outputs;
	}

	public Map<String, TsPort> getInputs() {
		return inputs;
	}

	public String getId() {
		return id;
	}

	public void setOutputs(Map<String, TsPort> subNodes) {
		this.outputs = subNodes;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int allNodesCount() {
		if (this.outputs.size() == 0) {
			return 1;
		}
		int ret = 0;
		for (TsPort port : this.outputs.values()) {
			if (!port.terminated()) {
				ret += port.pairPort().owner().allNodesCount();
			}
		}
		return ret;
	}

	public TsPort port_in(String id2) {
		if (!this.inputs.keySet().contains(id2)) {
			this.inputs.put(id2, new TsPort(this) {
				@Override
				public String getName() {
					return id2;
				}
			});
		}
		return this.inputs.get(id2);
	}
	
	public TsPort port_in() {
		return port_in("in");
	}
	public TsPort port_out(String id2) {
		if (!this.outputs.keySet().contains(id2)) {
			this.outputs.put(id2, new TsPort(this) {
				@Override
				public String getName() {
					return id2;
				}				
			});
		}
		return this.outputs.get(id2);
	}
}

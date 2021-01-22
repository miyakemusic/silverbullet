package jp.silverbullet.testspec;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class TsNode {
	abstract protected String generateId(TsPort port);
	
	private Map<String, TsPort> outputs = new LinkedHashMap<>();
	private Map<String, TsPort> inputs = new LinkedHashMap<>();
	public String id = "";
	
	private String name;
	
	public TsNode() {}
	
	public TsNode(String name) {
		this.name = name;
	}
	
	public Map<String, TsPort> getOutputs() {
		return outputs;
	}

	public Map<String, TsPort> getInputs() {
		return inputs;
	}

	public String getName() {
		return name;
	}

	public void setOutputs(Map<String, TsPort> subNodes) {
		this.outputs = subNodes;
	}

	public void setName(String name) {
		this.name = name;
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

	private TsPort createPort(String name, TsNode parent) {
		TsPort port = new TsPort(this) {
			@Override
			public String getName() {
				return name;
			}
		};
		port.id = generateId(port);
		return port;
	}
	
	public TsPort port_in(String name) {
		if (!this.inputs.keySet().contains(name)) {			
			this.inputs.put(name, createPort(name, this));
		}
		return this.inputs.get(name);
	}
	
	public TsPort port_in() {
		return port_in("in");
	}
	
	public TsPort port_out(String name) {
		if (!this.outputs.keySet().contains(name)) {
			this.outputs.put(name, createPort(name, this));
		}
		return this.outputs.get(name);
	}
}

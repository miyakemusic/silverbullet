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

	private TsPort createPort(String name, String id, TsNode parent) {
		TsPort port = new TsPort(this) {
			@Override
			public String getName() {
				return name;
			}
		};
		if (id == null) {
			port.id = generateId(port);
		}
		else {
			port.id = id;
		}
		portCreated(port);
		return port;
	}
	
	protected abstract void portCreated(TsPort port);

	public TsPort port_in(String name) {
		return port_in(name, null);
	}
	
	public TsPort port_in(String name, String id) {
		if (!this.inputs.keySet().contains(name)) {			
			this.inputs.put(name, createPort(name, id, this));
		}
		return this.inputs.get(name);
	}
	
	public TsPort port_in() {
		return port_in("in");
	}
	
	public TsPort port_out(String name) {
		return port_out(name, null);
	}
	
	public TsPort port_out(String name, String id) {
		if (!this.outputs.keySet().contains(name)) {
			this.outputs.put(name, createPort(name, id, this));
		}
		return this.outputs.get(name);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setInputs(Map<String, TsPort> inputs) {
		this.inputs = inputs;
	}

	public void copyPortConfig(String id2, TsPortConfig config) {
		if (containsId(id2, this.inputs)) {
			this.inputs.forEach((k,v) -> v.config(config));
		}
		else if (containsId(id2, this.outputs)) {
			this.outputs.forEach((k,v) -> v.config(config));
		}
	}

	private boolean containsId(String id2, Map<String, TsPort> inputs2) {
		for (TsPort port : inputs2.values()) {
			if (port.id.equals(id2)) {
				return true;
			}
		}
		return false;
	}
	
}

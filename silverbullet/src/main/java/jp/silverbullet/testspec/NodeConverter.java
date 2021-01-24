package jp.silverbullet.testspec;

public class NodeConverter {

	private PrsNode convert(TsNode tsNode) {
		PrsNode ret = new PrsNode();
		ret.id = tsNode.id;
		ret.name = tsNode.getName();
		tsNode.getInputs().forEach((k,v) -> {
			ret.inputs.put(k, new PrsPort(v.id, v.getName(), pairPort(v), v.config()));
		});
		tsNode.getOutputs().forEach((k,v) -> {
			ret.outputs.put(k, new PrsPort(v.id, v.getName(), pairPort(v), v.config()));
		});
		return ret;
	}

	private String pairPort(TsPort v) {
		if (v.pairPort() != null) {
			return v.pairPort().id;
		}
		return null;
	}

	public PrsNetworkConfiguration persistentData(NetworkConfiguration netConfig) {
		PrsNetworkConfiguration ret = new PrsNetworkConfiguration();
		ret.root = convert(netConfig.getRootNode());
		
		netConfig.allNodes.forEach((name, node) -> {
			ret.list.add(convert(node));
		});

		return ret;
	}

	public NetworkConfiguration programData(PrsNetworkConfiguration prsConfig) {
		NetworkConfiguration ret = new NetworkConfiguration();
		
		// create root
		//ret.createRoot(prsConfig.root.name, prsConfig.root.id);
		
		// create nodes
		prsConfig.list.forEach(prsNode -> {			
			ret.createNode(prsNode.name, prsNode.id);
		});
		
		// set root
		ret.setRoot(prsConfig.root.id);
		
		// create ports
		prsConfig.list.forEach(prsNode -> {		
			prsNode.inputs.forEach((portName, port) -> {
				ret.findNode(prsNode.id).port_in(portName, port.id).config(port.config);
			});
			
			prsNode.outputs.forEach((portName, port) -> {
				ret.findNode(prsNode.id).port_out(portName, port.id).config(port.config);
			});
		});
		
		// connect ports
		prsConfig.list.forEach(prsNode -> {		
			prsNode.inputs.forEach((portName, port) -> {
				if (port.pairPortId != null) {
					ret.findPort(port.id).connect(ret.findPort(port.pairPortId));
				}
			});
			
			prsNode.outputs.forEach((portName, port) -> {
				if (port.pairPortId != null) {
					ret.findPort(port.id).connect(ret.findPort(port.pairPortId));
				}
			});
		});
		
		return ret;
	}

}

package jp.silverbullet.testspec;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NetworkConfiguration {
	private TsNode rootNode = new TsNode("ROOT") {

		@Override
		protected String generateId(TsPort port) {
			return NetworkConfiguration.this.generateId(port);
		}

		@Override
		protected void portCreated(TsPort port) {
			allPorts.put(port.id, port);
		}
		
	};
	
	public TsNode root() {
		return rootNode;
	}

	public void setRoot(TsNode root) {
		this.rootNode = root;
	}

	@Override
	public String toString() {
		return  new TextGenerator().getText(rootNode);
	}

	public TsNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(TsNode rootNode) {
		this.rootNode = rootNode;
	}

	public NetworkConfiguration createDemo() {
		NetworkConfiguration networkConfig = new NetworkConfiguration();
		
		TsNode backbone = networkConfig.createRoot("BACKBONE");
		
		TsNode olt = networkConfig.createNode("OLT_001");
		backbone.port_out("out1").connect(olt.port_in("in1"));
		backbone.port_out("in1").connect(olt.port_in("out1"));
		backbone.port_out("out2").connect(olt.port_in("in2"));
		backbone.port_out("in2").connect(olt.port_in("out2"));
		
		TsNode splitter1 = networkConfig.createNode("SPLITTER_1");
		
		TsNode splitter1_1 = networkConfig.createNode("SPLITTER_1_1");
		for (int i = 0; i < 8; i++) {
			splitter1_1.port_out("B" + i).connect(networkConfig.createNode("ONU" + i).port_in());
		}
				
		TsNode splitter1_2 = networkConfig.createNode("SPLITTER_1_2");
		for (int i = 0; i < 16; i++) {
			splitter1_2.port_out("B" + i).connect(networkConfig.createNode("ONU" + i).port_in());
		}
		
		splitter1.port_out("B0").connect(networkConfig.createNode("ONU0").port_in());
		splitter1.port_out("B1").connect(networkConfig.createNode("ONU1").port_in());
		splitter1.port_out("B2").connect(splitter1_1.port_in());
		splitter1.port_out("B3").connect(networkConfig.createNode("ONU2").port_in());
		splitter1.port_out("B4").connect(networkConfig.createNode("ONU3").port_in());
		splitter1.port_out("B5").connect(networkConfig.createNode("ONU4").port_in());
		splitter1.port_out("B6").connect(networkConfig.createNode("UNUSED").port_in());
		splitter1.port_out("B7").connect(splitter1_2.port_in());
		
		olt.port_out("SLOT1").connect(splitter1.port_in());
		
		TsNode splitter2 = networkConfig.createNode("SPLITTER_2");
		olt.port_out("SLOT2").connect(splitter2.port_in());
		for (int i = 0; i < 16; i++) {
			splitter2.port_out("B" + i).terminate();
		}
		
		TsNode splitter3 = networkConfig.createNode("SPLITTER_3");
		olt.port_out("SLOT3").connect(splitter3.port_in());
		for (int i = 0; i < 16; i++) {
			splitter3.port_out("B" + i).terminate();
		}
		
		TsNode splitter4 = networkConfig.createNode("SPLITTER_4");
		olt.port_out("SLOT4").connect(splitter4.port_in());
		for (int i = 0; i < 16; i++) {
			splitter4.port_out("B" + i).terminate();
		}	
		this.rootNode = networkConfig.rootNode;
		return networkConfig;
	}

	TsNode createNode(String name) {
		return createNode(name, null);
	}
	
	TsNode createNode(String name, String id) {
		TsNode node = new TsNode(name) {
			@Override
			protected String generateId(TsPort port) {
				return NetworkConfiguration.this.generateId(port);
			}

			@Override
			protected void portCreated(TsPort port) {
				allPorts.put(port.id, port);
			}
			
		};
		if (id == null) {
			node.id = generateId(node);
		}
		else {
			node.id = id;
		}
		allNodes.put(node.id, node);
		return node;
	}

	public Map<String, TsNode> allNodes = new HashMap<>();
	public Map<String, TsPort> allPorts = new HashMap<>();
	
	public TsNode createRoot(String name) {
		return createRoot(name, null);
	}
	
	public TsNode createRoot(String name, String id) {
		TsNode root = createNode(name);
		if (id == null) {
			root.id = generateId(root);
		}
		else {
			root.id = id;
		}
		setRoot(root);
		return root;
	}

	protected String generateId(Object node) {
		return String.valueOf(node.hashCode());
	}

	public int allNodesCount() {
		return this.rootNode.allNodesCount();
	}

	public TsNode findNode(String id) {
		return this.allNodes.get(id);
	}

	public TsPort findPort(String id) {
		return this.allPorts.get(id);
	}

	public void setRoot(String id) {
		this.rootNode = this.findNode(id);
	}

	public void copyConfig(String id, TsPortConfig config) {
		this.allPorts.get(id).getOwner().copyPortConfig(id, config);
	}

}

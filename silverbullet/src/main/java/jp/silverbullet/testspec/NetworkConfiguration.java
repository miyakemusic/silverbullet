package jp.silverbullet.testspec;

public class NetworkConfiguration {
	private TsNode rootNode = new TsNode("ROOT");
	
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
		NetworkConfiguration ret = new NetworkConfiguration();
		
		TsNode backbone = new TsNode("Backbone");
		ret.setRoot(backbone);
		
		TsNode olt = new TsNode("OLT_001");
		backbone.port_out("P1").connect(olt.port_in("I1"));
		backbone.port_out("P2").connect(olt.port_in("I2"));
//		backbone.port_out("P3").connect(olt.port_in("I3"));
//		backbone.port_out("P4").connect(olt.port_in("I4"));
		
		TsNode splitter1 = new TsNode("SPL1");
		
		TsNode splitter1_1 = new TsNode("SPL1_1");
		for (int i = 0; i < 8; i++) {
			splitter1_1.port_out("B" + i).connect(new TsNode("ONU" + i).port_in());
		}
				
		TsNode splitter1_2 = new TsNode("SPL1_2");
		for (int i = 0; i < 8; i++) {
			splitter1_2.port_out("B" + i).connect(new TsNode("ONU" + i).port_in());
		}
		
		splitter1.port_out("B0").connect(new TsNode("ONU0").port_in());
		splitter1.port_out("B1").connect(new TsNode("ONU1").port_in());
		splitter1.port_out("B2").connect(splitter1_1.port_in());
		splitter1.port_out("B3").connect(new TsNode("ONU2").port_in());
		splitter1.port_out("B4").connect(new TsNode("ONU3").port_in());
		splitter1.port_out("B5").connect(new TsNode("ONU4").port_in());
		splitter1.port_out("B6").connect(splitter1_2.port_in());
		splitter1.port_out("B7").connect(new TsUnused().port_in());

		olt.port_out("SLOT1").connect(splitter1.port_in());
		
		TsNode splitter2 = new TsNode("SPL2");
		olt.port_out("SLOT2").connect(splitter2.port_in());
		
		TsNode splitter3 = new TsNode("SPL3");
		olt.port_out("SLOT3").connect(splitter3.port_in());
	
		TsNode splitter4 = new TsNode("SPL4");
		olt.port_out("SLOT4").connect(splitter4.port_in());
		
		return ret;
	}

	public int allNodesCount() {
		return this.rootNode.allNodesCount();
	}
}

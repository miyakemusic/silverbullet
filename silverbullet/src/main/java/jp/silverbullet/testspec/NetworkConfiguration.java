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
		TsOlt olt = new TsOlt("OLT_001", 12);
		ret.setRoot(olt);
		
		TsSplitter splitter1 = new TsSplitter("SPL1", 8);
		
		TsSplitter splitter1_1 = new TsSplitter("SPL1_1", 8);
		for (int i = 0; i < 8; i++) {
			splitter1_1.add("B" + i, new TsOnu("ONU" + i + "_SPL1_1"));
		}
				
		TsSplitter splitter1_2 = new TsSplitter("SPL1_2", 8);
		for (int i = 0; i < 8; i++) {
			splitter1_2.add("B" + i, new TsOnu("ONU" + i + "_SPL1_2"));
		}
		
		splitter1.add("B0", new TsOnu("ONU0_SPL1")).add("B1", new TsOnu("ONU1_SPL1")).add("B2", splitter1_1).add("B3", new TsOnu("ONU2_SPL1"))
		.add("B4", new TsOnu("ONU3_SPL1")).add("B5", new TsOnu("ONU4_SPL1")).add("B6", splitter1_2).add("B7", new TsUnused());

		olt.add("SLOT1", splitter1);
		
		TsSplitter splitter2 = new TsSplitter("SPL2", 8);
		olt.add("SLOT2", splitter2);
		
		TsSplitter splitter3 = new TsSplitter("SPL3", 8);
		olt.add("SLOT3", splitter3);
	
		TsSplitter splitter4 = new TsSplitter("SPL4", 8);
		olt.add("SLOT4", splitter4);
		
		return ret;
	}

	public int allNodesCount() {
		return this.rootNode.allNodesCount();
	}
}

package jp.silverbullet.testspec;

import java.util.ArrayList;
import java.util.List;

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
		TsOlt olt = new TsOlt("TOKYO_MACHUNOUCHI_001", 12);
		ret.setRoot(olt);
		
		TsSplitter splitter1 = new TsSplitter("SPL1", 8);
		
		TsSplitter splitter1_1 = new TsSplitter("SPL1_1", 8);
		for (int i = 0; i < 8; i++) {
			splitter1_1.add("B" + i, new TsOnu("ONU" + i));
		}
				
		TsSplitter splitter1_2 = new TsSplitter("SPL1_2", 8);
		for (int i = 0; i < 8; i++) {
			splitter1_2.add("B" + i, new TsOnu("ONU" + i));
		}
		
		splitter1.add("B0", new TsOnu("ONU1")).add("B1", new TsUnused()).add("B2", splitter1_1).add("B3", new TsOnu("ONU4"))
		.add("B4", new TsOnu("ONU5")).add("B5", new TsOnu("ONU6")).add("B6", splitter1_2).add("B7", new TsUnused());

		olt.add("SLOT1", splitter1);
		
		return ret;
	}
}

package jp.silverbullet.testspec;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NetworkConfigurationTest {

	@Test
	void test() {
		NetworkConfiguration networkConfiguraton = new NetworkConfiguration();
		
		TsNode olt = networkConfiguraton.createRoot("TOKYO_MACHUNOUCHI_001");
		//networkConfiguraton.setRoot(olt);
		
		TsNode splitter1 = networkConfiguraton.createNode("SPL1");
		
		TsNode splitter1_1 = networkConfiguraton.createNode("SPL1_1");
		for (int i = 0; i < 8; i++) {
			//splitter1_1.add("B" + i, networkConfiguraton.createNode("ONU" + i).port_in("IN"));
			splitter1_1.port_out("B" + i).connect(networkConfiguraton.createNode("ONU" + i).port_in("IN"));
		}
				
		TsNode splitter1_2 = networkConfiguraton.createNode("SPL1_2");
		for (int i = 0; i < 8; i++) {
			splitter1_2.port_out("B" + i).connect(networkConfiguraton.createNode("ONU" + i).port_in("IN"));
		}
		
		splitter1.port_out("B0").connect(networkConfiguraton.createNode("ONU1").port_in("IN"));
		splitter1.port_out("B1").connect(networkConfiguraton.createNode("ONU2").port_in("IN"));
		splitter1.port_out("B2").connect(networkConfiguraton.createNode("ONU3").port_in("IN"));
		splitter1.port_out("B3").connect(networkConfiguraton.createNode("ONU4").port_in("IN"));
		splitter1.port_out("B4").connect(networkConfiguraton.createNode("ONU5").port_in("IN"));
		splitter1.port_out("B5").connect(networkConfiguraton.createNode("ONU6").port_in("IN"));
		splitter1.port_out("B6").connect(splitter1_1.port_in("IN"));
		splitter1.port_out("B7").connect(networkConfiguraton.createNode("ONU6").port_in("IN"));

		olt.port_out("SLOT1").connect(splitter1.port_in("IN"));
		
		System.out.println(networkConfiguraton.toString());
		
		
		int allNodes = networkConfiguraton.allNodesCount();
//		System.out.println(allNodes);
		new TsPresentationNodes(networkConfiguraton);
	}

}

package jp.silverbullet.testspec;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NodeConverterTest {

	@Test
	void test() {
		NetworkConfiguration netConfig = new NetworkConfiguration();
		netConfig = netConfig.createDemo();
		PrsNetworkConfiguration prsConfig = new NodeConverter().persistentData(netConfig);
		
		NetworkConfiguration netConfig2 = new NodeConverter().programData(prsConfig);
	}

}

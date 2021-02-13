package jp.silverbullet.testspec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestSpecGenerator {

	public TsTestSpec generate(NetworkConfiguration active, List<String> ids, String projectName) {
		List<String> nodes = new ArrayList<>(); 
		ids.forEach(id -> {
			TsNode node = active.findNode(id);
			nodes.add(node.getName());
		});
		TsTestSpec testSpec = new TsTestSpec(projectName, nodes);
				
		ids.forEach(id -> {
			TsNode node = active.findNode(id);
			func(node.id, node.getName(), node.getOutputs(), "OUTPUT", testSpec);
			func(node.id, node.getName(), node.getInputs(), "INPUT", testSpec);
		});

		return testSpec;
	}

	private void func(String nodeId, String nodeName, Map<String, TsPort> ports, String direction, TsTestSpec testSpec) {
		ports.forEach((k,v)-> {
			v.config().insideTest.forEach((vv) -> {
				testSpec.add(nodeId, nodeName,  v.id, direction, "Device Side", v.getName(), vv);
			});
			v.config().outsideTest.forEach((vv) -> {
				testSpec.add(nodeId, nodeName, v.id, direction, "Fiber Side", v.getName(), vv);
			});
		});	
	}


}

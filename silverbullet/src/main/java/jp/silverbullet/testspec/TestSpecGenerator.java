package jp.silverbullet.testspec;

import java.util.List;
import java.util.Map;

public class TestSpecGenerator {

	public TsTestSpec generate(NetworkConfiguration active, List<String> ids) {
		TsTestSpec testSpec = new TsTestSpec();
				
		ids.forEach(id -> {
			TsNode node = active.findNode(id);
			func(node.getName(), node.getOutputs(), "OUTPUT", testSpec);
			func(node.getName(), node.getInputs(), "INPUT", testSpec);
		});

		return testSpec;
	}

	private void func(String nodeName, Map<String, TsPort> ports, String direction, TsTestSpec testSpec) {
		ports.forEach((k,v)-> {
			v.config().insideTest.forEach((vv) -> {
				testSpec.add(nodeName, direction, "Device Side", v.getName(), vv);
			});
			v.config().outsideTest.forEach((vv) -> {
				testSpec.add(nodeName, direction, "Fiber Side", v.getName(), vv);
			});
		});	
	}


}

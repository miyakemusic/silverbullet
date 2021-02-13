package jp.silverbullet.testspec;

import java.util.ArrayList;
import java.util.List;

public class TsTestSpec {
	public String projectName;
	public List<TsTestSpecElement> spec = new ArrayList<>();
	public List<String> script = new ArrayList<>();
	private List<String> nodes;
	
	public TsTestSpec(String projectName2, List<String> nodes) {
		this.projectName = projectName2;
		this.nodes = nodes;
		init();
	}

	private void init() {
		this.script.add("// PROJECTNAME=" + projectName);
		nodes.forEach(node -> {
			this.script.add("// NODE=" + node);
		});
	}

	public void add(String nodeId, String nodeName, String portId, String portDirection, String testSide, String portName, String testMethod) {
		this.spec.add(new TsTestSpecElement(nodeId, nodeName, portId, portDirection, testSide, portName, testMethod));
	}
	public void clear() {
		this.script.clear();
		init();
	}
	public void add(String line) {
		this.script.add(line);
	}
	public List<String> script() {
		return this.script;
	}
	
	public List<String> nodes() {
		return this.nodes;
	}
}

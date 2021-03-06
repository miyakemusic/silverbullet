package jp.silverbullet.testspec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class TsPresentationPort{
	private int width = 50;
	private String name;
	private int top;
	private int left = 0;
	private int height;
	private TsPort tsPort;
	public String id;
	public String direction;
	
	public TsPresentationPort(String id, String direction, String name, int left, int index, int height, TsPort tsPort) {
		this.name = name;
		this.top = index * height;
		this.height = height;
		this.left = left;
		this.tsPort = tsPort;
		this.id = id;
		this.direction = direction;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTop() {
		return top;
	}
	public void setTop(int top) {
		this.top = top;
	}
	public int getLeft() {
		return left;
	}
	public void setLeft(int left) {
		this.left = left;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public void topOffset(int top2) {
		this.top += top2;
	}
	
	public TsPort tsPort() {
		return tsPort;
	}
}

public class TsPresentationNode {
	private String name;
	private int left;
	private int height;
	private int top;
	private int width;
	public int unitHeight = 20;
	
	private List<TsPresentationPort> output = new ArrayList<>();
	private List<TsPresentationPort> input = new ArrayList<>();
	private TsPresentationNode parent;
	private TsNode node;
		
	public String id;
	
	public TsPresentationNode() {}
	public TsPresentationNode(TsNode node, TsPresentationNode parent2, String[] input2, String[] output2, int left2) {
		this.name = node.getName();
		this.left = left2;

		this.height = max(node.allNodesCount(), node.getInputs().size(), node.getOutputs().size()) * unitHeight;
		this.parent = parent2;
		this.node = node;
		this.id = node.id;
		
		int i = 0;
		for (String name : node.getInputs().keySet()) {
			TsPort port  =node.getInputs().get(name);
			this.input.add(new TsPresentationPort(port.id, "in", port.getName(), left, i++, unitHeight, port));
		}
		i = 0;
		for (String name : node.getOutputs().keySet()) {
			TsPort port  =node.getOutputs().get(name);
			this.output.add(new TsPresentationPort(port.id, "out", port.getName(), left, i++, unitHeight, port));
		}
		
//		for (int i = 0; i < input2.length; i++) {
//			String portid = input2[i];
//			this.input.add(new TsPresentationPort(/*"in_" + serial + "_" + portid*/id, "in", portid, left, i, unitHeight, node.getInputs().get(portid)));
//		}
//		
//		for (int i = 0; i < output2.length; i++) {
//			String portid = output2[i];
//			this.output.add(new TsPresentationPort(/*"out_" + serial + "_" + portid*/id, "out", portid, left, i, unitHeight, node.getOutputs().get(portid)));
//		}
	}

	private int max(int v1, int v2, int v3) {
		return Math.max(v2,  v3);
		//return Math.max(Math.max(v1, v2), v3);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getLeft() {
		return left;
	}
	public void setLeft(int left) {
		this.left = left;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
		this.output.forEach(o -> o.setLeft(left + width - o.getWidth()));
		
	}
	public int getTop() {
		return top;
	}
	public void setTop(int top) {
		this.top = top;
		this.input.forEach(o -> o.topOffset(top));
		this.output.forEach(o -> o.topOffset(top));
	}	
	public List<TsPresentationPort> getOutput() {
		return output;
	}
	public void setOutput(List<TsPresentationPort> output) {
		this.output = output;
	}
	public List<TsPresentationPort> getInput() {
		return input;
	}
	public void setInput(List<TsPresentationPort> input) {
		this.input = input;
	}
	public int parentTop() {
		if (this.parent != null) {
			return this.parent.top;
		}
		else {
			return 0;
		}
	}

	public TsPresentationNode parent() {
		return this.parent;
	}

	public TsNode node () {
		return this.node;
	}
}

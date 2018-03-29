package jp.silverbullet.dependency.engine;

import java.util.ArrayList;
import java.util.List;

import jp.silverbullet.dependency.speceditor2.DependencySpecDetail;

public class DepNode {
	private DependencySpecDetail value;
	private List<DepNode> nodes = new ArrayList<DepNode>();
	private DepNode parent;
	public DepNode(DependencySpecDetail d, DepNode parent2) {
		this.parent = parent2;
		this.value = d;
	}
	public DependencySpecDetail getValue() {
		return value;
	}
	public DepNode addChild(DependencySpecDetail d) {
		DepNode newNode = new DepNode(d, this);
		nodes.add(newNode);
		return newNode;
	}
	public void setValue(DependencySpecDetail value) {
		this.value = value;
	}
	public List<DepNode> getNodes() {
		return nodes;
	}
	public void setNodes(List<DepNode> nodes) {
		this.nodes = nodes;
	}
	public DepNode getParent() {
		return parent;
	}
	public void setParent(DepNode parent) {
		this.parent = parent;
	}
	
}

package jp.silverbullet.dependency;

import java.util.ArrayList;
import java.util.List;

public class DependencyNode {
	private List<DependencyNode> children = new ArrayList<>();
	private DependencyProperty dependencyProperty;
	private List<DependencyNode> parents = new ArrayList<>();
	private boolean recursive = false;
	private int layer;

	public DependencyNode(DependencyProperty prop, DependencyNode parent, int layer) {
		this.dependencyProperty = prop;
		this.layer = layer;
		this.parents.add(parent);
	}
	
	public void addChild(DependencyNode depProp) {
		this.children.add(depProp);
	}

	public List<DependencyNode> getChildren() {
		return children;
	}

	public DependencyProperty getDependencyProperty() {
		return dependencyProperty;
	}

	public void setChildren(List<DependencyNode> children) {
		this.children = children;
	}

	public void setDependencyProperty(DependencyProperty dependencyProperty) {
		this.dependencyProperty = dependencyProperty;
	}

	public DependencyNode getParent() {
		return parents.get(0);
	}

	public void addParent(DependencyNode parent) {
		this.parents.add(parent);
	}

	private boolean alreadyChanged(DependencyNode dependencyNode) {
		if (dependencyNode.getParent() == null || dependencyNode.getParent().getParent() == null) {
			return false;
		}
		if (dependencyNode.getParent().getDependencyProperty().getId().equals(this.getDependencyProperty().getId())) {
			return true;
		}
		return alreadyChanged(dependencyNode.getParent());
	}

	public boolean isRecursive() {
		return alreadyChanged(this) || this.recursive;
	}
	
	public boolean isLeaf() {
		return this.children.size() == 0;
	}

	public int getLayer() {
		return layer;
		//return goback(this, 0) - 1;
	}

	private int goback(DependencyNode dependencyNode, int number) {
		if (dependencyNode.getParent() == null) {
			return number;
		}
		return goback(dependencyNode.getParent(), number + 1 );
	}

	public void setRecursive(boolean b) {
		this.recursive  = b;
	}
}

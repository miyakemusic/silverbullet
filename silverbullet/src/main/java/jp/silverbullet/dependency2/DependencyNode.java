package jp.silverbullet.dependency2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DependencyNode {

	private List<DependencyLink> childLinks = new ArrayList<>();
	private List<DependencyLink> parentLinks = new ArrayList<>();
	
	private String id;
	private DependencyNodeGenerator nodeGenerator;
	
	public DependencyNode(String id, DependencyNodeGenerator nodeGenerator) {
		this.id = id;
		this.nodeGenerator = nodeGenerator;
	}

	public String getId() {
		return this.id;
	}

	public int getChildrenCount() {
		return this.getChildren().size();
	}

	public List<DependencyNode> getChildren() {
		return getNodes(this.getChildLinks());
	}

	private List<DependencyNode> getNodes(List<DependencyLink> childLinks2) {
		Set<DependencyNode> ret = new HashSet<>();
		for (DependencyLink link : childLinks2) {
			ret.add(this.nodeGenerator.getNode(link.getId()));
		}
		return new ArrayList<DependencyNode>(ret);
	}

	List<DependencyLink> getChildLinks() {		
		return this.childLinks;
	}

	public List<DependencyNode> getParents() {
		return getNodes(this.getParentLinks());
	}

	List<DependencyLink> getParentLinks() {
		return this.parentLinks;
	}

	public void addParents(String targetElement, Set<String> ids) {
		for (String id2 : ids) {
			this.addParentLink(new DependencyLink(id2, targetElement));
			this.nodeGenerator.getNode(id2).addChildLink(new DependencyLink(id, targetElement));
		}
	}

	private void addChildLink(DependencyLink dependencyLink) {
		this.childLinks.add(dependencyLink);	
	}

	private void addParentLink(DependencyLink dependencyLink) {
		this.parentLinks.add(dependencyLink);
	}

}

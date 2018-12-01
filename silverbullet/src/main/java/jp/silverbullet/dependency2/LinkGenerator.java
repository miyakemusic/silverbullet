package jp.silverbullet.dependency2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LinkGenerator {

	public enum LinkLevel {
		Detail,
		Simple
	}

	private DependencyNodeHolder nodeHolder;
	private List<Path> warningPaths = new ArrayList<>();
	
	public LinkGenerator(DependencyNodeHolder nodeHolder) {
		this.nodeHolder = nodeHolder;
	}

	public GenericLinks generateLinks(LinkLevel level, String id) {
		DependencyNode me = this.nodeHolder.getNode(id);
		List<GenericLink> links = new ArrayList<>();
		
		createLink(me, links);
		
		boolean loop = false;
		loop |= walkThroughtChild(me, links, new ArrayList<String>());
		loop |= walkThroughtParent(me, links, new ArrayList<String>());
		
		return new GenericLinks(links, loop);
	}

	abstract class WalkThrough {
		public boolean walk(DependencyNode me, List<GenericLink> links, ArrayList<String> experienced) {
			if (experienced.contains(me.getId())) {
				return false;
			}
			experienced.add(me.getId());
			for (DependencyNode subNode : getSubNode(me)) {
				createLink(subNode, links);
				walkThroughtChild(subNode, links, new ArrayList<String>(experienced));
			}
			return true;			
		}

		abstract protected List<DependencyNode> getSubNode(DependencyNode me);
	}
	private boolean walkThroughtChild(DependencyNode me, List<GenericLink> links, ArrayList<String> experienced) {
		return new WalkThrough() {
			@Override
			protected List<DependencyNode> getSubNode(DependencyNode me) {
				return me.getChildren();
			}
		}.walk(me, links, experienced);
	}
	private boolean walkThroughtParent(DependencyNode me, List<GenericLink> links, ArrayList<String> experienced) {
		return new WalkThrough() {
			@Override
			protected List<DependencyNode> getSubNode(DependencyNode me) {
				return me.getParents();
			}
		}.walk(me, links, experienced);
	}
	
	private void createLink(DependencyNode node, List<GenericLink> links) {
		for (DependencyLink child : node.getChildLinks()) {
			createLink(node.getId(), child.getId(), child.getTargetElement(), links);
		}
	}

	public GenericLinks generateLinks(LinkLevel level) {
		List<GenericLink> links = new ArrayList<>();
		for (DependencyNode node : this.nodeHolder.getAllNodes()) {
			for (DependencyLink child : node.getChildLinks()) {
				createLink(node.getId(), child.getId(), child.getTargetElement(), links);
			}
			
			Path path = new Path();
			analyzeWarning(node, node, path);
		}
		
		
		if (level.equals(LinkLevel.Detail)) {
			//return links;
		}
		if (level.equals(LinkLevel.Simple)) {
			Map<String, Set<String>> map = new HashMap<>();
			for (GenericLink link : links) {
				if (!map.keySet().contains(link.getFrom())) {
					map.put(link.getFrom(), new HashSet<String>());
				}
				map.get(link.getFrom()).add(link.getTo());
			}
			List<GenericLink> links2 = new ArrayList<>();
			for (String id : map.keySet()) {
				for (String to : map.get(id)) {
					links2.add(new GenericLink(id, to, ""));
				}
			}
			links = links2;
		}

		return new GenericLinks(links, false);
	}

	private void analyzeWarning(DependencyNode initialNode, DependencyNode node, Path path) {
		for (DependencyNode child : node.getChildren()) {
			Path newPath = new Path(child);
			path.add(newPath);
			if (initialNode.equals(child)) {
				warningPaths .add(newPath);
				continue;
			}
			analyzeWarning(initialNode, child, newPath);
		}
	}

	private void createLink(String from, String to, String targetElement, List<GenericLink> links) {
		GenericLink newLink = new GenericLink(from, to, targetElement);
		for (GenericLink link : links) {
			if (link.equals(newLink)) {
				return;
			}
		}
		links.add(newLink);
	}

	public List<Path> getWarningPaths() {
		return warningPaths;
	}
	
	
}

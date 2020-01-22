package jp.silverbullet.core.dependency2;

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
		
		boolean noLoop = true;
		Set<String> loops = new HashSet<>();
		
		noLoop &= walkThroughtChild(me, links, loops, new ArrayList<String>());
		noLoop &= walkThroughtParent(me, links, loops, new ArrayList<String>());
		
		return new GenericLinks(links, loops);
	}

	abstract class WalkThrough {
		public boolean walk(DependencyNode me, List<GenericLink> links, Set<String> loops, ArrayList<String> experienced) {
			if (experienced.contains(me.getId())) {
				if (experienced.size() > 2) {
					String from = experienced.get(experienced.size()-2);
					String to = experienced.get(experienced.size()-1);
					loops.add(from + "-" +  to);
				}
				else {
					loops.add(experienced.get(experienced.size()-1));
				}
				return false;
			}
			experienced.add(me.getId());
			
			boolean ret = true;
			for (DependencyNode subNode : getSubNode(me)) {
				createLink(subNode, links);
				ret &= walkThroughtChild(subNode, links, loops, new ArrayList<String>(experienced));
			}
			return ret;			
		}
		abstract protected List<DependencyNode> getSubNode(DependencyNode me);
	}
	private boolean walkThroughtChild(DependencyNode me, List<GenericLink> links, Set<String> loops, ArrayList<String> experienced) {
		return new WalkThrough() {
			@Override
			protected List<DependencyNode> getSubNode(DependencyNode me) {
				return me.getChildren();
			}
		}.walk(me, links, loops, experienced);
	}
	private boolean walkThroughtParent(DependencyNode me, List<GenericLink> links, Set<String> loops, ArrayList<String> experienced) {
		return new WalkThrough() {
			@Override
			protected List<DependencyNode> getSubNode(DependencyNode me) {
				return me.getParents();
			}
		}.walk(me, links, loops, experienced);
	}
	
	private void createLink(DependencyNode node, List<GenericLink> links) {
		for (DependencyLink child : node.getChildLinks()) {
			if (child.getId().equals(node.getId())) {
				continue;
			}
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

		return new GenericLinks(links);
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

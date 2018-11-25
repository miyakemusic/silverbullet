package jp.silverbullet.dependency2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LinkGenerator {

	enum LinkLevel {
		Detail,
		Simple
	}
	private DependencyNodeHolder nodeHolder;
	private List<Path> warningPaths = new ArrayList<>();
	
	public LinkGenerator(DependencyNodeHolder nodeHolder) {
		this.nodeHolder = nodeHolder;
	}

	public List<GenericLink> generateLinks(LinkLevel level) {
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

		return links;
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
		links.add(new GenericLink(from, to, targetElement));
	}

	public List<Path> getWarningPaths() {
		return warningPaths;
	}
	
	
}

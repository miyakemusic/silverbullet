package jp.silverbullet.core.dependency2;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DependencyNodeHolder implements DependencyNodeGenerator {
	private Map<String, DependencyNode> holder = new HashMap<>();
	
	@Override
	public DependencyNode getNode(String id) {
		if (!this.holder.keySet().contains(id)) {
			this.holder.put(id, new DependencyNode(id, this));
		}
		return this.holder.get(id);
	}

//	public void print() {
//		for (String id : this.holder.keySet()) {
//			System.out.println("###" + id + "###");
//			
//			DependencyNode node = this.holder.get(id);
//			print("#Parent", node.getParentLinks());
//			print("#Child", node.getChildLinks());
//		}
//	}
//
//	private void print(String comment, List<DependencyLink> links) {
//		System.out.println(comment);
//		for (DependencyLink link : links) {
//			System.out.println(link.getId() + "." + link.getTargetElement());
//		}
//	}

	public Collection<DependencyNode> getAllNodes() {
		return this.holder.values();
	}

}

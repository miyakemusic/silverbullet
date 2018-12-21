package obsolute;

import java.util.ArrayList;
import java.util.List;

public class DNode {
	private String id;
	private int level = 0;
	private List<DNode> parents = new ArrayList<DNode>();
	private List<DNode> children = new ArrayList<DNode>();
	
	public DNode(String id2) {
		this.id = id2;
	}

	public int getLevel() {
		return this.level;
	}

	public String getId() {
		return id;
	}

	public List<DNode> getParents() {
		return parents;
	}

	public List<DNode> getChildren() {
		return children;
	}
	
	public void setLevel(int level, List<String> experienced) {
		if (experienced.contains(id)) {
			return;
		}
		experienced.add(id);
		if (this.level < level) {
			this.level = level;
		}
		for (DNode sub : this.getChildren()) {
			if (!experienced.contains(sub.getId())) {
				List<String> subExperienced = new ArrayList<>();
				subExperienced.addAll(experienced);
				sub.setLevel(level + 1, subExperienced);
			}
		}
	}

	public boolean independent() {
		return (this.parents.size() == 0) && (this.children.size() == 0);
	}
}

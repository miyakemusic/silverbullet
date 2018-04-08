package jp.silverbullet.dependency.speceditor3;

import java.util.ArrayList;
import java.util.List;

public class DependencyBuilder3 {
	private DependencyNode tree = new DependencyNode(null, null, -1);
	
	public DependencyBuilder3(String id, DependencySpecHolder2 specHolder) {
		List<DependencyProperty> specs = specHolder.findSpecsToBeChangedSpecBy(id);
		List<String> experienced = new ArrayList<String>();
		experienced.add(id);
		analyze(tree, specHolder, specs, id, experienced, 0);
	}

	private void analyze(DependencyNode node, DependencySpecHolder2 specHolder, List<DependencyProperty> specs, String id, List<String> experienced, int layer) {
		for (DependencyProperty depProp : specs) {
			DependencyNode subNode = new DependencyNode(depProp, node, layer);
//			if (depProp.getId().equals(id)) {
			if (experienced.contains(depProp.getId())) {
				subNode.setRecursive(true);
			}
			node.addChild(subNode);
			if (!subNode.isRecursive()) {
				List<String> subExperienced = new ArrayList<>();
				subExperienced.add(depProp.getId());
				subExperienced.addAll(experienced);
				analyze(subNode, specHolder, specHolder.findSpecsToBeChangedSpecBy(depProp.getId()), id, subExperienced, layer+1);
			}
		}
	}

	public List<DependencyProperty> getSpecs(int layer) {
		List<DependencyProperty> ret = new ArrayList<>();
		collectDepdnencyProperties(tree, ret, layer);
		return ret;
	}

	private void collectDepdnencyProperties(DependencyNode node, List<DependencyProperty> ret, int layer) {
		for (DependencyNode n : node.getChildren()) {
			if (n.isRecursive()) {
				continue;
			}
			if (n.getLayer() == layer) {
				ret.add(n.getDependencyProperty());
			}
			else {
				collectDepdnencyProperties(n, ret, layer);
			}
		}
	}

	public int getLayerCount() {
		return countLayer(tree, 0);
	}

	private int countLayer(DependencyNode tree2, int layer) {
		if (tree2.isLeaf()) {
			return layer;
		}
		else {
			int count = 0;
			for (DependencyNode n : tree2.getChildren()) {
				count = Math.max(count, countLayer(n, layer+1));
			}
			return count;
		}
	}

	public DependencyNode getTree() {
		return tree;
	}

}

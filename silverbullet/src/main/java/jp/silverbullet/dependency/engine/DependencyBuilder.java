package jp.silverbullet.dependency.engine;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.silverbullet.dependency.speceditor2.DependencyFormula;
import jp.silverbullet.dependency.speceditor2.DependencySpecDetail;
import jp.silverbullet.dependency.speceditor2.DependencySpecHolder;

public class DependencyBuilder {
	private Map<Integer, List<DependencySpecDetail>> layers = new LinkedHashMap<>();
	private String initialId = "";
//	private DepNode root = new DepNode(null, null);
	private List<String> warnings = new ArrayList<String>();
	
	public DependencyBuilder(String id, DependencySpecHolder specHolder, MarcoExtractor marcoExtractor) {
		initialId = id;
		
		List<DependencySpecDetail> specs = specHolder.getActiveRelations(id);
		specs = marcoExtractor.extractMacros(specs);
		getLayer(0).addAll(specs);
		calc(1, specHolder, specs);
	}

	protected void calc(int layer, DependencySpecHolder specHolder,	List<DependencySpecDetail> specs) {
		for (DependencySpecDetail d : specs) {
			String id = d.getPassiveId();
			List<DependencySpecDetail> targets = new ArrayList<>();
			for (DependencySpecDetail target : specHolder.getActiveRelations(id)) {
				if (!d.getPassiveElement().equals(target.getSpecification().getElement())) {
					continue;
				}
				if (target.getPassiveId().equals(this.initialId)) {
					if (target.getPassiveElement().equals(DependencyFormula.VALUE)) { // <- this is trial code
						warnings.add("Loop!! by " + target.getSpecification().getId());
						continue;
					}
				}
				targets.add(target);
			}
			//addWIthSort(layer, targets, id);
			getLayer(layer).addAll(targets);
			
			calc(layer+1, specHolder, specHolder.getActiveRelations(d.getPassiveElement()));
		}
	}

	private void addWIthSort(int layer, List<DependencySpecDetail> targets, String id) {
		List<DependencySpecDetail> list = new ArrayList<DependencySpecDetail>();
		
		getLayer(layer).addAll(targets);
		for (DependencySpecDetail d : getLayer(layer)) {
			if (!d.getPassiveId().equals(id)) {
				list.add(d);
			}
		}
		for (DependencySpecDetail d : getLayer(layer)) {
			if (!list.contains(d)) {
				list.add(d);
			}
		}
		getLayer(layer).clear();
		getLayer(layer).addAll(list);
	}

	public List<String> getWarnings() {
		return warnings;
	}

	public Map<Integer, List<DependencySpecDetail>> getLayers() {
		return layers;
	}

	private List<DependencySpecDetail> getLayer(int layer) {
		if (!this.layers.containsKey(layer)) {
			this.layers.put(layer, new ArrayList<DependencySpecDetail>());
		}
		return this.layers.get(layer);
	}
}
class DepNode {
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
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
	private DepNode root = new DepNode(null, null);
	private List<String> warnings = new ArrayList<String>();
	
	public DependencyBuilder(String id, DependencySpecHolder specHolder) {
		initialId = id;
		
		List<DependencySpecDetail> specs = specHolder.getActiveRelations(id);
		getLayer(0).addAll(specs);
		calc(1, specHolder, specs);
		
		for (int l : this.layers.keySet()) {
		//	System.out.println(l);
			for (DependencySpecDetail d: this.layers.get(l)) {
			//	System.out.println(" " + d.toString());
			}
		}
	}

	protected void calc(int layer, DependencySpecHolder specHolder,	List<DependencySpecDetail> specs) {
		for (DependencySpecDetail d : specs) {
//			DepNode newNode = root2.addChild(d);
			List<DependencySpecDetail> targets = new ArrayList<>();
			for (DependencySpecDetail target : specHolder.getActiveRelations(d.getPassiveId())) {
				if (!d.getPassiveElement().equals(target.getSpecification().getElement())) {
			//		System.out.print("No:");
			//		System.out.println(d.toString() + "->" + target.toString());
					continue;
				}
			//	System.out.print("Yes:");
				if (target.getPassiveId().equals(this.initialId)) {
					if (target.getPassiveElement().equals(DependencyFormula.VALUE)) { // <- this is trial code
				//		System.out.println(target.getPassiveId() + " tried to changed initial ID' value !!!" + this.initialId);
						warnings.add("Loop!! by " + target.getSpecification().getId());
						continue;
					}
				}
				
				targets.add(target);
				
			}
			getLayer(layer).addAll(targets);
			
			calc(layer+1, specHolder, specHolder.getActiveRelations(d.getPassiveElement()));
		}
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
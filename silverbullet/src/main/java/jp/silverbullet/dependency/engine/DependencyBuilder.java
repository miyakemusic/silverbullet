package jp.silverbullet.dependency.engine;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.silverbullet.dependency.speceditor2.DependencySpecDetail;
import jp.silverbullet.dependency.speceditor2.DependencySpecHolder;

public class DependencyBuilder {
	private Map<Integer, List<DependencySpecDetail>> layers = new LinkedHashMap<>();
	private String initialId = "";
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
					if (target.getPassiveElement().equals(DependencySpecDetail.VALUE)) { // <- this is trial code
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

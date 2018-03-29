package jp.silverbullet.dependency.speceditor3;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.silverbullet.dependency.engine.MarcoExtractor;

public class ObsoluteDependencyBuilder2 {
	private Map<Integer, List<DependencyProperty>> layers = new LinkedHashMap<>();
	private String initialId = "";
	private List<String> warnings = new ArrayList<String>();
	
	public ObsoluteDependencyBuilder2(String id, DependencySpecHolder2 specHolder) {
		initialId = id;
		
		List<DependencyProperty> expressions = specHolder.findSpecsToBeChangedSpecBy(id);
		getLayer(0).addAll(expressions);
		calc(1, specHolder, expressions);
	}

	protected void calc(int layer, DependencySpecHolder2 specHolder, List<DependencyProperty> specs) {
		for (DependencyProperty d : specs) {
			String id = d.getId();
			List<DependencyProperty> expressionHolders = new ArrayList<>();
			
			for (DependencyProperty expressionHolder : specHolder.findSpecsToBeChangedSpecBy(id)) {		
//				if (!d.getElement().equals(expressionHolder.getElement())) {
//					continue;
//				}
				if (expressionHolder.getId().equals(this.initialId)) {
//					//if (expressionHolder.getTargetElement().equals(DependencySpecDetail.VALUE)) { // <- this is trial code
//						//warnings.add("Loop!! by " + expressionHolder.getExpressions().get.getSpecification().getId());
//						warnings.add("Loop!!");
						continue;
//					//}
				}
				expressionHolders.add(expressionHolder);
			}
			//addWIthSort(layer, targets, id);
			getLayer(layer).addAll(expressionHolders);
			
			calc(layer+1, specHolder, specHolder.findSpecsToBeChangedSpecBy(d.getId()));
		}
	}

	public List<String> getWarnings() {
		return warnings;
	}

	public Map<Integer, List<DependencyProperty>> getLayers() {
		return layers;
	}

	private List<DependencyProperty> getLayer(int layer) {
		if (!this.layers.containsKey(layer)) {
			this.layers.put(layer, new ArrayList<DependencyProperty>());
		}
		return this.layers.get(layer);
	}
}

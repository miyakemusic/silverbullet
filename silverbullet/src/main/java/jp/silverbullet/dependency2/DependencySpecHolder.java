package jp.silverbullet.dependency2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.SvProperty;
import jp.silverbullet.property.ListDetailElement;

public class DependencySpecHolder {
	private Map<String, DependencySpec> specs = new HashMap<>();
	private Map<String, Integer> priorityMap = new HashMap<>();
	
	public DependencySpecHolder() {
	}
	
	public void addSpec(DependencySpec spec) {
		this.specs.put(spec.getId(), spec);
	}

	public DependencySpec getSpec(String id) {
		if (!this.specs.keySet().contains(id)) {
			this.specs.put(id, new DependencySpec(id));
		}
		return this.specs.get(id);
	}

	public List<RuntimeDependencySpec> getRuntimeSpecs(String triggerId) {
		int triggerPriority= this.getPriority(triggerId);
		List<RuntimeDependencySpec> ret = new ArrayList<>();
		for (DependencySpec spec : this.specs.values()) {
			ExpressionHolder expressionHolder = spec.qualifies(triggerId);

			for (String target : expressionHolder.getExpressions().keySet()) {
				List<RuntimeDependencySpec> tmp = new ArrayList<>();
				for (Expression expression : expressionHolder.getExpressions().get(target)) {
					tmp.add(new RuntimeDependencySpec(spec.getId(), target, expression, triggerPriority < this.getPriority(spec.getId())));
				}
				processElse(tmp);
				ret.addAll(tmp);
			}
		}
		return ret;
	}

	private void processElse(List<RuntimeDependencySpec> tmp) {
		RuntimeDependencySpec elseSpec = null;
		for (RuntimeDependencySpec spec: tmp) {
			if (spec.getExpression().isElse()) {
				elseSpec = spec;
			}
		}
		if (elseSpec != null) {
			// move to last
			tmp.remove(elseSpec);
			for (RuntimeDependencySpec spec : tmp) {
				elseSpec.addElseSource(spec);
				spec.setElseSpec(elseSpec);
			}
			tmp.add(elseSpec);
		}
	}

	@JsonIgnore
	public Set<String> getSpecIds() {
		return this.specs.keySet();
	}
	
	@JsonIgnore
	public Set<String> getTriggerIds() {
		Set<String> triggerIds = new HashSet<>();
		for (DependencySpec spec : this.specs.values()) {
			triggerIds.addAll(spec.getTriggerIds());
		}
		return triggerIds;
	}

	@JsonIgnore
	public Set<String> getAllIds() {
		Set<String> ret  =new HashSet<>();
		ret.addAll(this.getTriggerIds());
		ret.addAll(this.getSpecIds());
		return ret;
	}
	
	public boolean containsId(String id) {
		return this.specs.keySet().contains(id);
	}

	public Map<String, DependencySpec> getSpecs() {
		return specs;
	}

	public void setSpecs(Map<String, DependencySpec> specs) {
		this.specs = specs;
	}

	public void setPriority(String id, int priority) {
		this.priorityMap.put(id, priority);
	}

	public int getPriority(String id) {
		if (!this.priorityMap.keySet().contains(id)) {
			return 0;
		}
		return this.priorityMap.get(id);
	}
	
	public Map<String, Integer> getPriorityMap() {
		return priorityMap;
	}

	public void setPriorityMap(Map<String, Integer> priorityMap) {
		this.priorityMap = priorityMap;
	}

	public void changeId(String prevId, String newId) {
		// TODO Auto-generated method stub
		
	}

	@JsonIgnore
	public DependencyRestriction getDependencyRestriction() {
		DependencyRestriction ret = new DependencyRestriction();
		DependencySpecHolder specHolder = this;
		for (String id : specHolder.getAllIds()) {
			SvProperty property = StaticInstances.getInstance().getBuilderModel().getProperty(id);
			ret.addColumn(property.getId(), "Enabled");
			ret.addColumn(property.getId(), "Value");
			if (property.isListProperty()) {
				for (ListDetailElement e : property.getListDetail()) {
					ret.addColumn(property.getId() + "." + e.getId(), "Enabled");
				}
			}
		}
		ret.build();
		return ret;
	}

}
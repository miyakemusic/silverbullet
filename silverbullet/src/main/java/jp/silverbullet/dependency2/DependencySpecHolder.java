package jp.silverbullet.dependency2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DependencySpecHolder {
	private Map<String, DependencySpec> specs = new HashMap<>();
	
	public DependencySpecHolder() {
	}
	
	public void addSpec(DependencySpec spec) {
		this.specs.put(spec.getId(), spec);
	}

	public DependencySpec getSpec(String id) {
		return this.specs.get(id);
	}

	public List<RuntimeDependencySpec> getRuntimeSpecs(String triggerId) {
		List<RuntimeDependencySpec> ret = new ArrayList<>();
		for (DependencySpec spec : this.specs.values()) {
			ExpressionHolder expressionHolder = spec.qualifies(triggerId);

			for (String target : expressionHolder.getExpressions().keySet()) {
				List<RuntimeDependencySpec> tmp = new ArrayList<>();
				for (Expression expression : expressionHolder.getExpressions().get(target)) {
					tmp.add(new RuntimeDependencySpec(spec.getId(), target, expression));
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

}

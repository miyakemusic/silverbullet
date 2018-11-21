package jp.silverbullet.dependency2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DependencySpecHolder {
	private Map<String, DependencySpec> specs = new HashMap<>();
	
	public void addSpec(DependencySpec spec) {
		this.specs.put(spec.getId(), spec);
	}

	public DependencySpec getSpec(String id) {
		return this.specs.get(id);
	}

	public List<RuntimeDependencySpec> getRuntimeSpecs(String triggerId, String value) {
		List<RuntimeDependencySpec> ret = new ArrayList<>();
		for (DependencySpec spec : this.specs.values()) {
			ExpressionHolder expressionHolder = spec.qualifies(triggerId, value);

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

}

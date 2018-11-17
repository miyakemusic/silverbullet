package jp.silverbullet.dependency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DependencyExpressionList {
	private ArrayList<DependencyExpression> dependencyExpressions = new ArrayList<>();

	public ArrayList<DependencyExpression> getDependencyExpressions() {
		return dependencyExpressions;
	}

	public void setDependencyExpressions(ArrayList<DependencyExpression> dependencyExpressions) {
		this.dependencyExpressions = dependencyExpressions;
	}

	public boolean remove(DependencyExpression pointer) {
		return dependencyExpressions.remove(pointer);
	}

	public Set<String> getTriggerIds() {
		Set<String> ret = new HashSet<>();
//		IdCollector collector = new IdCollector();
		for (DependencyExpression e: this.dependencyExpressions) {
			//ret.addAll(collector.collectIds(e.getExpression().getExpression()));
			ret.addAll(e.getTriggerIds());
		}
		return ret;
	}
	
}

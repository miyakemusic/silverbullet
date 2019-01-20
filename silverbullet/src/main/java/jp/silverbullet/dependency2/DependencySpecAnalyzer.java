package jp.silverbullet.dependency2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DependencySpecAnalyzer {

	private DependencySpecHolder specHolder;
	private DependencyNodeHolder nodeHolder = new DependencyNodeHolder();
	private LinkGenerator linkGenerator;
	
	public DependencySpecAnalyzer(DependencySpecHolder specHolder) {
		this.specHolder = specHolder;
		for (String id : this.specHolder.getAllIds()) {
			analyze(id);
		}
		
		this.linkGenerator = new LinkGenerator(this.nodeHolder);
		//print();
	}

	private void print() {
		this.nodeHolder.print();
	}

	public DependencyNode getNode(String id) {
		return this.nodeHolder.getNode(id);
	}
	
	private DependencyNode analyze(String id) {
		DependencyNode dependencyNode = this.nodeHolder.getNode(id);

		String[] targetElements = {DependencySpec.Value, DependencySpec.Enable, DependencySpec.Min, DependencySpec.Max};
		
		// analyze those who changes the id
		if (this.specHolder.containsId(id)) {
			DependencySpec spec = this.specHolder.getSpec(id);	
			
			for (String targetElement : targetElements) {
				if (!spec.containsTarget(targetElement)) {
					continue;
				}
				addParent(targetElement, spec, dependencyNode);
			}
			
			for (String option : spec.getTargetOptions()) {
				String targetElement = DependencySpec.OptionEnable + "#" + option;
				addParent(targetElement, spec, dependencyNode);
			}
		}
		return dependencyNode;
	}

	private void addParent(String targetElement, DependencySpec spec, DependencyNode dependencyNode) {
		List<Expression> expressions = spec.getExpression(targetElement);
		Set<String> ids = new HashSet<>();
		for (Expression expression : expressions) {
			ids.addAll(IdUtility.collectIds(expression.getTrigger()));
		}
		
		dependencyNode.addParents(targetElement, ids);
	}

	public LinkGenerator getLinkGenerator() {
		return linkGenerator;
	}
	
	
}

package jp.silverbullet.dependency2;

import java.util.List;
import java.util.Set;

public class DependencySpecDetail {
	
	private ExpressionHolder expressions = new ExpressionHolder();
	
	public void add(String targetElement, String enabled, String trigger, String condition) {
		expressions.add(targetElement, new Expression(enabled, trigger, condition));
	}

	public ExpressionHolder qualifies(String id) {
		return this.expressions.qualifies(id);
	}

	public void addValueCalculation(String option, String enabled, String trigger, String condition) {
		Expression expression = new Expression(enabled, trigger, condition);
		expression.setValueCalculationEnabled(true);
		expressions.add(option, expression);
	}

	public void add(String targetElement, String targetOption, String enabled, String trigger, String condition) {
		expressions.add(targetElement, targetOption, new Expression(enabled, trigger, condition));
	}

	public List<Expression> get(String targetElement) {
		return this.expressions.getExpressions(targetElement);
	}

	public List<String> getTargetOptions() {
		return this.expressions.getTargetOptions();
	}

	public boolean containsTarget(String targetElement) {
		return this.expressions.containsTarget(targetElement);
	}

	public Set<String> getTriggerIds() {
		return this.expressions.getTriggerIds();
	}

}

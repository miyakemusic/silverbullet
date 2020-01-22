package jp.silverbullet.core.dependency2;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DependencySpecDetail {
	
	private ExpressionHolder expressions = new ExpressionHolder();
	
	public DependencySpecDetail() {}
	
	public Expression createExpression(String targetElement, String enabled, String trigger, String condition) {
		Expression expression = new Expression(enabled, trigger, condition);
		expressions.add(targetElement, expression);
		return expression;
	}

	public ExpressionHolder qualifies(String id) {
		return this.expressions.qualifies(id);
	}

	public void addValueCalculation(String option, String enabled, String trigger, String condition) {
		Expression expression = new Expression(enabled, trigger, condition);
		expression.setValueCalculationEnabled(true);
		expressions.add(option, expression);
	}

	public Expression add(String targetElement, String targetOption, String enabled, String trigger, String condition) {
		Expression expression = new Expression(enabled, trigger, condition);
		expressions.add(targetElement, targetOption, expression);
		return expression;
	}

	public List<Expression> get(String targetElement) {
		return this.expressions.getExpressions(targetElement);
	}

	@JsonIgnore
	public List<String> getTargetOptions() {
		return this.expressions.getTargetOptions();
	}

	public boolean containsTarget(String targetElement) {
		return this.expressions.containsTarget(targetElement);
	}

	@JsonIgnore
	public Set<String> getTriggerIds() {
		return this.expressions.getTriggerIds();
	}

	public ExpressionHolder getExpressions() {
		return expressions;
	}

	public void setExpressions(ExpressionHolder expressions) {
		this.expressions = expressions;
	}

	public void update(String element, Integer row, String field, String value) {
		this.expressions.update(element, row, field, value);
	}

	public void copySpec(String fromTargetElement, String toTargetElement) {
		for (Expression exp : this.expressions.getExpressions(fromTargetElement)) {
			this.expressions.getExpressions(toTargetElement).add(exp.clone());
		}
	}

	public void changeId(String prevId, String newId) {
		this.expressions.changeId(prevId, newId);	
	}

	public void clear() {
		this.expressions.clear();
	}

	public void clear(String targetElement, String triggerId) {
		this.expressions.clear(targetElement, triggerId);
	}
	

}

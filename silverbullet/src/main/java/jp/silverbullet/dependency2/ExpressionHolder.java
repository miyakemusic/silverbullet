package jp.silverbullet.dependency2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpressionHolder {
	private Map<String, List<Expression>> expressions = new HashMap<>();
	
	private void addExpression(String option, Expression expression) {
		if (!expressions.keySet().contains(option)) {
			expressions.put(option, new ArrayList<Expression>());
		}
		expressions.get(option).add(expression);	
		sort(expressions.get(option));
	}

	private void sort(List<Expression> list) {
		Expression elseExp = null;
		for (Expression exp : list) {
			if (exp.isElse()) {
				elseExp = exp;
			}
		}
		if (elseExp != null) {
			list.remove(elseExp);
			list.add(elseExp);
		}
	}

	public void add(String option, Expression expression) {
		this.addExpression(option, expression);
	}

	public ExpressionHolder qualifies(String id) {
		ExpressionHolder ret = new ExpressionHolder();
		for (String option : this.expressions.keySet()) {
			
			for (Expression expression : this.expressions.get(option)) {
				if (expression.qualifies(id)) {
					ret.add(option, expression);
				}
				
				if (isElseQualified(ret, option, expression)) {
					ret.add(option, expression);
				}
			}
		}
		return ret;
	}

	private boolean isElseQualified(ExpressionHolder ret, String option, Expression expression) {
		return ret.getExpressions().get(option) != null && ret.getExpressions().get(option).size() > 0 && expression.isElse();
	}

	public Map<String, List<Expression>> getExpressions() {
		return expressions;
	}

	
}

package jp.silverbullet.dependency2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.silverbullet.property2.RuntimeProperty;

public class ExpressionHolder {
	private Map<String, List<Expression>> expressions = new HashMap<>();
	
	public ExpressionHolder() {}
	
	private void addExpression(String targetElement, Expression expression) {
		if (!expressions.keySet().contains(targetElement)) {
			expressions.put(targetElement, new ArrayList<Expression>());
		}
		expressions.get(targetElement).add(expression);	
		sort(expressions.get(targetElement));
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

	public void add(String targetElement, Expression expression) {
		this.addExpression(targetElement, expression);
	}

	public void add(String targetElement, String targetOption, Expression expression) {
		this.add(targetElement + "#" + targetOption, expression);
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

	public List<String> getTargetOptions() {
		List<String> ret = new ArrayList<>();
		for (String option : this.expressions.keySet()) {
			if (option.startsWith(DependencySpec.OptionEnable)) {
				ret.add(option.split("#")[1]);
			}
		}
		return ret;
	}

	public boolean containsTarget(String targetElement) {
		for (String te : this.expressions.keySet()) {
			if (te.contains("#")) {
				if (te.split("#")[0].equals(targetElement)) {
					return true;
				}
			}
			else {
				if (te.equals(targetElement)) {
					return true;
				}
			}
		}
		return false;
	}

	public List<Expression> getExpressions(String targetElement) {
		if (targetElement.equals(DependencySpec.OptionEnable)) {
			List<Expression> ret = new ArrayList<>();
			for (String te : this.expressions.keySet()) {
				if (te.startsWith(DependencySpec.OptionEnable)) {
					ret.addAll(this.expressions.get(te));
				}
			}
			return ret;
		}
		else {
			if (!this.expressions.containsKey(targetElement)) {
				this.expressions.put(targetElement, new ArrayList<Expression>());
			}
			return this.expressions.get(targetElement);
		}
	}

	public Set<String> getTriggerIds() {
		Set<String> ret = new HashSet<>();
		for (String value : this.expressions.keySet()) {
			for (Expression expression : this.expressions.get(value)) {
				ret.addAll(IdUtility.collectIds(expression.getTrigger()));
				if (expression.isValueCalculationEnabled()) {
					ret.addAll(IdUtility.collectIds(expression.getValue()));
				}
			}
		}
		return ret;
	}

	public void setExpressions(Map<String, List<Expression>> expressions) {
		this.expressions = expressions;
	}

	public void update(String element, Integer row, String field, String value) {
		List<Expression> list = this.expressions.get(element);
		Expression exp = null;
		if (list.size() > row) {
			exp = list.get(row);
		}
		else {
			exp = new Expression();
			list.add(exp);
		}
		
		if (field.equals(DependencySpec.Trigger)) {
			if (value.isEmpty()) {
				list.remove(row.intValue());
			}
			exp.setTrigger(value);
		}
		else if (field.equals(DependencySpec.Value)) {
			exp.setValue(value);
		}
		else if (field.equals(DependencySpec.Condition)) {
			exp.setCondition(value);
		}
		
		this.sort(list);
	}

	public void changeId(String prevId, String newId) {
		
		for (String targetElement : this.expressions.keySet()) {
			if (targetElement.startsWith(DependencySpec.OptionEnable)) {
				String id = targetElement.replace(DependencySpec.OptionEnable + RuntimeProperty.INDEXSIGN,"");
				if (IdUtility.isValidOption(prevId, id)) {
					String value = id.replace(prevId, "");
					List<Expression> expression = this.expressions.get(targetElement);
					this.expressions.put(DependencySpec.OptionEnable + RuntimeProperty.INDEXSIGN + newId + value, expression);
					this.expressions.remove(targetElement);
				}
			}
		}
		
		for (List<Expression> expression : this.expressions.values()) {
			for (Expression exp : expression) {
				exp.changeId(prevId, newId);
			}
		}
	}

	public void clear() {
		this.expressions.clear();
	}




}

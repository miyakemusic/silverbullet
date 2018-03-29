package jp.silverbullet.dependency.speceditor3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DependencyExpressionHolder {
	public enum SettingDisabledBehavior {
		Reject,
		Adjust,
		DependsOnStrength
	}
	
	private DependencyTargetElement targetElement;
	
	private Map<String, List<DependencyExpression>> expressions = new HashMap<>();

	private SettingDisabledBehavior settingDisabledBehavior;
	
	public DependencyExpressionHolder(DependencyTargetElement targetElement) {
		this.targetElement = targetElement;
	}
	
	public DependencyTargetElement getTargetElement() {
		return targetElement;
	}

	public void setTargetElement(DependencyTargetElement targetElement) {
		this.targetElement = targetElement;
	}

	public Map<String, List<DependencyExpression>> getExpressions() {
		return expressions;
	}
	
	public ExpressionBuilder addExpression() {
		DependencyExpression expression = new DependencyExpression() {
			@Override
			protected void targetValueAdded(String targetValue) {
				getExpressionList(targetValue).add(this);
			}
		};
		return expression.getExpression();
	}
	
	private List<DependencyExpression> getExpressionList(String targetValue) {
		if (!this.expressions.containsKey(targetValue)) {
			this.expressions.put(targetValue, new ArrayList<DependencyExpression>());
		}
		return this.expressions.get(targetValue);
	}

	public boolean containsToBeChangedBy(String id, DependencyTargetElement element) {
		return findToBeChangedBy(id, element).size() > 0;
	}

	private List<DependencyExpression> findToBeChangedBy(String id, DependencyTargetElement element) {
		List<DependencyExpression> ret = new ArrayList<DependencyExpression>();
		for (List<DependencyExpression> list : this.expressions.values()) {
			for (DependencyExpression expression : list) {
				if (expression.containsId(id, element)) {
					ret.add(expression);
				}
			}
		}
		return ret;
	}

	public List<DependencyProperty> getRelatedSpecs(String changedId, String listId,
			String triggerId, DependencyTargetElement targetElement2) {
		List<DependencyProperty> ret = new ArrayList<>();
		
		for (String resultValue : getExpressions().keySet()) {
			for (DependencyExpression condition : getExpressions().get(resultValue)) {
				if (condition.isEmpty()) {
					if (resultValue.contains("$"+triggerId + "." + targetElement2.name())) {
						ret.add(new DependencyProperty(changedId, listId, targetElement2, "", resultValue));
					}
				}
				else if (condition.containsId(triggerId, getTargetElement()) || condition.getExpression().getExpression().contains(DependencyExpression.ELSE)) {
					ret.add(new DependencyProperty(changedId, listId, targetElement2, condition.getExpression().getExpression(), resultValue));
				}
			}
		}
		
		return ret;
	}

	public void setSettingDisabledBehavior(SettingDisabledBehavior behavior) {
		this.settingDisabledBehavior = behavior;
	}

	public SettingDisabledBehavior getSettingDisabledBehavior() {
		return settingDisabledBehavior;
	}

}

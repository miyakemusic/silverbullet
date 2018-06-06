package jp.silverbullet.dependency.speceditor3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.sun.javafx.fxml.expression.Expression;
@XmlRootElement
public class DependencyExpressionHolder {
	public enum SettingDisabledBehavior {
		Reject,
		Adjust,
		DependsOnStrength
	}
	
	private DependencyExpressionListener listener = new DependencyExpressionListener() {
		@Override
		public void onTargetValueAdded(String targetValue, DependencyExpression dependencyExpression) {
			getExpressionList(targetValue).add(dependencyExpression);
		}
	};
	private DependencyTargetElement targetElement;
	
	private HashMap<String, DependencyExpressionList> expressions = new HashMap<>();

	private SettingDisabledBehavior settingDisabledBehavior = SettingDisabledBehavior.Reject;
	
	public DependencyExpressionHolder() {}
	
	public DependencyExpressionHolder(DependencyTargetElement targetElement) {
		this.targetElement = targetElement;
	}
	
	public DependencyTargetElement getTargetElement() {
		return targetElement;
	}

	public void setTargetElement(DependencyTargetElement targetElement) {
		this.targetElement = targetElement;
	}

	public HashMap<String, DependencyExpressionList> getExpressions() {
		return expressions;
	}
	
	public ExpressionBuilder addExpression() {
		DependencyExpression expression = new DependencyExpression(listener);
		return expression.getExpression();
	}
	
	private List<DependencyExpression> getExpressionList(String targetValue) {
		if (!this.expressions.containsKey(targetValue)) {
			this.expressions.put(targetValue, new DependencyExpressionList());
		}
		return this.expressions.get(targetValue).getDependencyExpressions();
	}

	public boolean containsToBeChangedBy(String id, DependencyTargetElement element) {
		return findToBeChangedBy(id, element).size() > 0;
	}

	private List<DependencyExpression> findToBeChangedBy(String id, DependencyTargetElement element) {
		List<DependencyExpression> ret = new ArrayList<DependencyExpression>();
		for (DependencyExpressionList list : this.expressions.values()) {
			for (DependencyExpression expression : list.getDependencyExpressions()) {
				if (expression.containsId(id, element)) {
					ret.add(expression);
				}
			}
		}
		return ret;
	}

	public List<DependencyProperty> getRelatedSpecs(String targetId, String selectionId,
			String triggerId, DependencyTargetElement targetElement2) {
		List<DependencyProperty> ret = new ArrayList<>();

		DependencyProperty elseCondition = null;
		for (String resultValue : getExpressions().keySet()) {
			for (DependencyExpression condition : getExpressions().get(resultValue).getDependencyExpressions()) {
				if (condition.isEmpty()) {
					if (resultValue.contains("$"+triggerId + "." + targetElement2.name())) {
						ret.add(new DependencyProperty(targetId, selectionId, targetElement2, "", resultValue, condition));
					}
				}
				else if (condition.containsId(triggerId, getTargetElement()) 
						) {
					ret.add(new DependencyProperty(targetId, selectionId, targetElement2, condition.getExpression().getExpression(), resultValue, condition));
				}
				else if (condition.getExpression().getExpression().contains(DependencyExpression.ELSE)) {
					elseCondition = new DependencyProperty(targetId, selectionId, targetElement2, condition.getExpression().getExpression(), resultValue, condition);					
					//ret.add(elseCondition);
				}
			}
		}

		if (!ret.isEmpty() && (elseCondition != null)) {
			for (DependencyProperty depProp : ret) {
				depProp.setElse(elseCondition);
			}
			ret.add(elseCondition);
		}
		return ret;
	}

	public void setSettingDisabledBehavior(SettingDisabledBehavior behavior) {
		this.settingDisabledBehavior = behavior;
	}

	public SettingDisabledBehavior getSettingDisabledBehavior() {
		return settingDisabledBehavior;
	}

	public void setExpressions(HashMap<String, DependencyExpressionList> expressions) {
		this.expressions = expressions;
	}

	public boolean remove(DependencyExpression pointer) {
		String removedKey = "";
		for (String key : this.expressions.keySet()) {
			DependencyExpressionList list = this.expressions.get(key);
			if (list.remove(pointer)) {
				removedKey = key;
				break;
			}
		}
		
		if (!removedKey.isEmpty()) {
			if (expressions.get(removedKey).getDependencyExpressions().size() == 0) {
				this.expressions.remove(removedKey);
			}
			return true;
		}
		return false;
	}

	public Set<String> getTriggerIds() {
		Set<String> ret = new HashSet<String>();
		IdCollector collector = new IdCollector();
		for (String key : this.expressions.keySet()) {
			ret.addAll(collector.collectIds(key));
			DependencyExpressionList list = this.expressions.get(key);
			ret.addAll(list.getTriggerIds());
		}
		return ret;
	}

	public boolean isEmpty() {
		return this.expressions.isEmpty();
	}

	public void remove(String value) {
		this.expressions.remove(value);
	}
}

package jp.silverbullet.dependency2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.silverbullet.web.ui.PropertyGetter;

public class DependencySpecRebuilder {

	private DependencySpecHolder newHolder = new DependencySpecHolder();
	private DependencySpecHolder depHolder;
	private PropertyGetter getter;
	
	public DependencySpecRebuilder(DependencySpecHolder depHolder, PropertyGetter store) {
		this.depHolder = depHolder;
		this.getter = store;
		for (String id : depHolder.getSpecs().keySet()) {
			handleOneSpec(id);
		}
	}

	public void handleOneSpec(String id) {
		DependencySpec spec = depHolder.getSpecs().get(id);
		Map<String, List<Expression>> expressionHolder = spec.getDependencySpecDetail().getExpressions().getExpressions();
		for (String targetElement : expressionHolder.keySet()) {
			List<Expression> expressions = expressionHolder.get(targetElement);
			
			if (targetElement.startsWith(DependencySpec.OptionEnable + "#") ) {
				String option = targetElement.replace(DependencySpec.OptionEnable + "#", "");

				List<String> trueCondition = new ArrayList<>();
				List<String> falseCondition = new ArrayList<>();
				for (Expression expression : expressions) {
					ExpressionParser parser = new ExpressionParser(expression.getTrigger());
					if (isPriority(id, parser.getId(), depHolder)) {
						if (expression.getValue().equals(DependencySpec.True)) {
							trueCondition.add(expression.getTrigger());
						}
						else if (expression.getValue().equals(DependencySpec.False)) {
							falseCondition.add(expression.getTrigger());
						}
					}
					else {
						newHolder.getSpec(id).getExpression(targetElement).add(expression);
					}
				}
				
				addCondition(id, option, trueCondition, falseCondition, getter, depHolder);
			}
			else {
				newHolder.getSpec(id).getExpression(targetElement).addAll(expressions);
			}
		}
	}

	private boolean isPriority(String id, String triggerId, DependencySpecHolder depHolder) {
		return depHolder.getPriority(id) <= depHolder.getPriority(triggerId);
	}

	private void addCondition(String id, String option, List<String> trueTrigger, List<String> falseTrigger, 
			PropertyGetter store, DependencySpecHolder depHolder) {
		
		if (isNotElse(trueTrigger)) {
			for (String trigger : trueTrigger) {
				ExpressionParser parser = new ExpressionParser(trigger);		
				this.newHolder.getSpec(parser.getId()).addValue(parser.getValue(), "$" + id + "==" + "%" + option, createCondition(trigger, trueTrigger));
				this.newHolder.getSpec(id).addValue(option, trigger, createConditionEx(id, trigger, option, depHolder, store));
			}
		}
		
		if (isNotElse(falseTrigger)) {
			for (String trigger : invert(trueTrigger, store)) {
				ExpressionParser parser = new ExpressionParser(trigger);
				this.newHolder.getSpec(parser.getId()).addValue(parser.getValue(),  "$" + id + "==" + "%" + option);
			}
		}
	}

	private String createConditionEx(String id, String trigger, String option2, 
			DependencySpecHolder depHolder, PropertyGetter store) {
		ExpressionParser parser = new ExpressionParser(trigger);
		String triggerId = parser.getId();
		String triggerValue = parser.getValue();
		
		DependencySpec spec = depHolder.getSpec(id);
		List<String> trueOptions = new ArrayList<>();
		
		for (String option : spec.getTargetOptions()) {
			if (option2.equals(option)) {
				continue;
			}
			List<Expression> expressions = spec.getExpression(DependencySpec.OptionEnable + "#" + option);
			for (Expression expression : expressions) {
				if (expression.getValue().equals(DependencySpec.True)) {
					ExpressionParser p2 = new ExpressionParser(expression.getTrigger());
					if (p2.getValue().equals(triggerValue) && p2.isEqual()) {
						trueOptions.add("$" + id + "==" + "%" + option);
					}
				}
				else if (expression.getValue().equals(DependencySpec.False)) {
					
				}
			}
		}
//		trueOptions.remove(parser.getValue());
		return createCondition("", trueOptions);
	}

	private String createCondition(String trigger, List<String> triggers) {
		String ret = "";
		List<String> list = new ArrayList<>(triggers);
		list.remove(trigger);
		
		for (int i = 0; i < list.size(); i++) {
			String tr = list.get(i);
			
			if (i > 0) {
				ret += "&&";
			}
			ExpressionParser parser = new ExpressionParser(tr);
			ret += "($" + parser.getId() + "!=%" + parser.getValue() + ")";
		}

		return ret;
	}

	private List<String> invert(List<String> trigger, PropertyGetter store) {
		String id = new ExpressionParser(trigger.get(0)).getId();
		List<String> ids = store.getProperty(id).getListIds();
		for (String exp : trigger) {
			ExpressionParser parser = new ExpressionParser(exp);
			ids.remove(parser.getValue());
		}
		
		List<String> ret = new ArrayList<>();	
		
		for (String id2 : ids) {
			ret.add("$" + id + "==" + "%" + id2);
		}
		return ret;
	}

	private boolean isElse(List<String> trigger) {
		return trigger.size() > 0 && trigger.get(0).equals(DependencySpec.Else);
	}

	private boolean isNotElse(List<String> trigger) {
		if (trigger.size() == 0) {
			return false;
		}
		return !trigger.get(0).equals(DependencySpec.Else);
	}

	private List<String> convertTrueCondition(String id, List<String> falseCondition, DepPropertyStore store) {
		List<String> ids = store.getProperty(id).getListIds();
		for (String exp : falseCondition) {
			ExpressionParser parser = new ExpressionParser(exp);
			ids.remove(parser.getValue());
		}
		
		List<String> ret = new ArrayList<>();
		
		return null;
	}

	public DependencySpecHolder getNewHolder() {
		return this.newHolder;
	}

}

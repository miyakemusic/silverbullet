package jp.silverbullet.dependency.alternative;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.silverbullet.dependency.DependencyExpression;
import jp.silverbullet.dependency.DependencyExpressionHolder;
import jp.silverbullet.dependency.DependencyExpressionHolderMap;
import jp.silverbullet.dependency.DependencySpec;
import jp.silverbullet.dependency.DependencySpecHolder;
import jp.silverbullet.dependency.DependencyTargetElement;
import jp.silverbullet.web.ui.PropertyGetter;

public class AlternativeDependencyGenerator {

	public DependencySpecHolder convert(DependencySpecHolder holder, PropertyGetter getter) {
		DependencySpecHolder ret = new DependencySpecHolder();
		DependecySpecController controller = new DependecySpecController(ret);
		
		new DepedencyWalkThrough(holder) {

			@Override
			protected void analyzeElse(String value, DependencyExpression dependencyExpression) {
				System.out.println(value + " " + dependencyExpression);
			}

			@Override
			protected void analyzeExpression(String id, DependencyTargetElement element, String targetListItem,
					String value, DependencyExpression expression) {

			}

			@Override
			protected void analyzeCondition(String id, DependencyTargetElement element, String targetListItem,
					Map<String, DependencyExpression> expressions, String elseValue) {
				
				if (element.equals(DependencyTargetElement.ListItemEnabled)) {
					analyzeListItemEnabled(id, element, targetListItem, expressions);
				}		
			}
			

			private void analyzeListItemEnabled(String id, DependencyTargetElement element, String targetListItem,
					Map<String, DependencyExpression> expressions) {

				Set<String> trueConditions = new HashSet<>();
				for (String value : expressions.keySet()) {
					String expression = expressions.get(value).getExpression().getExpression();
					expression = expression.replaceAll("\n", "");
					if (value.equals(DependencyExpression.True)) {
						if (expression.equals(DependencyExpression.ELSE)) {
							trueConditions.addAll(getCandidatesElse(DependencyExpression.False, getter, expressions.get(DependencyExpression.False).getExpression().getExpression()));
						}
						else {
							trueConditions.addAll(getCandidates(DependencyExpression.True, getter, expression));
						}
					}
					else if (value.equals(DependencyExpression.False)) {
						if (expression.equals(DependencyExpression.ELSE)) {
							trueConditions.addAll(getCandidatesElse(DependencyExpression.True, getter, expressions.get(DependencyExpression.True).getExpression().getExpression()));							
						}
						else {
							trueConditions.addAll(getCandidates(DependencyExpression.False, getter, expression));
						}						
					}
				}
				Map<String, Map<String, List<String>>> tmp = new HashMap<>();
				for (String condition : trueConditions) {
					IdValue idValue = new IdValue(condition);
					addTmp(tmp, idValue.getId(), "%" + idValue.getValue(), "$" + id + ".Value==%" + targetListItem);
					addTmp(tmp, id, "%" + targetListItem, condition);
				}
				setToDependencyHolder(tmp);
			}

			private List<String> getCandidatesElse(String value, PropertyGetter getter,
					String expression) {
				return new TrueConditionGenerator().getCandidatesElse(value, expression, getter);
			}

			private List<String> getCandidates(String value, PropertyGetter getter, String expression) {
				return new TrueConditionGenerator().getCandidates(value, expression, getter);
			}

			private void setToDependencyHolder(Map<String, Map<String, List<String>>> tmp) {

				for (String id : tmp.keySet()) {
					for (String value : tmp.get(id).keySet()) {
						String text = "";
						
						if (tmp.get(id).get(value).size() == 1) {
							text = tmp.get(id).get(value).get(0);
						}
						else {
							for (String expression : tmp.get(id).get(value)) {
								text += "(" + expression + ")||";
							}
							text = text.substring(0, text.length()-2);
						}
						
						controller.addAsOr(id, DependencyTargetElement.Value, DependencySpec.DefaultItem, value, text);
					//	ret.get(id).add(DependencyTargetElement.Value, DependencySpec.DefaultItem, value, text);
					}
				}
			}

			private void addTmp(Map<String, Map<String, List<String>>> tmp, String id, String value, String expression) {
			//	System.out.println(id + " " + value + " " + expression);
				if (tmp.get(id) == null) {
					tmp.put(id, new HashMap<String, List<String>>());
					tmp.get(id).put(value, new ArrayList<String>());
				}
				if (tmp.get(id).get(value) == null) {
					tmp.get(id).put(value, new ArrayList<String>());
				}
				tmp.get(id).get(value).add(expression);
			}

		};
		return ret;
	}

}
class DependecySpecController {
	private DependencySpecHolder holder;

	public DependecySpecController(DependencySpecHolder holder) {
		this.holder = holder;
	}
	
	public void addAsOr(String id, DependencyTargetElement element, String selectionId, String value, String expression) {
		DependencyExpression currentExpression = this.holder.get(id).getDependencyExpressionHolder(element, selectionId).getExpression(value);

		if (currentExpression.getExpression().getExpression().isEmpty()) {
			currentExpression.getExpression().setExpression(expression);
		}
		else {
			String currentText = currentExpression.getExpression().getExpression();
			String[] tmp = currentText.replace("||", "|").replace("&&", "&").split("[\\|&]+");
			Map<String, List<String>> map = new HashMap<String, List<String>>();
			IdValue idValueExp = new IdValue(expression);
			map.put(idValueExp.getId(), new ArrayList<String>());
			map.get(idValueExp.getId()).add(expression);
			for (String s : tmp) {
				s = trimBracket(s);
				IdValue idValue = new IdValue(s);
			
				if (!map.keySet().contains(idValue.getId())) {
					map.put(idValue.getId(), new ArrayList<String>());
				}
				map.get(idValue.getId()).add(s);
			}

			String text  = "";
			boolean bracket = map.keySet().size() >= 2;
			for (String trigger : map.keySet()) {
				if (bracket) {
					text += "(";
				}
				List<String> exps = map.get(trigger);
					
				boolean innerBracket = exps.size() >= 2;
				for (String exp : exps) {
					if (innerBracket) {
						text += "(";
					}
					text += exp;
					if (innerBracket) {
						text += ")";
					}
					text += "||";
				}
				text = text.substring(0, text.length() -2);
				if (bracket) {
					text += ")";
				}
				text += "&&";
			}
			text = text.substring(0, text.length() -2);

			currentExpression.getExpression().setExpression(text);
		}
		

	}

	private String trimBracket(String s) {
		return s.replace("(", "").replace(")", "");
	}
}
abstract class DepedencyWalkThrough {
	public DepedencyWalkThrough(DependencySpecHolder holder) {	
		for (String id : holder.getSpecs().keySet()) {
			DependencySpec spec = holder.get(id);
			for (DependencyTargetElement element : spec.getDepExpHolderMap().keySet()) {
				DependencyExpressionHolderMap map = spec.getDepExpHolderMap().get(element);
				for (String targetListItem : map.keySet()) {
					List<DependencyExpressionHolder> depnedencyExpressionHodler = map.get(targetListItem);
					for (DependencyExpressionHolder expressionHolder : depnedencyExpressionHodler) {
						Map<String, DependencyExpression> expressions = new HashMap<>();
						String elseValue = "";
						
						for (String value : expressionHolder.getExpressions().keySet()) {
							DependencyExpression expression = expressionHolder.getExpressions().get(value);
							expressions.put(value, expressionHolder.getExpressions().get(value));
							if (expression.getExpression().getExpression().equals(DependencyExpression.ELSE)) {
								elseValue = value;
							}
						}
						
						analyzeCondition(id, element, targetListItem, expressions, elseValue);
					}
				}
			}
		}	
	}

	abstract protected  void analyzeCondition(String id, DependencyTargetElement element, String targetListItem,
			Map<String, DependencyExpression> expressions, String elseValue);

	abstract protected void analyzeElse(String value, DependencyExpression dependencyExpression);
	abstract protected void analyzeExpression(String id, DependencyTargetElement element, String targetListItem, String value,
			DependencyExpression expression);

}

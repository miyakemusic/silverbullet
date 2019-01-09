package obsolute.alternative;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sun.xml.bind.v2.model.core.ID;

import jp.silverbullet.property2.ListDetailElement;
import jp.silverbullet.web.ui.PropertyGetter;
import obsolute.DependencyExpression;
import obsolute.DependencySpec;
import obsolute.DependencyTargetElement;
import obsolute.IdValue;
import obsolute.property.SvProperty;

public class TrueConditionGenerator {

	public List<String> getCandidates(String enabled, String condition, PropertyGetter getter) {
		List<String> candidates = new ArrayList<>();
		if (enabled.equals(DependencyExpression.True)) {
			if (condition.contains("||")) {
				
				String[] parts = condition.split("\\|\\|");
				if (isAllSameIds(parts)) {
					for (String part: parts) {
						if (part.contains(DependencyExpression.Equals)) {
							candidates.add(trimBracket(part));
						}
					}
				}
				else {
					
				}
			}
			else if (condition.contains("&&")) {
				String[] parts = condition.split("&&");
				if (isAllSameIds(parts)) {
					String id = new IdValue(parts[0]).getId();
					List<String> list = getSelections(id, getter);
					for (String part : parts) {
						if (part.contains(DependencyExpression.NotEquals)) {
							IdValue idValue = new IdValue(part);
							list.remove(idValue.getValue());
						}
					}	
					for (String value : list) {
						candidates.add("$" + id + ".Value" + DependencyExpression.Equals + "%" + value);
					}
				}
			}
			else {
				candidates.add(condition);
			}
		}
		else {
			if (condition.contains("||")) {
				String[] parts = condition.split("\\|\\|");
				List<String> cand = new ArrayList<>();
				for (String part : parts) {
					if (part.contains(DependencyExpression.Equals)) {
						cand = AndList(cand, calcOne(trimBracket(part), getter));
					}
				}
				candidates.addAll(cand);
			}
			else {
				candidates.addAll(calcOne(condition, getter));
			}
		}
		return candidates;
	}


	public List<String> getCandidatesElse(String value, String expression, PropertyGetter getter) {
		List<String> cand = new ArrayList<>();
		if (value.equals(DependencyExpression.True)) {
			if (expression.contains("||")) {
				String[] parts = expression.split("\\|\\|");
				for (String part : parts) {
					cand.add(trimBracket(part));
				}
			}
			else {
				cand.add(expression);
			}
		}
		else if (value.equals(DependencyExpression.False)) {
			IdValue idValue = new IdValue(expression);
//			SvProperty property = getter.getProperty(idValue.getId());
			
			if (idValue.getEvaluation().equals(DependencyExpression.Equals) ) {
//				for (ListDetailElement e : property.getListDetail()) {
//					if (idValue.getValue().equals(e.getId())) {
//						continue;
//					}
//					cand.add("$" + idValue.getId() + "." + DependencyTargetElement.Value.toString() + DependencyExpression.Equals + "%" + e.getId());
//				}		
			}
			else if (idValue.getEvaluation().equals(DependencyExpression.NotEquals)){
				cand.add(expression.replace(DependencyExpression.NotEquals, DependencyExpression.Equals));
			}
		}

		return cand;
	}
	
	private String getAlternativeOperator(String evaluation) {
		if (evaluation.equals(DependencyExpression.Equals)) {
			return DependencyExpression.NotEquals;
		}
		else if (evaluation.equals(DependencyExpression.NotEquals)) {
			return DependencyExpression.Equals;
		}
		return "";
	}


	private boolean isAllSameIds(String[] conditions) {
		Set<String> ids = new HashSet<>();
		for (String cond : conditions) {
			cond = trimBracket(cond);
			ids.add(new IdValue(cond).getId());
		}
		
		return ids.size() == 1;
	}

	private List<String> getSelections(String id, PropertyGetter getter) {
		List<String> ret = new ArrayList<>();
//		for (ListDetailElement e : getter.getProperty(id).getListDetail()) {
//			ret.add(e.getId());
//		}
		return ret;
	}

	private String trimBracket(String part) {
		return part.replace("(", "").replace(")", "");
	}

	private List<String> AndList(List<String> cand, List<String> calcOne) {
		if (cand.size() == 0) {
			return calcOne;
		}
		List<String> ret = new ArrayList<>();
		for (String c : cand) {
			if (calcOne.contains(c)) {
				ret.add(c);
			}
		}
		return ret;
	}

	private List<String> calcOne(String condition, PropertyGetter getter) {
		List<String> ret = new ArrayList<>();
		
		IdValue idValue = new IdValue(condition);
		
		if (idValue.getEvaluation().equals(DependencyExpression.Equals)) {
//			SvProperty prop = getter.getProperty(idValue.getId());
//			for (ListDetailElement e : prop.getListDetail()) {
//				if (!e.getId().equals(idValue.getValue())) {
//					ret.add("$" + idValue.getId() + ".Value" + DependencyExpression.Equals + "%" + e.getId());
//				}
				
//			}
		}
		else if (idValue.getEvaluation().equals(DependencyExpression.NotEquals)) {
			ret.add(condition.replace(DependencyExpression.NotEquals, DependencyExpression.Equals));
		}
		return ret;
	}

}

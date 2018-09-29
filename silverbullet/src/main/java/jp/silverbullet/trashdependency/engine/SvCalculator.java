package jp.silverbullet.trashdependency.engine;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jp.silverbullet.SvProperty;
import jp.silverbullet.trash.speceditor2.DependencyFormula;
import jp.silverbullet.trash.speceditor2.DependencySpecDetail;

public class SvCalculator {
	private ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");
	private SvCalculatorModel model;
	
	public SvCalculator(SvCalculatorModel model) {
		this.model = model;
	}
	
	public String calculate(String formula) {
		String replacedFormula = replaceCurrentValue(formula);

		try {
			Object result = scriptEngine.eval(replacedFormula);
			if (result == null) {
				result = 0.0;
			}
			return result.toString();
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return "ERROR";
	}

	private static String[] elements = {DependencySpecDetail.VALUE, DependencySpecDetail.MIN, DependencySpecDetail.MAX};
	
	private String replaceCurrentValue(String formula) {
		formula = formula.replace("*CALC[", "").replace("]", "");
		for (String id : model.getAllIds()) {
			for (String e : elements) {
				if (formula.contains(id + "." + e)) {
					formula = formula.replace(id + "." + e, model.getCurrentValue(id));
				}
			}
		}
		return formula;
	}
}

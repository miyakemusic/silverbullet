package jp.silverbullet.dependency.engine;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.speceditor2.DependencyFormula;

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

	private static String[] elements = {DependencyFormula.VALUE, DependencyFormula.MIN, DependencyFormula.MAX};
	
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

package jp.silverbullet.dependency;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jp.silverbullet.SvProperty;

public abstract class ExpressionCalculator {
	abstract protected SvProperty getProperty(String id);
	
	private ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");
	private IdCollector idCollector = new IdCollector();
	
	public boolean isSatisfied(String expression) {
		expression = replaceWithRealValue(expression);
		try {
		return (Boolean)getReturn(expression);
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	String replaceWithRealValue(String expression) {
		String ret = expression.replace("\n", "");
		
		for (String sel : idCollector.collectSelectionIds(expression)) {
			ret = ret.replace("%" + sel, "\"%" + sel + "\"");
		}
		
		List<String> ids = idCollector.collectIds(expression);
		for (String sid : ids) {
			String id = sid.replace("$", "");
			if (id.contains(".")) {
				id = id.split("\\.")[0];
			}
			String value = "";
			SvProperty prop = getProperty(id);
			if (prop.isListProperty()) {
				value = "\"%" + prop.getCurrentValue() + "\"";
			}
			else /*if (prop.isNumericProperty())*/ {
				value = prop.getCurrentValue();
			}
			ret = ret.replace("$" + sid, value);
		}

		return ret;
	}
	
	public String calculate(String expression) {
		String expression2 = replaceWithRealValue(expression);
		return String.valueOf(getReturn(expression2));
	}
	
	private Object getReturn(String expression) {
		try {
			scriptEngine.eval(expression);
			Object object = scriptEngine.get("ret");
			return object;
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return null;
	}
}

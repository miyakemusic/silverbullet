package jp.silverbullet.core.dependency2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jp.silverbullet.core.property2.RuntimeProperty;

public abstract class ExpressionCalculator {
	abstract protected RuntimeProperty getProperty(String id);
	
	private ScriptEngine scriptEngine = null;//new ScriptEngineManager().getEngineByName("JavaScript");
	private IdUtility idCollector = new IdUtility();

	public ExpressionCalculator() {
		System.setProperty("polyglot.js.nashorn-compat", "true");
		scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");
		if (scriptEngine == null) { // for Android
			scriptEngine = new ScriptEngineManager().getEngineByName("rhino");
		}
	}

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

	private String replaceWithRealValue(String expression) {
		 Map<String, String> mapTmpValue = new HashMap<>();
		String ret = expression.replace("\n", "");
		
		List<String> options = idCollector.sortByLength(idCollector.collectSelectionIds(expression));
		for (int i = 0; i < options.size(); i++) {
			String option = options.get(i);
			String val = getTmpOptionValue(option, mapTmpValue);
			ret = ret.replace("%" + option, val);
		}
		
		List<String> ids = idCollector.collectIds(expression);
		for (String sid : ids) {
			String id = sid.replace("$", "");
			if (id.contains(".")) {
				id = id.split("\\.")[0];
			}
			String value = "";
			RuntimeProperty prop = getProperty(id);
			if (prop.isList()) {
				value = getTmpOptionValue(prop.getCurrentValue(), mapTmpValue);//"\"%" + prop.getCurrentValue() + "\"";
			}
			else if (prop.isText()) {
				value = "'" + prop.getCurrentValue() + "'";
			}
			else /*if (prop.isNumericProperty())*/ {
				value = prop.getCurrentValue();
			}
			ret = ret.replace("$" + sid, value);
		}

		return ret;
	}
	
	private String getTmpOptionValue(String option, Map<String, String> mapTmpValue) {
		if (!mapTmpValue.keySet().contains(option)) {
			mapTmpValue.put(option, "V" + mapTmpValue.keySet().size());
		}
		return "\"" + mapTmpValue.get(option) + "\"";
	}

	public String calculate(String expression) {
		String expression2 = replaceWithRealValue(expression);
		if (expression.startsWith("ret=")) {
			return String.valueOf(getReturn(expression2));
		}
		else {
			return String.valueOf(getReturn("ret=" + expression2));
		}
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

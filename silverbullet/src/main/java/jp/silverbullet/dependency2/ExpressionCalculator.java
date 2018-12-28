package jp.silverbullet.dependency2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jp.silverbullet.property.SvProperty;
import jp.silverbullet.property2.RuntimeProperty;

public abstract class ExpressionCalculator {
	abstract protected RuntimeProperty getProperty(String id);
	
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

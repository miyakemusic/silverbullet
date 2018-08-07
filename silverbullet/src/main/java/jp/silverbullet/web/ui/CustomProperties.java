package jp.silverbullet.web.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.silverbullet.web.Pair;

public class CustomProperties {
	private static CustomProperties instance = null;
	private Map<String, List<Pair>> map = new HashMap<>();
	
	public static CustomProperties getInstance() {
		if (instance == null) {
			instance = new CustomProperties();
		}
		return instance;
	}
	
	private CustomProperties() {
		map.put(JsWidget.TAB, Arrays.asList(new Pair("id", "string")));
		map.put(JsWidget.TOGGLEBUTTON, Arrays.asList(new Pair("frame", "boolean")));
		map.put(JsWidget.GUI_DIALOG, Arrays.asList(new Pair("id", "string")));
	}

	public Map<String, List<Pair>> getMap() {
		return map;
	}

	
}

package jp.silverbullet.web.ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.silverbullet.web.Pair;

public class CustomProperties {
	public static final String GUI_ID = "gui_id";
	public static final String BACKGROUND_IMAGE = "background-image";
	public static final String BACKGROUND_COLOR = "background-color";
	public static final String OPACITY = "opacity";
	public static final String TAB_RELATION = "tab_relation_id";
	
	private static CustomProperties instance = null;
	private Map<String, List<Pair>> map = new HashMap<>();
	
	public static CustomProperties getInstance() {
		if (instance == null) {
			instance = new CustomProperties();
		}
		return instance;
	}
	
	private CustomProperties() {		
		map.put(JsWidget.TAB, Arrays.asList(new Pair(GUI_ID, "string")));
		map.put(JsWidget.TOGGLEBUTTON, Arrays.asList(new Pair("frame", "boolean")));
		map.put(JsWidget.GUI_DIALOG, Arrays.asList(new Pair("target_gui_id", "string"), new Pair("caption", "string")));
		map.put(JsWidget.PANEL, Arrays.asList(new Pair(GUI_ID, "string"), new Pair(TAB_RELATION, "string"), 
				new Pair(BACKGROUND_COLOR, "string"), new Pair(BACKGROUND_IMAGE, "string"), new Pair(OPACITY, "string")));
	}

	public Map<String, List<Pair>> getMap() {
		return map;
	}
	
}

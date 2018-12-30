package jp.silverbullet.web.ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.silverbullet.web.Pair;

public class CustomProperties {
	public static final String CAPTION = "caption";
	public static final String TARGET_GUI_ID = "target_gui_id";
	public static final String FRAME = "frame";
	public static final String GUI_ID = "gui_id";
	public static final String BACKGROUND_IMAGE = "background-image";
	public static final String BACKGROUND_COLOR = "background-color";
	public static final String OPACITY = "opacity";
	public static final String TAB_RELATION = "tab_relation_id";
	public static final String REGISTER_SHORTCUT = "register_shortcut";
	public static final String ARRAY = "array";
	public static final String COPIED = "copied";
	public static final String True = "true";
	public static final String False = "false";
	
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
		map.put(JsWidget.TOGGLEBUTTON, Arrays.asList(new Pair(FRAME, "boolean")));
		map.put(JsWidget.GUI_DIALOG, Arrays.asList(new Pair(TARGET_GUI_ID, "string"), new Pair(CAPTION, "string")));
		map.put(JsWidget.PANEL, Arrays.asList(new Pair(GUI_ID, "string"), new Pair(TAB_RELATION, "string"), 
				new Pair(BACKGROUND_COLOR, "string"), new Pair(BACKGROUND_IMAGE, "string"), new Pair(OPACITY, "string"),
				new Pair(ARRAY, "boolean"), new Pair(COPIED, "boolean")));
		map.put(JsWidget.REGISTERSHORTCUT, Arrays.asList(new Pair(REGISTER_SHORTCUT, "string")));
	}

	public Map<String, List<Pair>> getMap() {
		return map;
	}
	
}

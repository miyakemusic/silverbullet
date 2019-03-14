package jp.silverbullet.web.ui.part2;

import java.util.HashMap;
import java.util.Map;

public class WidgetIdManager {
	private long widgetIdNumber = 0;
	private Map<String, WidgetBase> map = new HashMap<>();
	
	public String createWidgetId(WidgetBase widget) {
		String divid =  "WID" + widgetIdNumber++;
		this.map.put(divid, widget);
		return divid;
	}

	public WidgetBase get(String divid) {
		return this.map.get(divid);
	}
}

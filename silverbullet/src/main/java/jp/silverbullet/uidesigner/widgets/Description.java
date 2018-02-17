package jp.silverbullet.uidesigner.widgets;

import java.util.ArrayList;
import java.util.List;

public class Description {

	public final static String WIDTH = "-width";
	public final static String HEIGHT = "-height";
	public final static String TABLE_COLUMN_WIDTH = "-table-colwidth";
	public static final String UID = "-id";
	public static final String TABS = "-tabs";
	public static final String FUNCTIONKEY = "-fkeys";
	public static final String LAYOUT = "-layout";
	public static final String TITLEVISIBLE = "-title-visible";
	public static final String ITEMS_PER_LINE = "-items-per-line";
	public static final String BUTTON_WIDTH = "-button-width";
	public static final String LIST_ICONS = "-list-icons";
	public static final String USER_CODE = "-user-code";
	public static final String TITLE_WIDTH = "-title-width";
	public static final String X = "-x-position";
	public static final String Y = "-y-position";
	public static final String TABHEADERHIGHT = "-tab-header-height";
	public static final String RELATEDID = "-related-id";
	
	private List<String> list = new ArrayList<String>();
//	private String description;

	public List<String> getList() {
		return list;
	}
	public Description(String description) {
		for (String s : description.trim().split(";")) {
			if (s.isEmpty())continue;
			list.add(s.trim());
		}
//		this.description = description;
	}
	public String getValue(String tag) {
		for (String s : list) {
			if (s.startsWith(tag)) {
				String[] tmp = s.split(":");
				if (tmp.length <= 2) {
					return tmp[1];
				}
				else {
					return s.substring(s.indexOf(":")+1, s.length());
				}
			}
		}
		return "";
	}
	public String get() {
		String ret = "";
		for (String s: list) {
			ret += s + ";";
		}
		return ret;
	}
	
	public String getSubValue(String key, String subKey) {
		String v =this.getValue(key);
		for (String s : v.split(",")) {
			String[] tmp = s.split("=");
			if (tmp[0].trim().equalsIgnoreCase(subKey.trim())) {
				return tmp[1];
			}
		}
		return "";
		
	}
	public boolean isDefined(String tag) {
		return !this.getValue(tag).isEmpty();
	}
	public void removeElement(String tag) {
		List<String> remove = new ArrayList<>();
		for (String e : list) {
			if (e.startsWith(tag + ":")) {
				remove.add(e);
			}
		}
		list.removeAll(remove);
	}
	public void update(String tag, String value) {
		this.removeElement(tag);
		this.add(tag, value);

	}
	private void add(String tag, String value) {
		this.list.add(tag + ":" + value);
	}
}

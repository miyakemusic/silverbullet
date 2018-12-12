package jp.silverbullet.dependency2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IdCollector {

	public static final String ID_SPLIT_CHARS = "[\\<>\\[\\]+/\\-=\\s();\\|!]";

	public static List<String> collectIds(String value) {
		List<String> ret = new ArrayList<>();
		if (!value.contains("$")) {
			return ret;
		}
		String[] tmp = value.split(ID_SPLIT_CHARS);
		for (String t : tmp) {
			if (t.startsWith("$")) {
				ret.add(t.replace("$", "").split("\\.")[0]);
			}
		}

		return ret;
	}

	public static List<String> collectSelectionIds(String value) {
		Set<String> ret = new HashSet<>();
		if (!value.contains("%")) {
			return new ArrayList<String>(ret);
		}
		String[] tmp = value.split(ID_SPLIT_CHARS/*"[\\<>\\[\\]+/\\-=\\s();\\|]"*/);
		for (String t : tmp) {
			if (t.startsWith("%")) {
				ret.add(t.replace("%", ""));
			}
		}

		return new ArrayList<String>(ret);
	}
	
	public static List<String> sortByLength(Collection<String> options) {
		List<String> list = new ArrayList<>(options);
		Collections.sort(list, new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				return arg1.length() - arg0.length() ;
			}
			
		});
		return list;
	}
}

package jp.silverbullet.dependency2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IdCollector {

	public static final String ID_SPLIT_CHARS = "[\\<>\\[\\]+/\\-=\\s();\\|]";

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

	public static Set<String> collectSelectionIds(String value) {
		Set<String> ret = new HashSet<>();
		if (!value.contains("%")) {
			return ret;
		}
		String[] tmp = value.split(ID_SPLIT_CHARS/*"[\\<>\\[\\]+/\\-=\\s();\\|]"*/);
		for (String t : tmp) {
			if (t.startsWith("%")) {
				ret.add(t.replace("%", ""));
			}
		}

		return ret;
	}
}

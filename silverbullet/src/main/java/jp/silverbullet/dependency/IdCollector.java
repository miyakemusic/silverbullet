package jp.silverbullet.dependency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IdCollector {

	

	public static List<String> collectIds(String value) {
		List<String> ret = new ArrayList<>();
		if (!value.contains("$")) {
			return ret;
		}
		String[] tmp = value.split(DependencyExpression.ID_SPLIT_CHARS);
		for (String t : tmp) {
			if (t.startsWith("$")) {
				ret.add(t.replace("$", ""));
			}
		}

		return ret;
	}

	public static Set<String> collectSelectionIds(String value) {
		Set<String> ret = new HashSet<>();
		if (!value.contains("%")) {
			return ret;
		}
		String[] tmp = value.split(DependencyExpression.ID_SPLIT_CHARS/*"[\\<>\\[\\]+/\\-=\\s();\\|]"*/);
		for (String t : tmp) {
			if (t.startsWith("%")) {
				ret.add(t.replace("%", ""));
			}
		}

		return ret;
	}

}

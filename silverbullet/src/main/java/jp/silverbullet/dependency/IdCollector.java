package jp.silverbullet.dependency;

import java.util.ArrayList;
import java.util.List;

public class IdCollector {

	public List<String> collectIds(String value) {
		List<String> ret = new ArrayList<>();
		if (!value.contains("$")) {
			return ret;
		}
		String[] tmp = value.split("[\\<>\\[\\]+/\\-=\\s();]");
		for (String t : tmp) {
			if (t.startsWith("$")) {
				ret.add(t.replace("$", ""));
			}
		}

		return ret;
	}

	public List<String> collectSelectionIds(String value) {
		List<String> ret = new ArrayList<>();
		if (!value.contains("%")) {
			return ret;
		}
		String[] tmp = value.split("[\\<>\\[\\]+/\\-=\\s();]");
		for (String t : tmp) {
			if (t.startsWith("%")) {
				ret.add(t.replace("%", ""));
			}
		}

		return ret;
	}
}

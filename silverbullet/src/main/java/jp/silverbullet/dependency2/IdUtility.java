package jp.silverbullet.dependency2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IdUtility {

	public static final String ID_SPLIT_CHARS = "[\\<>\\[\\]+/\\-=\\s();\\|!]";

	public static List<String> collectIds(String value) {
		Set<String> ret = new HashSet<>();
		if (!value.contains("$")) {
			return new ArrayList<String>(ret);
		}
		String[] tmp = value.split(ID_SPLIT_CHARS);
		for (String t : tmp) {
			if (t.startsWith("$")) {
				ret.add(t.replace("$", "").split("\\.")[0]);
			}
		}

		return new ArrayList<String>(ret);
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
	public static String replaceId(String expression, String prevId, String newId) {
		for (String option : IdUtility.collectSelectionIds(expression)) {
			if (isValidOption(prevId, option)) {
				expression = expression.replace(prevId, newId);
			}
		}
		if (IdUtility.collectIds(expression).contains(prevId)) {
			expression = expression.replace(prevId, newId);
		}
		return expression;
	}
	
	public static boolean isValidOption(String id, String optionId) {
		String[] array_opt = optionId.split("_");
		String[] array_id = id.split("_");
		for (int i = 0; i < array_id.length; i++) {
			if (!array_id[i].equals(array_opt[i])) {
				return false;
			}
		}
		return true;
	}
}

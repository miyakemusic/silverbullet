package jp.silverbullet.web.obsolute;

public class HtmlUtil {

	public static String wrap(String string) {
		string = string.replace("\"", "");
		return "\"" + string + "\"";
	}

	public static String setValue(String id, String value) {
		return "$('#" + id + "').val(" + HtmlUtil.wrap(value) + ");\n";
	}

	public static String setCheck(String id, String value) {
//		String val = value.equals("on") ? "true" : "false";
		return "$('#" + id + "').prop('checked'," + value + ");\n";
	}

}

package jp.silverbullet.core.register2;

public class RegisterCommon {
	static public String createInstanceName(String name) {
		return name.replaceAll("[^A-Za-z0-9]", "_").toLowerCase();
	}

	static public String createClassName(String name) {
		String ret = name.replaceAll("[^A-Za-z0-9]", "_");
		return ret.toUpperCase();
		//return StringUtils.capitalize(name.replaceAll("[^A-Za-z0-9]", "_"));
	}
}

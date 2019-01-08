package jp.silverbullet.register2;

import org.apache.commons.lang3.StringUtils;

public class RegisterCommon {
	static public String createInstanceName(String name) {
		return StringUtils.uncapitalize(name.replaceAll("[^A-Za-z0-9]", "_"));
	}

	static public String createClassName(String name) {
		String ret = name.replaceAll("[^A-Za-z0-9]", "_");
		return ret.toUpperCase();
		//return StringUtils.capitalize(name.replaceAll("[^A-Za-z0-9]", "_"));
	}
}

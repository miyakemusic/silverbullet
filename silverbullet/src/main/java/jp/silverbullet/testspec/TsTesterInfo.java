package jp.silverbullet.testspec;

import java.util.List;

public class TsTesterInfo {

	private List<String> script;
	private String message;

	public TsTesterInfo(List<String> script, String message) {
		this.script = script;
		this.message = message;
	}

	public List<String> getScript() {
		return script;
	}

	public String getMessage() {
		return message;
	}

}

package jp.silverbullet.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TestScript {
	private String scriptName;
	private List<TestItem> script = new ArrayList<>();
	public String getScriptName() {
		return scriptName;
	}
	public List<TestItem> getScript() {
		return script;
	}
	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}
	public void setScript(List<TestItem> script) {
		this.script = script;
	}
	public void add(TestItem testItem) {
		this.script.add(testItem);
		testItem.setSerial(testItem.hashCode());
	}
	public void clear() {
		this.script.clear();
	}
	public void remove(long serial) {
		for (TestItem item : script) {
			if (item.getSerial() == serial) {
				script.remove(item);
				break;
			}
		}
	}
	
}

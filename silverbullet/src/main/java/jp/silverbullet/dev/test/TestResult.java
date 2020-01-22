package jp.silverbullet.dev.test;

import java.util.HashMap;
import java.util.Map;

public class TestResult {
	private TestScript script;
	private Map<Long, TestResultItem> result = new HashMap<>();
	
	public TestResult(TestScript script) {
		this.script = script;
	}

	public void addResult(long serial, String currentValue, boolean passFail) {
		this.result.put(serial, new TestResultItem(currentValue, passFail));
	}

	public TestScript getScript() {
		return script;
	}

	public Map<Long, TestResultItem> getResult() {
		return result;
	}
	
	
}

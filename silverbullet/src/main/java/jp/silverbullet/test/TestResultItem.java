package jp.silverbullet.test;

public class TestResultItem {
	public enum PassFail {
		PASS,
		FAIL,
		NOT_TESTED
	}

//	private TestItem test;
	private PassFail passFail = PassFail.NOT_TESTED;
	private String elapsed;
	private String result;
		
	public TestResultItem(String currentValue, boolean passFail2) {
		this.result = currentValue;
		
		if (passFail2) {
			this.passFail = PassFail.PASS;
		}
		else {
			this.passFail = PassFail.FAIL;
		}
	}

//	public TestItem getTest() {
//		return test;
//	}

	public String getElapsed() {
		return elapsed;
	}

//	public void setTest(TestItem test) {
//		this.test = test;
//	}

	public void setElapsed(String elapsed) {
		this.elapsed = elapsed;
	}
	public PassFail getPassFail() {
		return passFail;
	}
	public void setPassFail(PassFail passFail) {
		this.passFail = passFail;
	}

	public String getResult() {
		return result;
	}
	
}

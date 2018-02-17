package jp.silverbullet.dependency.speceditor2;

public class SpecData {
	public SpecData(){}
	public SpecData(String condition, String answer) {
		this.condition = condition;
		this.answer = answer;
	}

	public String getAnswer() {
		return answer;
	}


	public String getCondition() {
		return condition;
	}

	private String  condition = "";
	private String  answer= "";
}
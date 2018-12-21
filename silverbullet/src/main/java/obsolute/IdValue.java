package obsolute;

public class IdValue {
	private String evaluation;
	private String id;
	private String value;

	public IdValue(String expression) {
		expression = expression.replace("(", "").replace(")", "");
		String[] eval = {DependencyExpression.Equals, DependencyExpression.NotEquals};
		for (String s : eval) {
			if (expression.contains(s)) {
				this.evaluation = s;
				this.id = expression.split(s)[0].replace("$", "").split("\\.")[0];
				this.value = expression.split(s)[1].replace("%", "");
				break;
			}
		}
	}

	public String getEvaluation() {
		return evaluation;
	}

	public String getId() {
		return id;
	}

	public String getValue() {
		return value;
	}
	
	public String toString() {
		return "$" + this.id + ".Value" + "==" + "%" + this.value;
	}
}
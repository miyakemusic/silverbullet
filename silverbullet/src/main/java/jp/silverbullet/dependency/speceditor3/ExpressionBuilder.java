package jp.silverbullet.dependency.speceditor3;

public abstract class ExpressionBuilder {
	public static final String SCRIPT = "*SCRIPT";
	public static final String EXPRESSION = "*EXPRESSION";
	private String expression = "";
	
	public ExpressionBuilder conditionIdValue(String id) {
		expression += createIdValue(id);
		return this;
	}

	public ExpressionBuilder equals() {
		expression += DependencyExpression.Equals;
		return this;
	}

	public ExpressionBuilder conditionSelectionId(String id) {
		expression += "%" + id;
		return this;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression2) {
		this.expression = expression2;
	}

	public boolean contains(String id, DependencyTargetElement dependencyTargetElement) {
		return this.expression.contains(createIdValue(id));
	}

	private String createIdValue(String id) {
		return "$" + id + "." + DependencyTargetElement.Value.toString();
	}

	public void conditionElse() {
		this.expression += DependencyExpression.ELSE;
	}

	public void anyValue() {
		this.expression += DependencyExpression.AnyValue;
	}

	public ExpressionBuilder resultValue(String targetValue) {
		targetValueAdded(targetValue);
		return this;
	}

	abstract protected void targetValueAdded(String targetValue);

	public ExpressionBuilder resultExpression(String targetValue) {
		targetValueAdded(EXPRESSION + "[" + targetValue + "]");
		return this;
	}

	public ExpressionBuilder conditionExpression(String condition) {
		this.expression += EXPRESSION + "[" + condition + "]";
		return this;
	}

	public ExpressionBuilder largerThan() {
		this.expression += DependencyExpression.LargerThan;
		return this;
	}

	public ExpressionBuilder resultIdValue(String id) {
		targetValueAdded(EXPRESSION + "[" +createIdValue(id) + "]");
		return this;
	}

	public ExpressionBuilder resultScript(String script) {
		targetValueAdded(SCRIPT + "[" + script + "]");
		return this;
	}
	
}

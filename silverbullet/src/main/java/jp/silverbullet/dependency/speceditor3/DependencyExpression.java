package jp.silverbullet.dependency.speceditor3;

public abstract class DependencyExpression {
	abstract protected void targetValueAdded(String targetValue);
	
	private ExpressionBuilder expression = new ExpressionBuilder() {
		@Override
		protected void targetValueAdded(String targetValue) {
			DependencyExpression.this.targetValueAdded(targetValue);
		}
	};
	
	public DependencyExpression(String expression) {
		this.expression.setExpression(expression);
	}
	
	public DependencyExpression() {
	
	}
	public static final String ELSE = "*Else";
	public static final String True = "True";
	public static final String False = "False";
	public static final String AnyValue = "*AnyValue";
	public static final String Equals = "==";
	public static final String NotEquals = "!=";
	public static final String LargerThan = ">";
	public static final String SmallerThan = "<";
			
	public boolean containsId(String id, DependencyTargetElement dependencyTargetElement) {
		return this.expression.contains(id, dependencyTargetElement);
	}
	public ExpressionBuilder getExpression() {
		return expression;
	}

	public boolean isEmpty() {
		return this.expression.getExpression().isEmpty();
	}
	
}

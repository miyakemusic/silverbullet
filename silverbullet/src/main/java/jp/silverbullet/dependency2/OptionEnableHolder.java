package jp.silverbullet.dependency2;

public class OptionEnableHolder {
	
	private ExpressionHolder expressions = new ExpressionHolder();
	
	public void add(String option, String enabled, String trigger) {
		expressions.add(option, new Expression(enabled, trigger));
	}

	public ExpressionHolder qualifies(String id) {
		return this.expressions.qualifies(id);
	}

	public void addValueCalculation(String option, String enabled, String trigger) {
		Expression expression = new Expression(enabled, trigger);
		expression.setValueCalculationEnabled(true);
		expressions.add(option, expression);
	}

}

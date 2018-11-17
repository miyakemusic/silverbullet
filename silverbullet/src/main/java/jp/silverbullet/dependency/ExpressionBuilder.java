package jp.silverbullet.dependency;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class ExpressionBuilder {
	public static final String SCRIPT = "*SCRIPT";
	private String expression = "";
	
	@XmlTransient
	private ExpressionBuilderListener listener;
	
	public ExpressionBuilder() {}
	public ExpressionBuilder(ExpressionBuilderListener listener) {
		this.listener = listener;
	}
	
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

	public ExpressionBuilder conditionElse() {
		this.expression += DependencyExpression.ELSE;
		return this;
	}

	public ExpressionBuilder anyValue() {
		this.expression += DependencyExpression.AnyValue;
		return this;
	}

//	public ExpressionBuilder resultValue(String targetValue) {
//		targetValueAdded(targetValue);
//		return this;
//	}

	protected void targetValueAdded(String targetValue) {
		this.listener.onTargetValueAdded(targetValue, this);
	}

	public ExpressionBuilder resultExpression(String targetValue) {
		//targetValueAdded(EXPRESSION + "[" + targetValue + "]");
		targetValueAdded(targetValue);
		return this;
	}

	public ExpressionBuilder conditionExpression(String condition) {
		//this.expression += EXPRESSION + "[" + condition + "]";
		this.expression += condition;
		return this;
	}

	public ExpressionBuilder largerThan() {
		this.expression += DependencyExpression.LargerThan;
		return this;
	}

//	public ExpressionBuilder resultIdValue(String id) {
////		targetValueAdded(EXPRESSION + "[" +createIdValue(id) + "]");
//		targetValueAdded(createIdValue(id));
//		return this;
//	}

	public ExpressionBuilder resultScript(String script) {
		targetValueAdded(SCRIPT + "[" + script + "]");
		return this;
	}
	public ExpressionBuilder or() {
		this.expression += " || ";
		return this;
	}
	
}

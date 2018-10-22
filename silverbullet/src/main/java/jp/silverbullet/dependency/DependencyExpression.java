package jp.silverbullet.dependency;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
@XmlRootElement
public class DependencyExpression {
	public static final String ID_SPLIT_CHARS = "[\\<>\\[\\]+/\\-=\\s();\\|]";
	
	@XmlTransient
	private DependencyExpressionListener listener;
	protected void targetValueAdded(String targetValue) {
		listener.onTargetValueAdded(targetValue, this);
	}
	
	private ExpressionBuilder expression = new ExpressionBuilder(new ExpressionBuilderListener() {
		@Override
		public void onTargetValueAdded(String targetValue, ExpressionBuilder expressionBuilder) {
			targetValueAdded(targetValue);
		}
	});
	private boolean confirmationRequired = false;
	
	public DependencyExpression(String expression, DependencyExpressionListener listener) {
		this.expression.setExpression(expression);
		this.listener = listener;
	}
	
	public DependencyExpression() {}
	public DependencyExpression(DependencyExpressionListener listener) {
		this.listener = listener;
	}
	
	public static final String ELSE = "*else";
	public static final String True = "true";
	public static final String False = "false";
	public static final String AnyValue = "*any";
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

	public void setExpression(ExpressionBuilder expression) {
		this.expression = expression;
	}

	public boolean isConfirmationRequired() {
		return confirmationRequired;
	}

	public void setConfirmationRequired(boolean confirmationRequired) {
		this.confirmationRequired = confirmationRequired;
	}

	public List<IdElement> getIdElement() {
		List<IdElement> ret = new ArrayList<>();
		for (String word : this.getExpression().getExpression().split(ID_SPLIT_CHARS)) {
			if (!word.startsWith("$")) {
				continue;
			}
			String[] tmp = word.split("\\.");
			ret.add(new IdElement(tmp[0].replace("$", ""), tmp[1]));
		}
		return ret;
	}

	
}

package jp.silverbullet.dependency2;

public class ExpressionParser {
	public static final String ID_SPLIT_CHARS = "[\\<>\\[\\]+/\\-=\\s();\\|]";
	private String id = "";
	private String value = "";
	private boolean equal = false;
	private boolean elseCondition;
	
	public ExpressionParser(String expression) {
		if (expression.equals(DependencySpec.Else)) {
			elseCondition = true;
			return;
		}
		id = IdCollector.collectIds(expression).get(0);
		value = IdCollector.collectSelectionIds(expression).get(0);
		
		if (expression.contains("==")) {
			equal = true;
		}
		else if (expression.contains("!=")) {
			equal = false;
		}
	}

	public String getId() {
		return id;
	}

	public String getValue() {
		return value;
	}

	public boolean isEqual() {
		return equal;
	}

	public boolean isElseCondition() {
		return elseCondition;
	}

}

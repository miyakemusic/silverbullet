package jp.silverbullet.trash.speceditor2;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DependencyFormula implements Cloneable{

	public static final String SAMEVALUE = "*SAME_VALUE*";

	public static final String ANY = "**Any**";

	public static final String EQUAL = "=";
	
	public static final String LARGER = ">";

	public static final String LARGER_EQUAL = ">=";

	public static final String SMALLER = "<";

	public static final String SMALLER_EQUAL = "<=";

	public static final String TRUE = "true";

	public static final String FALSE = "false";

	public static final String NOTEQUAL = "!=";

	public static final String OTHER = "*OTHER*";
	
	
	public void setId(String id) {
		this.id = id;
	}

	public void setElement(String element) {
		this.element = element;
	}

	public void setEvalution(String evalution) {
		this.evalution = evalution;
	}
	
	public void setRightSide(String rightSide) {
		this.rightSide = rightSide;
	}

	private String id;

	private String element;

	private String evalution;

	private String rightSide;

	private String valueMatched = "";
	
	public String getValueMatched() {
		return valueMatched;
	}

	public void setValueMatched(String valueMatched) {
		this.valueMatched = valueMatched;
	}

	public DependencyFormula() {}
		
	public DependencyFormula(String id, String element,
			String evaluation, String rightSide) {
		this.id = id;
		this.element = element;
		this.evalution = evaluation;
		this.rightSide = rightSide;
	}

	public String getId() {
		return id;
	}

	public String getElement() {
		return element;
	}

	public String getEvalution() {
		return evalution;
	}

	public String getRightSide() {
		return rightSide;
	}

	public String getSample() {
		return this.valueMatched + " : " + id + "." + element + " " + evalution + " " + rightSide;
	}

	@Override
	public DependencyFormula clone() {
		try {
			DependencyFormula ret =  (DependencyFormula)super.clone();
			return ret;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isLarger(String value1, String value2) {
		return Double.valueOf(value1) > Double.valueOf(value2);
	}

	public String getTextFormula() {
		return id + "." + element + " " + evalution + " " + rightSide;
	}

	public static String getOppositeValue(String value2) {
		if (value2.equals(DependencyFormula.TRUE)) {
			return DependencyFormula.FALSE;
		}
		else if (value2.equals(DependencyFormula.FALSE)) {
			return DependencyFormula.TRUE;
		}
		return "";
	}

}

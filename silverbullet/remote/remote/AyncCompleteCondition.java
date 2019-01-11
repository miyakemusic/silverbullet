package jp.silverbullet.remote;

public class AyncCompleteCondition {
	private String id = "ID_SERVER_STATE";
	private String element = "Value";
	private String eval = "=";
	private String value = "ID_SERVER_STATE_IDLE";
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getElement() {
		return element;
	}
	public void setElement(String element) {
		this.element = element;
	}
	public String getEval() {
		return eval;
	}
	public void setEval(String eval) {
		this.eval = eval;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return this.id + "." + this.element + this.eval + this.value;
	}
	
}

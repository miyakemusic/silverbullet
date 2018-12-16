package jp.silverbullet.dependency2;

public class ChangedItemValue {

	private String element;
	private String value;

	public ChangedItemValue(String element, String value2) {
		this.element = element;
		this.value = value2;
	}

	public String getElement() {
		return element;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return element.toString() + ":" + value;
	}

	
}

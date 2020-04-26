package jp.silverbullet.core.dependency2;

public class ChangedItemValue {

	private String element;
	private String value;
	public boolean blockPropagation = false;
	
	public ChangedItemValue() {}
	
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

	public boolean isBlockPropagation() {
		return blockPropagation;
	}

	public void setBlockPropagation(boolean blockPropagation) {
		this.blockPropagation = blockPropagation;
	}

	public void setElement(String element) {
		this.element = element;
	}

	public void setValue(String value) {
		this.value = value;
	}

	
}

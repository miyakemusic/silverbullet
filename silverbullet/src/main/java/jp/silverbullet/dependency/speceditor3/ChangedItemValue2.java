package jp.silverbullet.dependency.speceditor3;

public class ChangedItemValue2 {

	private DependencyTargetElement element;
	private String value;

	public ChangedItemValue2(DependencyTargetElement element, String value2) {
		this.element = element;
		this.value = value2;
	}

	public DependencyTargetElement getElement() {
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

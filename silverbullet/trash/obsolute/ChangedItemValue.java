package obsolute;

public class ChangedItemValue {

	private DependencyTargetElement element;
	private String value;

	public ChangedItemValue(DependencyTargetElement element, String value2) {
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

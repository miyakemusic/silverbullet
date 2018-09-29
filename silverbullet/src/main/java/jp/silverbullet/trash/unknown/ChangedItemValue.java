package jp.silverbullet.trash.unknown;

public class ChangedItemValue {
	public ChangedItemValue(String element, String value) {
		this.element = element;
		this.value = value;
	}
	public String element;
	public String value;
	
	
	public String toString() {
		return element + "=" + value;
		
	}
}

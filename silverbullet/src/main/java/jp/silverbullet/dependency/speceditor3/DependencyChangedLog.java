package jp.silverbullet.dependency.speceditor3;

public class DependencyChangedLog {

	private String id;
	private DependencyTargetElement element;
	private String value;

	public DependencyChangedLog(String id, DependencyTargetElement element, String value) {
		this.id = id;
		this.element = element;
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public DependencyTargetElement getElement() {
		return element;
	}

	public String getValue() {
		return value;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setElement(DependencyTargetElement element) {
		this.element = element;
	}

	public void setValue(String value) {
		this.value = value;
	}

}

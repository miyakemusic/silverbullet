package jp.silverbullet.dependency2;

public class DependencyLink {
	private String id;
	private String targetElement;
	
	public DependencyLink(String id2, String targetElement2) {
		this.id = id2;
		this.targetElement = targetElement2;
	}

	public String getId() {
		return this.id;
	}

	public String getTargetElement() {
		return targetElement;
	}
	
}

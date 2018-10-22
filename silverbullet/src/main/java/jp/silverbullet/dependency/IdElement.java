package jp.silverbullet.dependency;

public class IdElement {
	public IdElement(String id, String element) {
		this.id = id;
		this.element = element;
	}
	public String id;
	public String element;
	
	@Override
	public boolean equals(Object arg0) {
		IdElement target = (IdElement)arg0;
		return id.equals(target.id) && element.equals(target.element);
	}
}
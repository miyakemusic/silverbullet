package jp.silverbullet.dev.dependency2.design;

public class RestrictionMatrixElement {
	
	public RestrictionMatrixElement() {};
	
	public RestrictionMatrixElement(boolean b, String string) {
		this.enabled = b;
		this.condition = string;
	}
	public boolean enabled = false;
	public String condition = "";
}

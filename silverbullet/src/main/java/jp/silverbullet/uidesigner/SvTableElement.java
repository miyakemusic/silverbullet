package jp.silverbullet.uidesigner;

public class SvTableElement {
	public enum Status {
		Pass,
		Fail,
		Nothing
	}

	private String value = "";
	private Status status = Status.Nothing;
	
	public SvTableElement(String value, Status status) {
		this.value = value;
		this.status = status;
	}

	public SvTableElement(String value) {
		this.value = value;
	}
	
	public SvTableElement() {
		
	}

	public String getValue() {
		return value;
	}

	public Status getStatus() {
		return status;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
}

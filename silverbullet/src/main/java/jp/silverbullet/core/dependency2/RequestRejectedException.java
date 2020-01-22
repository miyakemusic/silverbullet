package jp.silverbullet.core.dependency2;

public class RequestRejectedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String OUT_OF_RANGE = "Out Of Range";
	private Id target;
	private Id source;

	public RequestRejectedException(Id targetId, String message) {
		super(message);
		this.target = targetId;
	}

	public void setSource(Id id) {
		this.source = id;
	}

	public Id getTarget() {
		return target;
	}

	public Id getSource() {
		return source;
	}


}

package jp.silverbullet.dependency2;

public class RequestRejectedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String OUT_OF_RANGE = "Out Of Range";

	public RequestRejectedException(String message) {
		super(message);
	}

}

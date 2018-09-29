package jp.silverbullet.dependency;

public class RequestRejectedException extends Exception {

	public static final String OUT_OF_RANGE = "Out Of Range";

	public RequestRejectedException(String message) {
		super(message);
	}

}

package jp.silverbullet.remote.engine;

public class RemoteError {
	private int errorCode;
	private String message = "";
	public RemoteError(int code, String message) {
		this.errorCode = code;
		this.message = message;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}

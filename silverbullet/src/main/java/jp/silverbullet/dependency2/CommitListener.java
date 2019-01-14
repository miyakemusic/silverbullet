package jp.silverbullet.dependency2;

public interface CommitListener {

	public enum Reply {
		Accept,
		Reject,
		Pend
	}

	public Reply confirm(String message);

}

package jp.silverbullet.dependency2;

public interface CommitListener {

	public enum Reply {
		Accept,
		Reject,
		Pending
	}

	public Reply confirm(String message);

}

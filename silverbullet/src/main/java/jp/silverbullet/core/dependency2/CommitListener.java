package jp.silverbullet.core.dependency2;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CommitListener {

	public enum Reply {
		Accept,
		Reject,
		Pend
	}

	public Reply confirm(Set<IdValue> message);

}

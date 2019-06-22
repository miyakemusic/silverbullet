package jp.silverbullet.dependency2;

import java.util.List;
import java.util.Map;

public interface CommitListener {

	public enum Reply {
		Accept,
		Reject,
		Pend
	}

	public Reply confirm(Map<String, List<ChangedItemValue>> map);

}

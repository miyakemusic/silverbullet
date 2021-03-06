package jp.silverbullet.core.dependency2;

import java.util.List;
import java.util.Map;

public interface DependencyListener {

	boolean confirm(String history);

	void onResult(Map<String, List<ChangedItemValue>> changedHistory);

	void onCompleted(String message);

	void onStart(Id id, String value);
	
	void onRejected(Id id, String message);

	void onProgress(List<String> log);

}

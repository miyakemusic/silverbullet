package jp.silverbullet.dependency;

import java.util.List;
import java.util.Map;

public interface DependencyListener {

	boolean confirm(String history);

	void onResult(Map<String, List<ChangedItemValue>> changedHistory);

	void onCompleted(String message);

}

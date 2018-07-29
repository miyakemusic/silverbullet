package jp.silverbullet.dependency.engine;

import java.util.List;
import java.util.Map;

import jp.silverbullet.ChangedItemValue;

public interface DependencyListener {

	boolean confirm(String history);

	void onResult(Map<String, List<ChangedItemValue>> changedHistory);

	void onCompleted(String message);

}

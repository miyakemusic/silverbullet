package jp.silverbullet.dependency;

import java.util.List;
import java.util.Map;

import jp.silverbullet.trash.unknown.ChangedItemValue;

public interface DependencyListener {

	boolean confirm(String history);

	void onResult(Map<String, List<ChangedItemValue>> changedHistory);

	void onCompleted(String message);

}
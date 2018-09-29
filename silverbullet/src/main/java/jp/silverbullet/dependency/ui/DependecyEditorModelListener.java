package jp.silverbullet.dependency.ui;

import jp.silverbullet.dependency.DependencyTargetElement;

public interface DependecyEditorModelListener {

	void onSpecUpdate();
	void onSelectionChanged(String id);
	void onRequestAdd(String id, DependencyTargetElement dependencyTargetElement, String selectionId);
}

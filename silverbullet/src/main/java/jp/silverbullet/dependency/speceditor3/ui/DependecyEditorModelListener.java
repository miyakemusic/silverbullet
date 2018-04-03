package jp.silverbullet.dependency.speceditor3.ui;

import jp.silverbullet.dependency.speceditor3.DependencyTargetElement;

public interface DependecyEditorModelListener {

	void onSpecUpdate();
	void onSelectionChanged(String id);
	void onRequestAdd(String id, DependencyTargetElement dependencyTargetElement, String selectionId);
}

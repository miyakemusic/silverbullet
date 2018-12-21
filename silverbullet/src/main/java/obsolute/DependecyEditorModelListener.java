package obsolute;

public interface DependecyEditorModelListener {

	void onSpecUpdate();
	void onSelectionChanged(String id);
	void onRequestAdd(String id, DependencyTargetElement dependencyTargetElement, String selectionId);
}

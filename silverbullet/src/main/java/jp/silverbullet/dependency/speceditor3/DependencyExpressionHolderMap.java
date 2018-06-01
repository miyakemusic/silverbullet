package jp.silverbullet.dependency.speceditor3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class DependencyExpressionHolderMap {
	private HashMap<String, DependencyExpressionHolderList> dependencyExpressionHolderMap = new HashMap<>();

	public HashMap<String, DependencyExpressionHolderList> getDependencyExpressionHolderMap() {
		return dependencyExpressionHolderMap;
	}

	public void setDependencyExpressionHolderMap(
			HashMap<String, DependencyExpressionHolderList> dependencyExpressionHolderMap) {
		this.dependencyExpressionHolderMap = dependencyExpressionHolderMap;
	}

	public boolean containsKey(String targetListItem) {
		return dependencyExpressionHolderMap.containsKey(targetListItem);
	}

	public void put(String targetListItem, ArrayList<DependencyExpressionHolder> arrayList) {
		dependencyExpressionHolderMap.put(targetListItem, new DependencyExpressionHolderList(arrayList));
	}

	public List<DependencyExpressionHolder> get(String targetListItem) {
		return dependencyExpressionHolderMap.get(targetListItem).getDependencyExpressionHolders();
	}

	public Set<String> keySet() {
		return dependencyExpressionHolderMap.keySet();
	}

	public void remove(String key) {
		this.dependencyExpressionHolderMap.remove(key);
	}

	public boolean isEmpty() {
		return dependencyExpressionHolderMap.isEmpty();
	}
	
}

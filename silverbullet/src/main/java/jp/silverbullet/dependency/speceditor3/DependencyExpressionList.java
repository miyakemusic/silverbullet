package jp.silverbullet.dependency.speceditor3;

import java.util.ArrayList;

public class DependencyExpressionList {
	private ArrayList<DependencyExpression> dependencyExpressions = new ArrayList<>();

	public ArrayList<DependencyExpression> getDependencyExpressions() {
		return dependencyExpressions;
	}

	public void setDependencyExpressions(ArrayList<DependencyExpression> dependencyExpressions) {
		this.dependencyExpressions = dependencyExpressions;
	}

	public boolean remove(DependencyExpression pointer) {
		return dependencyExpressions.remove(pointer);
	}
	
}

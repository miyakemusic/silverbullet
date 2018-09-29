package jp.silverbullet.dependency;

public interface DependencyExpressionListener {

	void onTargetValueAdded(String targetValue, DependencyExpression dependencyExpression);

}

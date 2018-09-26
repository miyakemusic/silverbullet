package jp.silverbullet.dependency.engine;

public interface DependencyInterface {

	void requestChange(String id, String value) throws RequestRejectedException;

	void addDependencyListener(DependencyListener dependencyListener);
	
}

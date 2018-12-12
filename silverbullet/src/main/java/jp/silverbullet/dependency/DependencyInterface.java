package jp.silverbullet.dependency;

import jp.silverbullet.dependency2.CommitListener;

public interface DependencyInterface {

	void requestChange(String id, String value) throws RequestRejectedException;

	void addDependencyListener(DependencyListener dependencyListener);

	void requestChange(String id, Integer index, String value, CommitListener commitListener) throws RequestRejectedException;

	void requestChange(String id, Integer index, String value) throws RequestRejectedException;

	
}

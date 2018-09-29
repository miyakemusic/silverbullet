package jp.silverbullet.remote.engine;

import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.DependencyInterface;
import jp.silverbullet.remote.SvTexHolder;

public interface RemoteServerModel {

	SvTexHolder getTexHolder();

	DependencyInterface getDependency();

	SvProperty getProperty(String id);

}

package jp.silverbullet.remote.engine;

import jp.silverbullet.property.SvProperty;
import jp.silverbullet.remote.SvTexHolder;
import obsolute.DependencyInterface;

public interface RemoteServerModel {

	SvTexHolder getTexHolder();

//	DependencyInterface getDependency();

	SvProperty getProperty(String id);

}

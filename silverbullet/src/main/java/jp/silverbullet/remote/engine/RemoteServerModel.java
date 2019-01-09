package jp.silverbullet.remote.engine;

import jp.silverbullet.remote.SvTexHolder;
import obsolute.DependencyInterface;
import obsolute.property.SvProperty;

public interface RemoteServerModel {

	SvTexHolder getTexHolder();

//	DependencyInterface getDependency();

	SvProperty getProperty(String id);

}

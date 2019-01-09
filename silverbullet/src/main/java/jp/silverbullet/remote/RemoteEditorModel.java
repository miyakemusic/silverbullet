package jp.silverbullet.remote;

import java.util.List;

import jp.silverbullet.handlers.HandlerProperty;
import obsolute.property.PropertyHolder;
import obsolute.property.SvProperty;

public interface RemoteEditorModel {

	List<SvProperty> getAllProperties();

	PropertyHolder getPropertyHolder();

	List<SvProperty> getProperties(List<String> ids);

	SvTexHolder getTexHolder();

	List<HandlerProperty> getHandlers();

	void remove(SvTex selectedItem);

}

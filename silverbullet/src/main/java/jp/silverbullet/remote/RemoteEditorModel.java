package jp.silverbullet.remote;

import java.util.List;

import jp.silverbullet.property.PropertyHolder;
import jp.silverbullet.property.SvProperty;
import jp.silverbullet.handlers.HandlerProperty;

public interface RemoteEditorModel {

	List<SvProperty> getAllProperties();

	PropertyHolder getPropertyHolder();

	List<SvProperty> getProperties(List<String> ids);

	SvTexHolder getTexHolder();

	List<HandlerProperty> getHandlers();

	void remove(SvTex selectedItem);

}

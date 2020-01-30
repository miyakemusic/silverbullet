package jp.silverbullet.swing;

import jp.silverbullet.core.ui.UiProperty;

public interface UiModel {

	UiProperty getUiProperty(String id, String extension);
	UiProperty getUiProperty(String id);
	void addListener(String id, UiModelListener uiModelListener);
	void setValue(String id, String value);

}

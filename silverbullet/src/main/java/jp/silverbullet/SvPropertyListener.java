package jp.silverbullet;

public interface SvPropertyListener {

	void onValueChanged(String id, String value);

	void onEnableChanged(String id, boolean b);

	enum Flag {
		MAX,
		MIN,
		LIST_ELEMENT_PRESENTATION
	}
	void onFlagChanged(String id, Flag flag);

	void onVisibleChanged(String id, Boolean b);

	void onListMaskChanged(String id, String string);

	void onTitleChanged(String id, String title);

}

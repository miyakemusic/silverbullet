package jp.silverbullet;

public interface SvPropertyListener {

	void onValueChanged(String id, int index, String value);

	void onEnableChanged(String id, int index, boolean b);

	enum Flag {
		MAX,
		MIN,
		LIST_ELEMENT_PRESENTATION
	}
	void onFlagChanged(String id, int index, Flag flag);

	void onVisibleChanged(String id, int index, Boolean b);

	void onListMaskChanged(String id, int index, String string);

	void onTitleChanged(String id, int index, String title);

}

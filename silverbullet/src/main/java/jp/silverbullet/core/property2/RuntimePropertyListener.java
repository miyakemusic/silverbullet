package jp.silverbullet.core.property2;

public interface RuntimePropertyListener {

	void onValueChange(String id, int index, String value);

	void onEnableChange(String id, int index, boolean b);

	enum Flag {
		MAX,
		MIN,
		LIST_ELEMENT_PRESENTATION, 
		SIZE, UNIT
	}
	void onFlagChange(String id, int index, Flag flag);

	void onListMaskChange(String id, int index, String optionId, boolean mask);

	void onTitleChange(String id, int index, String title);


}

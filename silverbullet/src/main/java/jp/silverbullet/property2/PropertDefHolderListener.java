package jp.silverbullet.property2;

public interface PropertDefHolderListener {

	void onChange(String id, String field, Object value, Object prevValue);

	void onAdd(String id);

	void onRemove(String id, String replacedId);

}

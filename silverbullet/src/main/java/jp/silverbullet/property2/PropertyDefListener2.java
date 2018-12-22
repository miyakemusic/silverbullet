package jp.silverbullet.property2;

public interface PropertyDefListener2 {

	void onIdChanged(String newId, String oldId);

	void onOptionAdded(String id, String optionId, String title2, String comment2);

	void onParamChange(String id, Object value, String fieldName);

	void onTypeChange(String id, PropertyType2 type2);

	void onOptionRemove(String id, String optionId);

}

package jp.silverbullet.property;

public interface PropertyHolderListener {

	void onAdded(PropertyDef newProperty);

	void onRemoved(PropertyDef property);

	void onPropertyUpdated(PropertyDef newPropertyDef);

	void onIdChanged(String oldId, String newId);

}

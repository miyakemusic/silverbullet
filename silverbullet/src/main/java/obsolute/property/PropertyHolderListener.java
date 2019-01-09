package obsolute.property;

public interface PropertyHolderListener {

	void onAdded(PropertyDef newProperty);

	void onRemoved(PropertyDef property);

	void onPropertyUpdated(PropertyDef newPropertyDef);

	boolean onIdChanged(String oldId, String newId);

}

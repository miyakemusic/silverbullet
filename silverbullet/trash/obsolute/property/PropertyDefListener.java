package obsolute.property;

public interface PropertyDefListener {

	void onChanged(PropertyDef propertyDef);

	void onIdChanged(String oldId, String newId);

}

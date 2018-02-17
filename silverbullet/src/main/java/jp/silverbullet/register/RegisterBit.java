package jp.silverbullet.register;

public class RegisterBit {
	public enum ReadWriteType {
		RO,
		WO,
		RW,
		UNUSED
	}
	private String name;
	private String bit;
	private ReadWriteType type;
	private String description = "";
	private String definition = "";
	private String propertyFormula = "";
	public RegisterBit(){}
	
	public RegisterBit(String name, int bitFrom, int bitTo, ReadWriteType type, String description, String definition2) {
		super();
		this.bit = "[" + bitTo + ":" + bitFrom + "]";
		this.type = type;
		this.description = description;
		this.name = name;
		this.definition = definition2;
	}

	public ReadWriteType getType() {
		return type;
	}

	public void setType(ReadWriteType type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description.replace("\n", "").replace(";", ";\n");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBit() {
		return bit;
	}

	public void setBit(String bit) {
		this.bit = bit;
	}

	public String getPropertyFormula() {
		return propertyFormula;
	}

	public void setPropertyFormula(String propertyFormula) {
		this.propertyFormula = propertyFormula;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition.replace("\n", "").replace(";", ";\n");
	}

	@Override
	public String toString() {
		return this.bit + "\t" + this.name + "\t" + this.description;
	}

	public boolean isReadEnabled() {
		return type.equals(ReadWriteType.RO) || type.equals(ReadWriteType.RW);
	}
	
	public boolean isWriteEnabled() {
		return type.equals(ReadWriteType.WO) || type.equals(ReadWriteType.RW);
	}

	public boolean isSingle() {
		return !this.bit.contains(":");
	}
}

package jp.silverbullet.register;

public class RegisterBit {
	public enum ReadWriteType {
		RO, // Read Only
		WO, // Write Only
		RW, // Read / Write
		RC, // Read and clear
		UNUSED
	}
	private String name;
	private String bit;
	private ReadWriteType type;
	private String description = "";
	private String definition = "";
	private String propertyFormula = "";
	private int size;
	public RegisterBit(){}
	
	public RegisterBit(String name, int bitFrom, int bitTo, ReadWriteType type, String description, String definition2) {
		super();
//	this.bit = "[" + bitTo + ":" + bitFrom + "]";
		this.bit = bitTo + ":" + bitFrom;
		this.type = type;
		this.description = description;
		this.name = name;
		this.definition = definition2;
		calcSize();
	}

	public void calcSize() {
		if (this.bit.contains(":")) {
			this.size = Integer.valueOf(this.bit.split(":")[0]) - Integer.valueOf(this.bit.split(":")[1])+1; 
		}
		else {
			this.size = 1;
		}
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
		calcSize();
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
		return type.equals(ReadWriteType.RO) || type.equals(ReadWriteType.RW) || type.equals(ReadWriteType.RC);
	}
	
	public boolean isWriteEnabled() {
		return type.equals(ReadWriteType.WO) || type.equals(ReadWriteType.RW);
	}

	public boolean isSingle() {
		return !this.bit.contains(":");
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
		int startBit = 0;
		if (this.bit.contains(":")) {
			startBit = Integer.valueOf(this.bit.split(":")[1]);
		}
		else {
			startBit = Integer.valueOf(bit);
		}
		int endBit = startBit + size - 1;
		if (size == 1) {
			this.bit = String.valueOf(startBit);
		}
		else {
			this.bit = endBit + ":" + startBit;
		}
	}

	public int getStartBit() {
		return getBitRange(1);
	}
	
	public int getEndBit() {
		return getBitRange(0);
	}
	
	private int getBitRange(int index) {
		String startBit;
		if (this.bit.contains(":")) {
			startBit = this.bit.split(":")[index];
		}
		else {
			startBit = this.bit;
		}
		return Integer.valueOf(startBit);
	}
}

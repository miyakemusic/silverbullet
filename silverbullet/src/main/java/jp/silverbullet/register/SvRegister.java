package jp.silverbullet.register;

public class SvRegister {

	private String name;
	private String description;
	private String address;
	private RegisterBitArray bits = new RegisterBitArray();
	
	public SvRegister() {
		
	}
	
	public SvRegister(String name2, String description2, String address2) {
		this.name = name2;
		this.description = description2;
		this.address = address2;
	}
	
	public void addBit(String name, int from, int to, RegisterBit.ReadWriteType rw, String description, String definition) {
		this.bits.add(new RegisterBit(name, from, to, rw, description, definition));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public RegisterBitArray getBits() {
		return bits;
	}

	public void setBits(RegisterBitArray bits) {
		this.bits = bits;
	}

	public boolean isBlock() {
		return this.address.contains("-");
	}
	
	
}

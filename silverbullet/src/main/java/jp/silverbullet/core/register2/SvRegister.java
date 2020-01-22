package jp.silverbullet.core.register2;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jp.silverbullet.core.register2.RegisterBit.ReadWriteType;

public class SvRegister {
	
	private String name = "";
	private String description;
	private String address;
	private Set<SvRegisterListener> listeners = new HashSet<>();
	
	private RegisterBitArray bits = new RegisterBitArray();
	private SvRegisterInterface registerInterface;
	
	public SvRegister() {
		
	}
	
	@JsonIgnore
	public void setRegisterInterface(SvRegisterInterface registerInterface) {
		this.registerInterface = registerInterface;
		this.bits.setRegisterInterface(this.registerInterface);
	}
	
	public SvRegister(String name2, String description2, String address2) {
		this.name = name2;
		this.description = description2;
		this.address = address2;
	}
	
	public void addBit(String name, int from, int to, RegisterBit.ReadWriteType rw, String description, String definition) {
		RegisterBit bit = new RegisterBit(name, from, to, rw, description, definition);
		this.bits.add(bit);
	}

	public String getName() {
		return name;
	}

	public boolean setName(String name) {
		if (this.registerInterface == null || !registerInterface.conflictsName(name, this)) {
			this.name = name;
			return true;
		}
		return false;
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

	public boolean setAddress(String address) {
		if (!address.startsWith("0x")) {
			return false;
		}
		String prev = this.address;
		this.address = address;
		if (prev != null && listeners != null) {
			this.listeners.forEach(listener -> listener.onAddressChange(this, this.address, prev));
		}
		return true;
	}

	public RegisterBitArray getBits() {
		return bits;
	}

	public void setBits(RegisterBitArray bits) {
		this.bits = bits;
	}

	@JsonIgnore
	public boolean isBlock() {
		return this.address.contains("-");
	}

	public void addBit(String name, ReadWriteType rw, String description2, String definition2) {
		this.bits.add(name, rw, description2, definition2);
	}

	public SvRegister newBit(String bitName, int startBit, int size, ReadWriteType rw, String description2) {
		this.addBit(bitName, startBit, startBit + size - 1, rw, description2, "");
		return this;
	}
	
	@JsonIgnore
	public long getDecAddress() {
		String hexAddress = "";
		if (this.getAddress().contains("-")) {
			hexAddress = this.getAddress().split("-")[0];
		}
		else {
			hexAddress = this.getAddress();
		}
		return Long.parseLong(hexAddress.replace("0x", ""), 16);
	}

	@JsonIgnore
	public void setAddressDec(long addressDec) {
		this.setAddress(RegisterSpecHolder.toHexAddress(addressDec));

	}
	
	public void setAddressDec(long from, long to) {
		this.setAddress(RegisterSpecHolder.toHexAddress(from, to));		
	}	
	
	public RegisterBit getBit(String bitName) {
		return this.getBits().get(bitName);
	}

	public void removeListener(SvRegisterListener listener) {
		this.listeners.remove(listener);
	}

	public void addListener(SvRegisterListener listener) {
		this.listeners.add(listener);
	}

	public void addBit() {
		this.getBits().add();
	}


	
}

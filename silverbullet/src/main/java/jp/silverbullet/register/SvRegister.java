package jp.silverbullet.register;

import java.util.HashSet;
import java.util.Set;

import jp.silverbullet.register.RegisterBit.ReadWriteType;
import jp.silverbullet.register.UserRuntimeRegisterHolderForTest.Reg1;
import jp.silverbullet.register2.SvRegisterListener;

public abstract class SvRegister {
	abstract protected int getRegisterWidth();
	
	private String name = "";
	private String description;
	private String address;
	private Set<SvRegisterListener> listeners = new HashSet<>();
	
	private RegisterBitArray bits = new RegisterBitArray() {
		@Override
		protected int getRegisterWidth() {
			return SvRegister.this.getRegisterWidth();
		}
	};
	
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

	public boolean setName(String name) {
		if (!conflictsName(name, this)) {
			this.name = name;
			return true;
		}
		return false;
	}

	abstract protected boolean conflictsName(String name2, SvRegister svRegister);

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAddress() {
		return address;
	}

	public void setAddressHex(String address) {
		String prev = this.address;
		this.address = address;
		if (prev != null) {
			this.listeners.forEach(listener -> listener.onAddressChange(this, this.address, prev));
		}
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

	public void addBit(String name, ReadWriteType rw, String description2, String definition2) {
		this.bits.add(name, rw, description2, definition2);
	}

	public SvRegister newBit(String bitName, int startBit, int size, ReadWriteType rw, String description2) {
		this.addBit(bitName, startBit, startBit + size - 1, rw, description2, "");
		return this;
	}
	
	public int getDecAddress() {
		String hexAddress = "";
		if (this.getAddress().contains("-")) {
			hexAddress = this.getAddress().split("-")[0];
		}
		else {
			hexAddress = this.getAddress();
		}
		return Integer.parseInt(hexAddress.replace("0x", ""), 16);
	}

	public void setAddress(long addressDec) {
		this.setAddressHex(RegisterSpecHolder.toHexAddress(addressDec));

	}
	
	public void setAddress(long from, long to) {
		this.setAddressHex(RegisterSpecHolder.toHexAddress(from, to));		
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

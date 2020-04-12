package jp.silverbullet.core.register2;

import java.util.ArrayList;
import java.util.List;

public class RegisterUpdates {
	public static final String INTERRUPT = "@Interrupt@";
	private int address;
	private String name;
	private List<BitUpdates> bits = new ArrayList<>();
	public int getAddress() {
		return address;
	}
	public String getName() {
		return name;
	}
	public List<BitUpdates> getBits() {
		return bits;
	}
	public void setAddress(int address) {
		this.address = address;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setBits(List<BitUpdates> bits) {
		this.bits = bits;
	}
	public void addBit(BitUpdates bit) {
		this.bits.add(bit);
	}
	
	
}

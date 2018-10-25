package jp.silverbullet.register;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.silverbullet.register.RegisterBit.ReadWriteType;

public class RegisterBitArray {
	private BitComparator comparator = new BitComparator();
	private List<RegisterBit> bits = new ArrayList<RegisterBit>();
	public int register_width = 32;
	
	public RegisterBitArray() {
		
	}
	public RegisterBitArray(int registerWidth) {
		this.register_width = registerWidth;
	}
	
	public List<RegisterBit> getBits() {
		return bits;
	}

	public void setBits(List<RegisterBit> bits) {
		this.bits = bits;
	}

	public void add(RegisterBit registerBit) {
		this.bits.add(registerBit);
	}

	@Override
	public String toString() {
		String ret = "";
		for (RegisterBit bit : this.bits) {
			ret += bit.getName() + "\t" + bit.getType().toString() + "\t" + 
		bit + "\t" + bit.getDescription() + "\n";
		}
		return ret;
	}

	public void remove(int bitRow) {
		this.bits.remove(bitRow);
	}
	
	public void sort() {
		Collections.sort(this.bits, comparator);
	}

	public void removeAll(List<Integer> indexes) {
		for (int i : indexes) {
			this.bits.remove(i);
		}
	}

	public void add(String name, ReadWriteType rw, String description2, String definition2) {
		int startBit = 0;
		int endBit = register_width - 1;
		if (this.bits.size() > 0) {
			// Searches vacant bits
			for (int i = this.bits.size()-1; i >= 0; i--) {
				RegisterBit bit = this.bits.get(i);
				
				if (this.getFromBit(bit.getBit()) > startBit) {
					endBit = this.getFromBit(bit.getBit())-1;
				}
				else {
					startBit = this.getToBit(bit.getBit()) + 1;
				}
			}
			//startBit = this.getToBit(this.bits.get(this.bits.size()-1).getBit());
		}
				
		this.add(new RegisterBit(name, startBit, endBit, ReadWriteType.RW, description2, definition2));
		this.sort();
	}
	
	private int getFromBit(String bit) {
		if (bit.contains(":")) {
			return Integer.valueOf(bit.split(":")[1]);
		}
		else {
			return Integer.valueOf(bit);
		}
	}
	
	private int getToBit(String bit) {
		return Integer.valueOf(bit.split(":")[0]);
	}

	public RegisterBit get(String bitName) {
		for (RegisterBit bit: this.bits) {
			if (bit.getName().equals(bitName)) {
				return bit;
			}
		}
		return null;
	}

	public RegisterBit getRegisterBit(int i) {
		for (RegisterBit bit: this.bits) {
			if (bit.getStartBit() <= i && bit.getEndBit() >= i) {
				return bit;
			}
		}
		return null;
	}
}

class BitComparator implements Comparator<RegisterBit> {

	@Override
	public int compare(RegisterBit arg0, RegisterBit arg1) {
		return getBit(arg1.getBit()) - getBit(arg0.getBit());
	}

	private int getBit(String bit) {
		bit = bit.replace("[", "").replaceAll("]", "");
		if (bit.contains(":")) {
			return Integer.valueOf(bit.split(":")[1]);
		}
		else {
			return Integer.valueOf(bit);
		}
	}

	
	
}

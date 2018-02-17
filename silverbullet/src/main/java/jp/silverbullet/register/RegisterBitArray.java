package jp.silverbullet.register;

import java.util.ArrayList;
import java.util.List;

public class RegisterBitArray {
	private List<RegisterBit> bits = new ArrayList<RegisterBit>();

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
	
}

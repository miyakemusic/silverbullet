package jp.silverbullet.register;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import jp.silverbullet.register.RegisterBit.ReadWriteType;

public abstract class RegisterBitArray {
	abstract protected int getRegisterWidth();
	
	private BitComparator comparator = new BitComparator();
	private List<RegisterBit> bits = new ArrayList<RegisterBit>();
	private RegisterBitListener listener = new RegisterBitListener() {
		@Override
		public void onBitChange(String bit, String prev, RegisterBit registerBit) {
			sort();
		}
	};
	
	public RegisterBitArray() {
		
	}

	public List<RegisterBit> getBits() {
		return bits;
	}

	public void setBits(List<RegisterBit> bits) {
		this.bits = bits;
		
		this.bits.forEach(bit -> bit.addListener(listener));
	}

	public void add(RegisterBit registerBit) {
		this.bits.add(registerBit);
		registerBit.addListener(listener);
		this.sort();
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
		this.bits.get(bitRow).removeListener(listener);
		this.bits.remove(bitRow);
	}
	
	public void sort() {
		Collections.sort(this.bits, comparator);
	}

	public void removeAll(List<Integer> indexes) {
		for (int i : indexes) {
			this.remove(i);
		}
	}

	public void add(String name, ReadWriteType rw, String description2, String definition2) {
		int startBit = 0;
		int endBit = getRegisterWidth() - 1;
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
				
		RegisterBit reg = new RegisterBit(name, startBit, endBit, ReadWriteType.RW, description2, definition2);
		reg.addListener(listener);
		this.add(reg);
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

	public RegisterBit getRegisterBit(int targetBit) {
		for (RegisterBit bit: this.bits) {
			if (bit.getStartBit() <= targetBit && bit.getEndBit() >= targetBit) {
				return bit;
			}
		}
		return null;
	}

	public void add() {
		String name = "new" + Calendar.getInstance().getTimeInMillis();
		if (this.getBits().size() > 0) {
			String bits = this.getBits().get(0).getBit();
			int max = Integer.valueOf(bits.split(":")[0]);
			if (max < this.getRegisterWidth()-1) {
				RegisterBit bit = new RegisterBit(name, max, this.getRegisterWidth()-1, ReadWriteType.RW, "Auto-added", "");
				this.add(bit);
			}
			else {
				for (int i = this.getBits().size()-1; i > 0; i--) {
					RegisterBit bit = this.getBits().get(i);
					RegisterBit nextBit = this.getBits().get(i-1);
					int myMax = Integer.valueOf(bit.getBit().split(":")[0]);
					int nextMin = Integer.valueOf(nextBit.getBit().split(":")[1]);
					if (nextMin - myMax >= 2) {
						RegisterBit newBit = new RegisterBit(name, myMax+1, nextMin-1, ReadWriteType.RW, "Auto-adde", "");
						this.add(newBit);
						break;
					}
				}
			}
		}
		else {
			this.add(name, ReadWriteType.RW , "Auto-added", "");
		}
		this.sort();
	}

	public void remove(String bitName) {
		Iterator<RegisterBit> it = this.getBits().iterator();
		while (it.hasNext()) {
			if (it.next().getName().equals(bitName)) {
				it.remove();
				break;
			}
		}
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

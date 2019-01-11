package obsolute.register;

import java.util.BitSet;

public class RegisterValueCalculator {

	public static String getValue(int startBit, int endBit, BitSet current) {
		String ret = "";
		int value = 0;
		for (int index = startBit; index <= endBit; index++) {
			if (current.get(index)) {
				value += Math.pow(2, index - startBit);
			}
		}
		ret = String.valueOf(value);
		return ret;
	}
	
	public static String getValue(String bits, BitSet current) {
		String ret = "";
		RangeGetter range = new RangeGetter(bits);
				
		if (range.isRange()) {
			int startBit = range.getStart();
			int endBit = range.getStop();	
			ret = getValue(startBit, endBit, current);
		}
		else {
			ret = String.valueOf(current.get(range.getStart()) ? 1 : 0);
		}
		return ret;
	}

	public static boolean updateValue(int value, BitSet current, int startBit, int width) {
		boolean changed = false;
		for (int i = startBit; i < startBit + width; i++) {
			int shift = i - startBit;
			boolean v = ((value >> shift) & 0x01) == 0x01;
			if (v != current.get(i)) {
				changed = true;
			}
			current.set(i, v);
		}
		return changed;
	}
	
	public static String getValue(int startBit, BitSet current, BitSet mask) {
		String ret = "";
		String bits = String.valueOf(startBit);
		int i = startBit;
		for (; i < mask.length(); i++) {
			if (!mask.get(i)) {
				bits = String.valueOf(i) + ":" + startBit;
				break;
			}
		}
		if (i == mask.length()) {
			bits = String.valueOf(i-1) + ":" + startBit;
		}
		RangeGetter range = new RangeGetter(bits);
				
		if (range.isRange()) {
			int value = 0;
			for (int index = range.getStart(); index <= range.getStop(); index++) {
				if (current.get(index)) {
					value += Math.pow(2, index - range.getStart());
				}
			}
			ret = String.valueOf(value);	
		}
		else {
			ret = String.valueOf(current.get(range.getStart()) ? 1 : 0);
		}
		return ret;
	}
	
	public static int getValue(BitSet data, int offset, int size) {
		int ret = 0;
		for (int i = offset; i < offset + size; i++) {
			if (data.get(i)) {
				ret += Math.pow(2, i - offset);
			}
		}
		return ret;
	}
}

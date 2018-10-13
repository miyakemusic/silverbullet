package jp.silverbullet.register;

import java.util.BitSet;

public class BitSetToIntConverter {
	private int ret = 0;
	public int convert(BitSet bitSet, int startBit, int endBit) {
		BitSet newBitSet = bitSet.get(startBit, endBit);
		newBitSet.stream().forEach(i -> {
			ret |= (1 << i);
        });

		return ret;
	}
	
	public int convert(BitSet bitSet) {
		return this.convert(bitSet, 0, bitSet.length());
	}
}

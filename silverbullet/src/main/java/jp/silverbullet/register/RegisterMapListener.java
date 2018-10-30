package jp.silverbullet.register;

import java.util.BitSet;

public interface RegisterMapListener {

	void onUpdate(/*int regIndex, int blockNumber, int value, long address, BitSet bitSet, */RegisterUpdates updates);

	void onInterrupt();

	void onUpdatedByHardware(RegisterUpdates updates);

}

package obsolute.register;

import java.util.BitSet;

import jp.silverbullet.register2.RegisterUpdates;

public interface RegisterMapListener {

	void onUpdate(/*int regIndex, int blockNumber, int value, long address, BitSet bitSet, */RegisterUpdates updates);

	void onInterrupt();

	void onUpdatedByHardware(RegisterUpdates updates);

}

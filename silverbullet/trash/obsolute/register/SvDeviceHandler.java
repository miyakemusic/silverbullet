package obsolute.register;

import java.util.BitSet;

public interface SvDeviceHandler {
	void onUpdateRegister(long address, BitSet data, BitSet mask);

	void onInterrupt();

	void onUpdateBlockData(long address, byte[] data);

}

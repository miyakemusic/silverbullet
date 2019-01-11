package obsolute.register;

import java.util.BitSet;

public interface RegisterMonitor {

	void writeIo(long address, BitSet data, BitSet mask);
	void updateBlock(long address, byte[] fileData);
	void updateIo(long address, BitSet data, BitSet mask);
	void interrupt();
	void setSimulator(String replace);
	void setSimulatorEnabled(boolean enabled);

}

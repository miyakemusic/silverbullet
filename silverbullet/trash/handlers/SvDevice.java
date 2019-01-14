package jp.silverbullet.handlers;

import java.util.BitSet;

public interface SvDevice {
	BitSet readIo(long address);
	byte[] readBlock(long address, int size);
	int writeIo(long address, BitSet data, BitSet mask);
	
	void addInterruptHandler(InterruptHandler interruptHandler);
	void removeInterruptHandler(InterruptHandler interruptHandler);
}

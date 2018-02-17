package jp.silverbullet.register;

import java.util.BitSet;

public class NullMonitor implements RegisterMonitor {

	@Override
	public void writeIo(long address, BitSet data, BitSet mask) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateBlock(long address, byte[] fileData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateIo(long address, BitSet data, BitSet mask) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void interrupt() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSimulator(String replace) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSimulatorEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		
	}

}

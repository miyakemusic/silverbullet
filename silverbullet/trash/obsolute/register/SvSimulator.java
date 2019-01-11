package obsolute.register;

import java.util.BitSet;

import jp.silverbullet.handlers.InterruptHandler;
import jp.silverbullet.handlers.SvDevice;

abstract public class SvSimulator {
//	private SimRegisterControl regControl;
	private SvDeviceHandler deviceHandler;

//	protected SimRegisterControl getRegControl() {
//		return regControl;
//	}
	public synchronized void triggerInterrupt() {
		this.deviceHandler.onInterrupt();
	}
	
	public void updateBlockData(long address, byte[] data) {
		this.deviceHandler.onUpdateBlockData(address, data);
	}
	
	public void updateRegister(long address, BitSet data, BitSet mask) {
		this.deviceHandler.onUpdateRegister(address, data, mask);
	}
	
	protected int getValue(int index, BitSet value, BitSet mask) {
		return Integer.valueOf(RegisterValueCalculator.getValue(index, value, mask));
	}
	
	public void setDevice(final SvDeviceHandler deviceHandler) {
		this.deviceHandler = deviceHandler;
		
		SvDevice device = new SvDevice() {
			@Override
			public BitSet readIo(long address) {
				return null;
			}

			@Override
			public byte[] readBlock(long address, int size) {
				return null;
			}

			@Override
			public int writeIo(long address, BitSet data, BitSet mask) {
				deviceHandler.onUpdateRegister(address, data, mask);
				return 1;
			}

			@Override
			public void addInterruptHandler(InterruptHandler interruptHandler) {

			}

			@Override
			public void removeInterruptHandler(InterruptHandler interruptHandler) {
				// TODO Auto-generated method stub
				
			}
			
		};
		
//		regControl = new SimRegisterControl(new RegisterAccess(device));
	}
	
	abstract protected void writeIo(long address, BitSet data, BitSet mask);
//	abstract protected void readIo(long address);
//	abstract protected byte[] readBlock(long address, int size);
	abstract protected void writeBlock(long address, byte[] data);
//	abstract protected boolean isFile(long address);
//	abstract protected String getBlockDescription(long address);
}

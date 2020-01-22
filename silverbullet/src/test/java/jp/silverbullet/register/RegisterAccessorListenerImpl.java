package jp.silverbullet.register;

import jp.silverbullet.core.register2.RegisterAccessorListener;

public class RegisterAccessorListenerImpl implements RegisterAccessorListener {

	private boolean interruptFired = false;
	private Object lastChangedReg;
	private Object lastChangedBit;
	private int lastChangedValue;

	@Override
	public void onUpdate(Object regName, Object bitName, int value) {
		lastChangedReg = regName;
		lastChangedBit = bitName;
		lastChangedValue = value;
	}

	@Override
	public void onUpdate(Object regName, byte[] image) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInterrupt() {
		this.interruptFired = true;
	}

	public boolean isInterruptFired() {
		return interruptFired;
	}

	public Object getLastChangedReg() {
		return lastChangedReg;
	}

	public Object getLastChangedBit() {
		return lastChangedBit;
	}

	public int getLastChangedValue() {
		return lastChangedValue;
	}

}

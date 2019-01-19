package jp.silverbullet.register;

import java.util.List;

import jp.silverbullet.register2.BitValue;
import jp.silverbullet.register2.RegisterAccessor;
import jp.silverbullet.register2.RegisterAccessorListener;

public class RegisterAccessorImpl implements RegisterAccessor {

	private boolean blockRead = false;
	private boolean regRead = false;
	private boolean written = false;
	private boolean cleared  = false;

	@Override
	public void write(Object regName, List<BitValue> data) {
		written = true;
	}

	@Override
	public long readRegister(Object regName, Object bitName) {
		regRead = true;
		return 0;
	}

	@Override
	public void clear(Object regName) {
		cleared   = true;
	}

	@Override
	public void addListener(RegisterAccessorListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] readRegister(Object regName) {
		blockRead = true;
		return null;
	}

	public boolean isBlockRead() {
		return blockRead;
	}

	public boolean isRegRead() {
		return regRead;
	}

	public boolean isWritten() {
		return written;
	}

	public boolean isCleared() {
		return cleared;
	}

	@Override
	public void write(Object regName, Object bitName, int value) {
		this.written = true;
	}

}

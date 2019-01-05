package jp.silverbullet.register2;

import java.util.List;

import jp.silverbullet.register.RegisterSpecHolder;

public class RegisterController implements RegisterAccessor {
	private RegisterAccessorListener listener;

	public RegisterController() {

	}

	@Override
	public void write(Object regName, List<BitValue> data) {
		// TODO Auto-generated method stub

	}

	@Override
	public int readRegister(Object regName, Object bitName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear(Object regName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addListener(RegisterAccessorListener listener) {
		this.listener = listener;
	}

	public void updateValue(Object regName, Object bitName, int value) {
		listener.onUpdate(regName, bitName, value);
	}

	public void updateValue(Object regName, byte[] image) {
		listener.onUpdate(regName, image);
	}

	@Override
	public byte[] readRegister(Object regName) {
		// TODO Auto-generated method stub
		return null;
	}

	public void triggerInterrupt() {
		listener.onInterrupt();
	}

	public void write(String regName, byte[] data) {
		listener.onUpdate(regName, data);
	}

	public void write(String regName, String bitName, String value) {
		listener.onUpdate(regName, bitName, Integer.valueOf(value));
	}
}

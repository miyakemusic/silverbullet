package jp.silverbullet.register2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RegisterController implements RegisterAccessor {
	private Set<RegisterAccessorListener> listeners = new HashSet<>();

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
		this.listeners.add(listener);
	}

	public void updateValue(Object regName, Object bitName, int value) {
		this.listeners.forEach(listener -> listener.onUpdate(regName, bitName, value));
	}

	public void updateValue(Object regName, byte[] image) {
		this.listeners.forEach(listener ->  listener.onUpdate(regName, image));
	}

	@Override
	public byte[] readRegister(Object regName) {
		// TODO Auto-generated method stub
		return null;
	}

	public void triggerInterrupt() {
		listeners.forEach(listener -> listener.onInterrupt());
	}

	public void write(String regName, byte[] data) {
		listeners.forEach(listener -> listener.onUpdate(regName, data));
	}

	public void write(String regName, String bitName, String value) {
		listeners.forEach(listener -> listener.onUpdate(regName, bitName, Integer.valueOf(value)));
	}
}

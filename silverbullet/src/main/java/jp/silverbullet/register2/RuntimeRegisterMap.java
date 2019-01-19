package jp.silverbullet.register2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

class RegisterValue {
	private Map<String, RuntimeBit> registerValues = new HashMap<>();

	private RuntimeBit getBit(String regName) {
		if (!this.registerValues.keySet().contains(regName)) {
			this.registerValues.put(regName, new RuntimeBit());
		}
		return this.registerValues.get(regName);
	}
	
	public int get(String regName, String bitName) {
		return this.getBit(regName).getValue(bitName);
	}

	public void clear(String regName) {
		this.registerValues.get(regName).clear();
	}

	public void set(String regName, byte[] image) {
		this.getBit(regName).setValue(image);
	}

	public byte[] get(String regName) {
		return this.getBit(regName).getImage();
	}

	public void set(String regName, String bitName, int value) {
		this.getBit(regName).setValue(bitName, value);
	}
}

public class RuntimeRegisterMap implements RegisterAccessor, RegisterAccessorListener {
	public enum DeviceType {
		SIMULATOR,
		HARDWARE,
		CONTROLLER
	}
	
	private RegisterValue registerValue = new RegisterValue();
	private Map<DeviceType, RegisterAccessor> devices = new HashMap<>();
	private Set<RegisterAccessorListener> listeners = new HashSet<>();
//	private RegisterController registerController = new RegisterController();
	public RuntimeRegisterMap() {
//		this.addDevice(DeviceType.CONTROLLER, registerController);
	}
	
	@Override
	public void write(Object regName, List<BitValue> data) {
		for (BitValue regValue : data) {
			storeValue(regName, regValue.bitName, regValue.value);
		}
		this.devices.values().forEach(device -> device.write(regName, data));
	}

	private void storeValue(Object regName, Object bitName, int value) {
		if (this.registerValue.get(regName.toString(), bitName.toString()) != value) {
			this.registerValue.set(regName.toString(), bitName.toString(), value);
			this.listeners.forEach(listener -> listener.onUpdate(regName, bitName, value));
		}
	}

	@Override
	public long readRegister(Object regName, Object bitName) {
		if (this.devices.get(DeviceType.HARDWARE) != null) {
			return this.devices.get(DeviceType.HARDWARE).readRegister(regName, bitName);
		}
		return this.registerValue.get(regName.toString(), bitName.toString());
	}

	@Override
	public void clear(Object regName) {
		String reg = regName.toString();
		this.registerValue.clear(regName.toString());
		this.devices.values().forEach(device -> device.clear(reg));
	}

	public void addDevice(DeviceType deviceType, RegisterAccessor accessor) {
		this.devices .put(deviceType, accessor);
		accessor.addListener(this);
	}

	public RegisterAccessor getDevice(DeviceType deviceType) {
		return this.devices.get(deviceType);
	}
	
	@Override // SimulatorListener
	public void onUpdate(Object regName, Object bitName, int value) {
		this.storeValue(regName, bitName, value);
	}
	
	@Override
	public void addListener(RegisterAccessorListener listener) {
		listeners .add(listener);
	}

	@Override
	public void onUpdate(Object regName, byte[] image) {
		storeValue(regName, image);
	}

	private void storeValue(Object regName, byte[] image) {
		this.registerValue.set(regName.toString(), image);
		this.listeners.forEach(listener -> listener.onUpdate(regName, image));
	}

	@Override
	public byte[] readRegister(Object regName) {
		if (this.devices.get(DeviceType.HARDWARE) != null) {
			return this.devices.get(DeviceType.HARDWARE).readRegister(regName);
		}
		return this.registerValue.get(regName.toString());
	}

	@Override
	public void onInterrupt() {
		this.listeners.forEach(listener -> listener.onInterrupt());
	}

	public void removeDevice(RegisterAccessor rg) {
		for (DeviceType key : this.devices.keySet()) {
			if (this.devices.get(key).equals(rg)) {
				this.devices.remove(key);
				return;
			}
		}
	}

	public Set<RegisterAccessor> getUserDevices() {
		Set<RegisterAccessor> ret = new HashSet<>();
		if (this.devices.containsKey(DeviceType.HARDWARE)) {
			ret.add(this.devices.get(DeviceType.HARDWARE));
		}

		if (this.devices.containsKey(DeviceType.SIMULATOR)) {
			ret.add(this.devices.get(DeviceType.SIMULATOR));
		}
		return ret;
	}

	public RegisterController getRegisterController() {
		return (RegisterController)this.devices.get(DeviceType.CONTROLLER);
	}

	@Override
	public void write(Object regName, Object bitName, int value) {
		this.write(regName, Arrays.asList(new BitValue(bitName, value)));
	}

}

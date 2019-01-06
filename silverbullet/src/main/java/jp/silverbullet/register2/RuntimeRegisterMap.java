package jp.silverbullet.register2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RuntimeRegisterMap implements RegisterAccessor, RegisterAccessorListener {
	private Map<String, RuntimeBit> registerValues = new HashMap<>();
	private Set<RegisterAccessor> devices = new HashSet<>();
	private Set<RegisterAccessorListener> listeners = new HashSet<>();
	private RegisterController registerController = new RegisterController();
	public RuntimeRegisterMap() {
		this.addDevice(registerController);
	}
	
	@Override
	public void write(Object regName, List<BitValue> data) {
		for (BitValue regValue : data) {
			storeValue(regName, regValue.bitName, regValue.value);
		}
		this.devices.forEach(device -> device.write(regName, data));
	}

	private void storeValue(Object regName, Object bitName, int value) {
		this.getReg(regName.toString()).setValue(bitName.toString(), value);
		this.listeners.forEach(listener -> listener.onUpdate(regName, bitName, value));
	}
	
	private RuntimeBit getReg(String regName) {
		if (!registerValues.keySet().contains(regName)) {
			this.registerValues.put(regName, new RuntimeBit());
		}
		return this.registerValues.get(regName);
	}

	@Override
	public int readRegister(Object regName, Object bitName) {
		if (!this.registerValues.keySet().contains(regName.toString())) {
			this.registerValues.put(regName.toString(), new RuntimeBit());
		}
		
		return this.registerValues.get(regName.toString()).getValue(bitName.toString());
		//return this.registerValues.get(regName.toString()).getValue(bitName.toString());
	}

	@Override
	public void clear(Object regName) {
		String reg = regName.toString();
		this.registerValues.get(reg).clear();
		this.devices.forEach(device -> device.clear(reg));
	}

	public void addDevice(RegisterAccessor accessor) {
		this.devices .add(accessor);
		accessor.addListener(this);
	}

	@Override // SimulatorListener
	public void onUpdate(Object regName, Object bitName, int value) {
		String reg = regName.toString();
		String bit = bitName.toString();
		//getReg(reg).setValue(bit, value);
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
		getReg(regName.toString()).setValue(image);
		this.listeners.forEach(listener -> listener.onUpdate(regName, image));
	}

	@Override
	public byte[] readRegister(Object regName) {
		return this.getReg(regName.toString()).getImages();
	}

	@Override
	public void onInterrupt() {
		this.listeners.forEach(listener -> listener.onInterrupt());
	}

	public RegisterController getRegisterController() {
		return this.registerController;
	}

	public RegisterSpecHolder getSpecs() {
		// TODO Auto-generated method stub
		return null;
	}
}

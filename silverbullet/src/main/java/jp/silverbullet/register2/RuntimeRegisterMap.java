package jp.silverbullet.register2;

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
	private RegisterValue registerValue = new RegisterValue();
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
		this.registerValue.set(regName.toString(), bitName.toString(), value);
//		this.getReg(regName.toString()).setValue(bitName.toString(), value);
		this.listeners.forEach(listener -> listener.onUpdate(regName, bitName, value));
	}

	@Override
	public int readRegister(Object regName, Object bitName) {
		return this.registerValue.get(regName.toString(), bitName.toString());
		//return this.registerValues.get(regName.toString()).getValue(bitName.toString());
	}

	@Override
	public void clear(Object regName) {
		String reg = regName.toString();
		this.registerValue.clear(regName.toString());
		this.devices.forEach(device -> device.clear(reg));
	}

	public void addDevice(RegisterAccessor accessor) {
		this.devices .add(accessor);
		accessor.addListener(this);
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
		return this.registerValue.get(regName.toString());
//		return this.getReg(regName.toString()).getImages();
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

	public Set<RegisterAccessor> getDevices() {
		return this.devices;
	}

	public void removeDevice(RegisterAccessor rg) {
		this.devices.remove(rg);
	}

	public Set<RegisterAccessor> getUserDevices() {
		Set<RegisterAccessor> ret = new HashSet<>(); 
		if (this.devices.size() >= 2) {
			Iterator<RegisterAccessor> it = this.devices.iterator();
			it.next();
			ret.add(it.next());
		}
		return ret;
	}

}

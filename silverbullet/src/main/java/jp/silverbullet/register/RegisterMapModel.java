package jp.silverbullet.register;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import com.sun.jersey.core.util.Base64;

import jp.silverbullet.BuilderModel;
import jp.silverbullet.handlers.InterruptHandler;
import jp.silverbullet.handlers.SvDevice;

public class RegisterMapModel implements SvDevice, SvDeviceHandler {
	private Map<Long, SvRegister> map = new LinkedHashMap<>();
	private Map<Long, BitSet> mapValue = new LinkedHashMap<>();
	private long minAddress;
	private long maxAddress;
	private BuilderModel builderModel;
	private Set<RegisterMapListener> listeners = new HashSet<>();
	private Set<InterruptHandler> interruptHandlers = new HashSet<>();
	private Set<SvSimulator> simulators = new HashSet<>();
	private Map<Long, byte[]> blockData = new HashMap<>();
	private Map<Long, String> blockNameMap = new HashMap<>();

	public long getMinAddress() {
		return minAddress;
	}

	public long getMaxAddress() {
		return maxAddress;
	}
	
	public RegisterMapModel(BuilderModel builderModel) {
		this.builderModel = builderModel;
		update();
	}
	
	public List<SvSimulator> getSimulators() {
		return new ArrayList<SvSimulator>(simulators);
	}


	public void addSimulator(SvSimulator simulator) {
		for (SvSimulator sim : this.simulators) {
			if (sim.getClass().getName().equals(simulator.getClass().getName())) {
				return ;
			}
		}
	//	this.simulators.forEach(sim -> {if (sim.getClass().getName().equals(simulator.getClass().getName()))return;});
		simulator.setDevice(this);
		this.simulators.add(simulator);
//		this.monitor.setSimulator(simulator.getClass().getSimpleName().replace(".class", ""));
	}
	
	public void update() {
		minAddress = Long.MAX_VALUE;
		maxAddress = Long.MIN_VALUE;
		map.clear();
		for (SvRegister register : builderModel.getRegisterProperty().getRegisters()) {
			long address = 0;
			if (register.isBlock()) {
				String[] tmp = register.getAddress().split("-");
				address = Long.parseLong(tmp[0].replace("0x", ""), 16);
			}
			else {
				address = Long.parseLong(register.getAddress().replace("0x", ""), 16);
			}
			if (minAddress > address) {
				minAddress = address;
			}
			else if (maxAddress < address) {
				maxAddress = address;
			}
			map.put(address, register);
			mapValue.put(address, new BitSet(32));
		}
	}

	public int getCount() {
		return (int)(this.maxAddress - this.minAddress)+1;
	}

	public boolean exists(int i) {
		return getRegAt(i) != null;
	}

	private long getAddAt(int i) {
		return this.minAddress + i;
	}

	public int getPartitions(int i) {
		SvRegister reg = getRegAt(i);
		return reg.getBits().getBits().size();
	}

	private SvRegister getRegAt(int i) {
		long add = getAddAt(i);
		return this.map.get(add);
	}

	public List<RegisterBit> getBits(int i) {
		return getRegAt(i).getBits().getBits();
	}

	public String getTitle(int i) {
		return getRegAt(i).getName();
	}

	public String getDescription(int i) {
		return getRegAt(i).getDescription();
	}

	@Override
	public BitSet readIo(long address) {
		BitSet ret = (BitSet)this.mapValue.get(address).clone();
		return ret;
	}

	@Override
	public int writeIo(long address, BitSet data, BitSet mask) {
		if (map.isEmpty()) {
			return -1;
		}
		updateBits(address, data, mask, true);
		
		for (SvSimulator simulator : this.simulators) {
			simulator.writeIo(address, data, mask);
		}
		
		
		return 0;
	}

	protected void updateBits(long address, BitSet data, BitSet mask, boolean bySoftware) {		
		BitSet current = this.mapValue.get(address);
		final int regIndex = getIndex(address);	
		SvRegister register = this.map.get(address);
		Set<RegisterBit> changed = new HashSet<>();
		for (int i = 0; i < builderModel.getRegisterProperty().getRegisterWidth(); i++) {
			if (mask.get(i)) {
				if (current.get(i) != data.get(i)) {
					current.set(i, data.get(i));
					RegisterBit bit = register.getBits().getRegisterBit(i);
					changed.add(bit);
				}
			}
		}

		RegisterUpdates updates = new RegisterUpdates();
		updates.setAddress((int)address);
		updates.setName(register.getName());
		
		for (RegisterBit bit : changed) {
			int val = new BitSetToIntConverter().convert(current.get(bit.getStartBit(), bit.getEndBit() + 1));
			BitUpdates info = new BitUpdates(bit.getName(), String.valueOf(val));
			updates.getBits().add(info);
		}

		fireUpdate(/*regIndex, 0, 0, address, current, */updates, bySoftware);
	}

	private int getIndex(long address) {
		return (int)(address - this.minAddress);
	}

	public int getBitWidth(String bit) {
		if (bit.contains(":")) {
			String[] tmp = bit.split(":");
			return Integer.valueOf(tmp[0].replace("[", "")) - Integer.valueOf(tmp[1].replace("]", "")) + 1;
		}
		else {
			return 1;
		}
	}
		
	public String getValue(int regIndex, int block) {
		BitSet current = getCurrentValue(regIndex);
		String ret = getCurrentValue(getBits(regIndex, block), current);
		return ret;
	}

	protected String getCurrentValue(String bits, BitSet current) {
		return RegisterValueCalculator.getValue(bits, current);
	}

	public void addListener(RegisterMapListener listener) {
		this.listeners.add( listener );
	}

	public String getAddress(int regIndex) {
		return getRegAt(regIndex).getAddress();
	}

	private ReadWriteLock lock = new ReentrantReadWriteLock();
	public synchronized void triggerInterrupt() {
		lock.readLock().lock();
		for (InterruptHandler handler : this.interruptHandlers) {
			handler.onTrigger();
		}
		lock.readLock().unlock();
		
		for (RegisterMapListener listener :this.listeners) {
			listener.onInterrupt();
		}
	}

	@Override
	public void addInterruptHandler(InterruptHandler interruptHandler) {
		lock.writeLock().lock();
		this.interruptHandlers.add(interruptHandler);
		lock.writeLock().unlock();
	}

	@Override
	public void removeInterruptHandler(InterruptHandler interruptHandler) {
		lock.writeLock().lock();;
		this.interruptHandlers.remove(interruptHandler);
		lock.writeLock().unlock();
	}
	
	protected String getBits(int regIndex, int block) {
		String bits = this.getRegAt(regIndex).getBits().getBits().get(block).getBit();
		return bits;
	}

	protected BitSet getCurrentValue(int i) {
		BitSet current = this.mapValue.get(this.getAddAt(i));
		return current;
	}

	@Override
	public byte[] readBlock(long address, int size) {
		return this.getBlockData(address, size);
	}

	private byte[] getBlockData(long address, int size) {
		return this.blockData .get(address);
	}

	@Override
	public void onUpdateRegister(long address, BitSet data, BitSet mask) {
		updateBits(address, data, mask, false);
	}

	protected void fireUpdate(/*int regIndex, int num, int val, long address, BitSet bitSet, */RegisterUpdates updates, boolean bySoftware) {
		for (RegisterMapListener listener :this.listeners) {
			listener.onUpdate(/*regIndex, num, val, address, bitSet,*/ updates);
			if (!bySoftware) {
				listener.onUpdatedByHardware(updates);
			}
		}
	}

	@Override
	public void onInterrupt() {
		this.triggerInterrupt();	
	}
	
	private long getAddress(String regName) {
		for (long addr : this.map.keySet()) {
			SvRegister reg = this.map.get(addr);
			if (reg.getName().equals(regName)) {
				return addr;
			}
		}	
		return 0;
	}
	
	public void setBlockData(String regName, byte[] b) {
		this.blockData.put(getAddress(regName), b);
	}
	
	public List<String> getSimulatorClasses(String userApplicationPackage) {
		Reflections reflections = new Reflections(userApplicationPackage + ".test", new SubTypesScanner(false));
		Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);
		List<String> ret = new ArrayList<>();
		for (Class c : allClasses) {
			if (c.getName().startsWith(userApplicationPackage + ".test.Sim")) {
				ret.add(c.getName().replace(".class", "").replace(userApplicationPackage + ".test.", "").replace("$1", ""));
			}
		}
		return ret;
	}

	@Override
	public void onUpdateBlockData(long address, byte[] data) {
		this.blockData.put(address, data);
		RegisterUpdates updates = new RegisterUpdates();
		updates.setAddress((int)address);
		
		SvRegister register = this.map.get(address);
		updates.setName(register.getName());
		BitUpdates bit = new BitUpdates(register.getBits().getRegisterBit(0).getName(), new String(Base64.encode((data))));
		updates.addBit(bit);
		this.fireUpdate(updates, false);
	}

	public Map<Long, BitSet> getMapValue() {
		return this.mapValue;
	}

}

package jp.silverbullet.register;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import jp.silverbullet.BuilderFx;
import jp.silverbullet.BuilderModel;
import jp.silverbullet.handlers.InterruptHandler;
import jp.silverbullet.handlers.RegisterAccess;
import jp.silverbullet.handlers.SvDevice;

public class RegisterMapModel implements SvDevice, SvDeviceHandler {
	private Map<Long, SvRegister> map = new LinkedHashMap<>();
	private Map<Long, BitSet> mapValue = new LinkedHashMap<>();
	private long minAddress;
	private long maxAddress;
	private BuilderModel builderModel;
	private RegisterMapListener listener;
	private InterruptHandler interruptHandler;
	private SvSimulator currentDevice;
	private SvSimulator simulator;
	private SvSimulator nullSimulator = new NullSimulator();
	private Map<Long, byte[]> blockData = new HashMap<>();
	private Map<Long, String> blockNameMap = new HashMap<>();
	private RegisterMonitor monitor = new NullMonitor();
	public long getMinAddress() {
		return minAddress;
	}

	public long getMaxAddress() {
		return maxAddress;
	}
	
	public RegisterMapModel(BuilderModel builderModel) {
		this.builderModel = builderModel;
		this.currentDevice = nullSimulator;
		update();
	}

	public void setMonitor(RegisterMonitor monitor) {
		this.monitor = monitor;
	}
	
	public void setSimulator(SvSimulator simulator) {
		this.simulator = simulator;
		this.simulator.setDevice(this);
		if (this.currentDevice != this.nullSimulator) {
			this.currentDevice = this.simulator;
		}
		this.monitor.setSimulator(simulator.getClass().getSimpleName().replace(".class", ""));
	}
	
	public void update() {
		minAddress = Long.MAX_VALUE;
		maxAddress = Long.MIN_VALUE;
		map.clear();
		for (SvRegister register : builderModel.getRegisterProperty().getRegisters()) {
			long address = 0;
			if (register.isBlock()) {
				String[] tmp = register.getAddress().split("-");
				//long start = Long.valueOf(tmp[0]);
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
		updateBits(address, data, mask);
		currentDevice.writeIo(address, data, mask);
		
		return 0;
	}

	protected void updateBits(long address, BitSet data, BitSet mask) {
		monitor.writeIo(address, data, mask); // for auto test
		
		BitSet current = this.mapValue.get(address);
		final int regIndex = getIndex(address);
		
		List<RegisterBit> bits = this.map.get(address).getBits().getBits();
		int offset = 0;
		int blockNumber = bits.size() -1;
		for (int i = bits.size()-1; i >= 0; i--) {
			RegisterBit bit = bits.get(i);
			int width = this.getBitWidth(bit.getBit());
			boolean changed = false;
			int value = 0;
			for (int j = 0; j < width; j++) {
				int index = offset + j;
				if (mask.get(index)) {
					if (current.get(index) != data.get(index)) {
						current.set(index, data.get(index));
						changed = true;
					}
					if (data.get(index)) {
						value += Math.pow(2, j);
					}
				}
			}
			if (changed) {
				final int num = blockNumber;
				final int val = value;
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						fireUpdate(regIndex, num, val);
					}
				});
				
			}
			blockNumber--;
			offset += width;
		}
	}

	private int getIndex(long address) {
		return (int)(address - this.minAddress);
	}

	int getBitWidth(String bit) {
		if (bit.contains(":")) {
			String[] tmp = bit.split(":");
			return Integer.valueOf(tmp[0].replace("[", "")) - Integer.valueOf(tmp[1].replace("]", "")) + 1;
		}
		else {
			return 1;
		}
	}
		
	public String getValue(int regIndex, int block) {
		long address = this.getAddAt(regIndex);
		if ((this.currentDevice == this.nullSimulator) && (this.blockNameMap.get(address) != null)) {
			return "File:" + blockNameMap.get(address);
		}
		else {
			BitSet current = getCurrentValue(regIndex);
			String ret = getCurrentValue(getBits(regIndex, block), current);
			return ret;
		}
	}

	protected String getCurrentValue(String bits, BitSet current) {
		return RegisterValueCalculator.getValue(bits, current);
	}

	public void setOnChange(RegisterMapListener listener) {
		this.listener = listener;
	}

	public String getAddress(int regIndex) {
		return getRegAt(regIndex).getAddress();
	}

	public void triggerInterrupt() {
		if (interruptHandler != null) {
			interruptHandler.onTrigger();
		}
		if (this.listener != null) {
			this.listener.onInterrupt();
		}
		monitor.interrupt();
	}

	@Override
	public void setInterruptHandler(InterruptHandler interruptHandler) {
		this.interruptHandler = interruptHandler;
	}

	public void setValue(int regIndex, int block, int value) {
		String bits = getBits(regIndex, block);
		BitSet current = getCurrentValue(regIndex);
		RangeGetter range = new RangeGetter(bits);
		if (range.isRange()) {
			for (int index = range.getStart(); index <= range.getStop(); index++) {
				long v = (value >> (index-range.getStart())) & 0x01;
				current.set(index, v == 1);
			}
			fireUpdate(regIndex, block, value);
		}
		else {
			String tmp = bits.replace("[", "").replace("]", "");
			current.set(Integer.valueOf(tmp), value == 1 ? true : false);
			fireUpdate(regIndex, block, value);
		}
		monitor.writeIo(this.getAddAt(regIndex), current, getBitSet(this.getAddAt(regIndex), block));
	}

	private BitSet getBitSet(long address, int block) {
		List<RegisterBit> bits = this.map.get(address).getBits().getBits();
		RegisterBit bit = bits.get(bits.size() - block);
		RangeGetter range = new RangeGetter(bit.getBit());
		BitSet ret = new BitSet();
		if (range.isRange()) {
			for (int i = range.getStart(); i <= range.getStop(); i++) {
				ret.set(i);
			}
		}
		else {
			ret.set(range.getStart());
		}
		return ret;
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
		updateBits(address, data, mask);
		monitor.updateIo(address, data, mask);
	}

	protected void fireUpdate(int regIndex, int num, int val) {
		if (listener == null) {
			return;
		}
		
		listener.onDataUpdate(regIndex, num, val);
	}

	@Override
	public void onInterrupt() {
		this.triggerInterrupt();	
	}

	public void setBlock(int regIndex, int block, File file) {
	    byte[] fileData = new byte[(int) file.length()];
	    
		try {
		    DataInputStream dis = new DataInputStream(new FileInputStream(file));
		    dis.readFully(fileData);
		    dis.close();
		    
		    long address = this.getAddAt(regIndex);
//		    FileBasedBlock data = new FileBasedBlock(file.getAbsolutePath(), fileData);
//		    ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		    ObjectOutputStream dos = new ObjectOutputStream(bos);
//		    dos.writeObject(data);
		    this.blockData.put(address, fileData);
		    this.blockNameMap.put(address, file.getAbsolutePath());
		    monitor.updateBlock(address, fileData);
		    //currentDevice.writeBlock(this.getAddAt(regIndex), bos.toByteArray());

		    this.fireUpdate(regIndex, block, -1);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setSimulatorEnabled(boolean enabled) {
		if (enabled) {
			this.currentDevice = this.simulator;
		}
		else {
			this.currentDevice = this.nullSimulator ;
		}
		this.monitor.setSimulatorEnabled(enabled);
	}

	public void setSimulatorClass(String simClass) {
		try {
			Class<?> c = Class.forName(builderModel.getUserApplicationPath() + ".test." + simClass);
			
			SvSimulator object = (SvSimulator)c.getConstructor(RegisterAccess.class).newInstance(builderModel.getRegisterAccess());
//			SvSimulator object = (SvSimulator)c.newInstance();
			this.setSimulator(object);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<String> getSimulatorClasses() {
		String p = this.getClass().getResource(this.getClass().getSimpleName() + ".class").toExternalForm();
		String pp = p.replace(this.getClass().getName().replace(".", "/") + ".class", "").replace("file:/", "");
		pp += builderModel.getUserApplicationPath().replaceAll("\\.", "/") + "/test";
		//String p = builderModel.getUserApplicationPath().
		List<String> ret = new ArrayList<String>();
		for (File file : new File(pp).listFiles()) {
			String name =file.getName().replace(".class", "");
			if (name.contains("$") || !name.startsWith("Sim")) {
				continue;
			}
			ret.add(name);
		}
		return ret;
	}

	@Override
	public void onUpdateBlockData(long address, byte[] data) {
		this.blockData.put(address, data);
		this.monitor.updateBlock(address, data);
	}
}

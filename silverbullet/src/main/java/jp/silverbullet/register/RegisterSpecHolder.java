package jp.silverbullet.register;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import jp.silverbullet.register2.SvRegisterListener;
@XmlRootElement
public class RegisterSpecHolder {
	private Map<Long, SvRegister> registers = new LinkedHashMap<>();
	private int registerWidth = 32;
	
	private SvRegisterListener listener = new SvRegisterListener() {
		@Override
		public void onAddressChange(SvRegister svRegister, String address, String prev) {
			address = getStartAddress(address);
			prev = getStartAddress(prev);
			changeAddress(svRegister, RegisterSpecHolder.toDecAddess(address), RegisterSpecHolder.toDecAddess(prev));
		}
	};
	
	public RegisterSpecHolder() {}


	public RegisterSpecHolder(int width){
		this.registerWidth = width;
	}
	
	public SvRegister addRegister(String name, String address, String description) {
		SvRegister register = this.createRegister();
		register.setName(name);
		register.setDescription(description);
		register.setAddressHex(address);
		
		address = getStartAddress(address);
//		this.registers.put(toDecAddess(address), register);
		this.putNewRegister(toDecAddess(address), register);
		sort();
		return register;
	}
	public static long toDecAddess(String address) {
		return Long.parseLong(address.replace("0x", ""), 16);
	}
	
	public void sort() {
		// This algolithm is bad..
		List<Long> keys = new ArrayList<Long>(this.registers.keySet());
		Collections.sort(keys);
		
		Map<Long, SvRegister> newMap = new LinkedHashMap<>();
		for (Long key : keys) {
			newMap.put(key, this.registers.get(key));
		}
		this.registers = newMap;
	}
	
	public List<SvRegister> getRegisterList() {
		return new ArrayList<SvRegister>(this.registers.values());
	}


	public Map<Long, SvRegister> getRegisters() {
		return registers;
	}


	public void remove(SvRegister svRegister) {
		svRegister.removeListener(listener);
		this.registers.remove(toDecAddess(svRegister.getAddress()));
	}

	public void remove(long address) {
		this.registers.get(address).removeListener(listener);
		this.registers.remove(address);
	}
	
	public SvRegister getRegisterByName(String regName) {
		for (Long address : this.registers.keySet()) {
			SvRegister register = this.registers.get(address);
			if (register.getName().equals(regName)) {
				return register;
			}			
		}
		return null;
	}

	public SvRegister getRegisterByAddress(long address) {
		return this.registers.get(address);
	}

	public int getLastDecAddess() {
		if (this.registers.size() == 0) {
			return 0;
		}
		return this.registers.get((long)(this.registers.size()-1)).getDecAddress();
	}

	public int getRegisterWidth() {
		return this.registerWidth ;
	}
	public SvRegister newRegister(String name, long address, String description) {
		SvRegister ret = createRegister();
		ret.setName(name.toString());
		ret.setAddress(address);
		ret.setDescription(description);
		putNewRegister(address, ret);
		this.sort();
		return ret;
	}
	private void putNewRegister(long address, SvRegister register) {
		register.addListener(listener);
		this.registers.put(address, register);
	}
	
	public SvRegister insertRegisterAt(Integer row) {	
		SvRegister newRegister = createRegister();
		newRegister.setName("NEW_" + row.toString()  + this.registers.size() + Calendar.getInstance().getTimeInMillis());
		int iRow = Integer.valueOf(row);
			
		String currentAddress = "";
		if (registers.size() == 0) {
			currentAddress = "0x00";
		}
		else {
			currentAddress = registers.values().toArray(new SvRegister[0])[iRow].getAddress();
		}
		if (currentAddress.contains("-")) {
			currentAddress = currentAddress.split("-")[1];
		}
		
		int i = 1;
		long address = 0;
		while (true) {
			address = toDecAddess(currentAddress) + (this.getRegisterWidth()/8) * i;		
			if (!this.registers.keySet().contains(address)) {
				break;
			}
			i++;
		}
		
		newRegister.setAddressHex(toHexAddress(address));
		
		putNewRegister(address, newRegister);
		this.sort();
		
		return newRegister;
	}

	private SvRegister createRegister() {
		SvRegister newRegister = new SvRegister() {
			@Override
			protected int getRegisterWidth() {
				return registerWidth;
			}

			@Override
			protected boolean conflictsName(String name2, SvRegister svRegister) {
				for (SvRegister reg : registers.values()) {
					if (reg.equals(svRegister)) {
						continue;
					}
					if (reg.getName().equals(name2)) {
						return true;
					}
				}
				return false;
			}
		};
		return newRegister;
	}
	public void newMultiRegister(String name, long startAddress, long endAddress, String description) {
		this.addRegister(name, toHexAddress(startAddress, endAddress), description);
	}

	public static String toHexAddress(long startAddress, long endAddress) {
		return toHexAddress(startAddress) + "-" + toHexAddress(endAddress);
	}
	public static String toHexAddress(long decAddress) {
		return "0x" + Long.toHexString(decAddress);
	}
	public Integer size() {
		return this.registers.size();
	}

	protected void changeAddress(SvRegister register, long newAddress, long oldAddress) {
		this.remove(oldAddress);
		this.putNewRegister(newAddress, register);
		this.sort();	
	}

	public static String getStartAddress(String address) {
		if (address.contains("-")) {
			address = address.split("-")[0];
		}
		return address;
	}


	public SvRegister getRegisterByIndex(int row) {
		return this.getRegisterList().get(row);
	}


	public void addRegister() {
		this.insertRegisterAt(this.size()-1);
	}


	public void removeRow(Integer row) {
		this.remove(this.getRegisterList().get((row)));
	}
}

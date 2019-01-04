package jp.silverbullet.register;

import java.util.BitSet;

public class RegisterInfo {

	private int intAddress;
	private BitSet mask;
	private BitSet dataSet;

	public RegisterInfo(String regName, String bitName, String value, RegisterSpecHolder registerProperty) {
		SvRegister register  = registerProperty.getRegisterByName(regName);
		RegisterBit bit = register.getBits().get(bitName);
		
		String address = register.getAddress().replace("0x", "");
		if (address.contains("-")) {
			address = address.split("-")[0];
		}
		intAddress = Integer.parseInt(address, 16);
		dataSet = new BitSet();
		
		int iValue = Integer.valueOf(value);
		BitSet tmp = BitSet.valueOf(new long[]{iValue});
		mask = new BitSet();
		for (int i = bit.getStartBit(); i <= bit.getEndBit(); i++) {
			dataSet.set(i, tmp.get(i - bit.getStartBit()));
			mask.set(i);
		}
	}

	public int getIntAddress() {
		return intAddress;
	}

	public BitSet getMask() {
		return mask;
	}

	public BitSet getDataSet() {
		return dataSet;
	}

}

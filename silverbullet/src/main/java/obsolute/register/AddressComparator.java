package obsolute.register;

import java.util.Comparator;

import jp.silverbullet.register2.SvRegister;

public class AddressComparator implements Comparator<SvRegister> {
	@Override
	public int compare(SvRegister o1, SvRegister o2) {
		return getStartAddress(o1.getAddress()) - getStartAddress(o2.getAddress());
	}

	private int getStartAddress(String address) {
		address = address.replace("0x", "");
		if (address.contains("-")) {
			return Integer.parseInt(address.split("-")[0], 16);
		}
		else {
			return Integer.parseInt(address, 16);
		}
	}

}

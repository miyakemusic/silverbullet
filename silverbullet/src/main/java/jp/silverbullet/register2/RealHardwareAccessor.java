package jp.silverbullet.register2;

import java.util.BitSet;
import java.util.List;

import jp.silverbullet.register.RegisterSpecHolder;
import jp.silverbullet.register.SvRegister;

public class RealHardwareAccessor implements RegisterAccessor {

	private RegisterAccessorListener listener;
	private RegisterSpecHolder specHolder;

	public RealHardwareAccessor(RegisterSpecHolder specs) {
		this.specHolder = specs;
	}

	@Override
	public void write(Object regName, List<BitValue> data) {
		//...
		SvRegister reg = this.specHolder.getRegisterByName(regName.toString());	
		BitSet bitSet = null;
		nativeWrite(reg.getDecAddress(), bitSet);
		
		//...
		this.listener.onInterrupt();
	}

	private void nativeWrite(int decAddress, BitSet bitSet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int readRegister(Object regName, Object bitName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear(Object string) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addListener(RegisterAccessorListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] readRegister(Object regName) {
		// TODO Auto-generated method stub
		return null;
	}

}

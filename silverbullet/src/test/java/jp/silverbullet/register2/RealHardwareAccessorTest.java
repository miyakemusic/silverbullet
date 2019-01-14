package jp.silverbullet.register2;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import jp.silverbullet.register.UserRuntimeRegisterHolderForTest;
import jp.silverbullet.register2.RegisterBit.ReadWriteType;

public class RealHardwareAccessorTest {
	private long val;
	@Test
	public void test() {
//		RegisterSpecHolder holder = new RegisterSpecHolder(32);
//		holder.newRegister(UserRuntimeRegisterHolderForTest.Register.Reg1.toString(), 0x10, "ret1").
//			newBit(UserRuntimeRegisterHolderForTest.Reg1.bit1.toString(), 0, 15, ReadWriteType.RW, "").
//			newBit(UserRuntimeRegisterHolderForTest.Reg1.bit2.toString(), 16, 31, ReadWriteType.RW, "");
//
//		NativeAccessor nativeAccessor = new NativeAccessor() {
//			@Override
//			public void write(long address, long value) {
//				val = value;
//			}			
//		};
//		RealHardwareAccessor realHard = new RealHardwareAccessor(holder, nativeAccessor);
//		List<BitValue> data = new ArrayList<>();
//		data.add(new BitValue(UserRuntimeRegisterHolderForTest.Reg1.bit1, 10));
//		realHard.write(UserRuntimeRegisterHolderForTest.Register.Reg1, data);
//		assertEquals(10, val);
//		
//		data.clear();
//		data.add(new BitValue(UserRuntimeRegisterHolderForTest.Reg1.bit2, 10));
//		realHard.write(UserRuntimeRegisterHolderForTest.Register.Reg1, data);
//		assertEquals(((long)10)<<16, val);
//		
//		data.clear();
//		data.add(new BitValue(UserRuntimeRegisterHolderForTest.Reg1.bit1, 10));
//		data.add(new BitValue(UserRuntimeRegisterHolderForTest.Reg1.bit2, 10));
//		realHard.write(UserRuntimeRegisterHolderForTest.Register.Reg1, data);
//		assertEquals(((long)10)<<16 | (long)10, val);
	}

}

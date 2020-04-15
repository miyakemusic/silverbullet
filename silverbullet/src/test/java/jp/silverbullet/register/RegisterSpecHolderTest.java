package jp.silverbullet.register;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import jp.silverbullet.core.KeyValue;
import jp.silverbullet.core.register2.BitValue;
import jp.silverbullet.core.register2.RegisterAccessor;
import jp.silverbullet.core.register2.RegisterAccessorListener;
import jp.silverbullet.core.register2.RegisterBit;
import jp.silverbullet.core.register2.RegisterBitArray;
import jp.silverbullet.core.register2.RegisterController;
import jp.silverbullet.core.register2.RegisterJsonController;
import jp.silverbullet.core.register2.RegisterSpecHolder;
import jp.silverbullet.core.register2.RuntimeRegisterMap;
import jp.silverbullet.core.register2.SvRegister;
import jp.silverbullet.core.register2.RegisterBit.ReadWriteType;
import jp.silverbullet.core.register2.RuntimeRegisterMap.DeviceType;
import jp.silverbullet.dev.sourcegenerator.RegisterSourceGenerator;
import jp.silverbullet.web.register.json.SvRegisterJsonHolder;

public class RegisterSpecHolderTest {

	@Test
	public void testRegisters() {
		RegisterSpecHolder holder = new RegisterSpecHolder(32);
		holder.newRegister("test control", 0x10, "test control").
			newBit("start", 0, 1, ReadWriteType.RW, "Start test when the bit is on").
			newBit("stop", 1, 1, ReadWriteType.RW, "Stop test when bit it is on");
		
		holder.newRegister("standard config", 0x20, "standard configuration").
		newBit("parameter 1", 0, 1, ReadWriteType.RW, "").
		newBit("parameter 2", 1, 1, ReadWriteType.RW, "");
		
		holder.newRegister("additional config", 0x18, "additional configuration").
		newBit("additional B", 16, 16, ReadWriteType.RW, "").
		newBit("additional A", 0, 16, ReadWriteType.RW, "");
	
		// test sort
		assertEquals(3, holder.getRegisterList().size());
		assertEquals("test control", holder.getRegisterList().get(0).getName());
		assertEquals("0x10", holder.getRegisterList().get(0).getAddress());
		assertEquals("additional config", holder.getRegisterList().get(1).getName());
		assertEquals("standard config", holder.getRegisterList().get(2).getName());

		{
			SvRegister reg = holder.getRegisterByName("test control");
			RegisterBit bit = reg.getBit("start");
			assertEquals("start", bit.getName());
			assertEquals(0, bit.getStartBit());
			assertEquals(1, bit.getSize());
			assertEquals("Start test when the bit is on", bit.getDescription());
			
			assertEquals("stop", reg.getBits().getBits().get(0).getName());
			assertEquals("start", reg.getBits().getBits().get(1).getName());
		}
		{
			RegisterBit bit = holder.getRegisterByName("additional config").getBit("additional B");
			assertEquals(16, bit.getStartBit());
			assertEquals(31, bit.getEndBit());
			
			// test sort
			RegisterBitArray bits = holder.getRegisterByAddress(Integer.parseInt("18", 16)).getBits();
			assertEquals("additional B", bits.getBits().get(0).getName());
			assertEquals("additional A", bits.getBits().get(1).getName());
		}
		
		{// multi register
			holder.newMultiRegister("graph data", 0x100, 0x200, "Graph Data");
			SvRegister reg = holder.getRegisterByName("graph data");
			assertEquals("graph data", reg.getName());
			assertEquals("0x100-0x200", reg.getAddress());
		}
		
		// Insert
		{
			// Inserts last
			holder.insertRegisterAt(holder.size()-1);
			SvRegister reg = holder.getRegisterList().get(holder.size() - 1);
			assertEquals("0x204", reg.getAddress());
			
			// Inserts head (can insert at next)
			SvRegister inserted = holder.insertRegisterAt(0);
			assertTrue(inserted.getName().startsWith("NEW"));
			SvRegister reg2 = holder.getRegisterList().get(1);
			assertEquals(inserted, reg2);
			assertEquals(0x14, reg2.getDecAddress());
			assertEquals(reg2, holder.getRegisterByAddress(0x14));
			assertTrue(reg2.getName().startsWith("NEW"));
			 
			// Inserts head (cannot insert at next, but next to next..
			SvRegister inserted2 = holder.insertRegisterAt(0);
			SvRegister reg3 = holder.getRegisterList().get(3);
			assertEquals(inserted2, reg3);
			assertEquals(0x1C, reg3.getDecAddress());
			assertTrue(reg3.getName().startsWith("NEW"));
			assertEquals(reg3, holder.getRegisterByAddress(0x1C));
			
			// remove above two registers
			holder.remove(reg2.getDecAddress());
			holder.remove(reg3.getDecAddress());
		}
		
		{
			holder.remove(0x10);
			assertEquals("additional config", holder.getRegisterList().get(0).getName());
			assertEquals("standard config", holder.getRegisterList().get(1).getName());
			
			assertEquals("0x204", holder.getRegisterByAddress(holder.getLastDecAddess()).getAddress());
			SvRegister reg = holder.addRegister("will removed", "0x100000", "");
			assertEquals(0x100000, holder.getLastDecAddess());
			assertEquals(reg, holder.getRegisterByAddress(0x100000));
			assertEquals("0x100000", holder.getRegisterByAddress(holder.getLastDecAddess()).getAddress());
			holder.removeRow(holder.getRegisterList().size()-1);
			assertEquals("0x204", holder.getRegisterByAddress(holder.getLastDecAddess()).getAddress());
			
		}
		
		{
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			holder.addRegister();
			SvRegister added = holder.getRegisterList().get(holder.size()-1);
			assertTrue(added.getName().startsWith("NEW"));
			assertEquals("0x208", added.getAddress());
			holder.remove(added.getDecAddress());
		}
		// change address
		{
			SvRegister reg = holder.getRegisterByName("additional config");
			reg.setAddressDec(0x1000);
			assertEquals("standard config", holder.getRegisterList().get(0).getName());
			
			reg.setAddressDec(0x18);
			assertEquals("additional config", holder.getRegisterList().get(0).getName());
			
			reg.setAddress("0x1000");
			assertEquals("standard config", holder.getRegisterList().get(0).getName());
		}
		{
			SvRegister reg = holder.getRegisterByName("graph data");
			reg.setAddressDec(0x2000, 0x2100);
			
			SvRegister lastReg = holder.getRegisterList().get(holder.size()-1);
			assertEquals("graph data", lastReg.getName());
			assertEquals("0x2000-0x2100", lastReg.getAddress());
		}
		
		// change name
		{
			holder.getRegisterByName("graph data").setName("trace data");
			assertEquals("0x2000-0x2100", holder.getRegisterByName("trace data").getAddress());
		}
		
		// name conflicts
		{
			boolean ret = holder.getRegisterByName("trace data").setName("additional config");
			assertEquals(false, ret);
			assertEquals("trace data", holder.getRegisterList().get(holder.size()-1).getName());
		}
		
	}
	
	@Test
	public void testBit() {
		RegisterSpecHolder holder = new RegisterSpecHolder(32);
		holder.newRegister("test control", 0x10, "test control").
			newBit("bit0", 0, 1, ReadWriteType.RW, "Start test when the bit is on").
			newBit("bit1", 1, 1, ReadWriteType.RW, "Stop test when bit it is on").
			newBit("bit8", 8, 4, ReadWriteType.RO, "4bits for ").
			newBit("bit16", 16, 4, ReadWriteType.WO, "Stop test when bit it is on").
			newBit("bit2", 2, 4, ReadWriteType.WO, "Stop test when bit it is on");
		
		SvRegister reg = holder.getRegisterByName("test control");
		RegisterBit bit1 = reg.getBits().getBits().get(0);
		assertEquals("bit16", bit1.getName());
		assertEquals("19:16", bit1.getBit());
		RegisterBit bit2 = reg.getBits().getBits().get(1);
		assertEquals("bit8", bit2.getName());
		assertEquals("11:8", bit2.getBit());
		RegisterBit bit3 = reg.getBits().getBits().get(2);
		assertEquals("bit2", bit3.getName());
		assertEquals("5:2", bit3.getBit());
		RegisterBit bit4 = reg.getBits().getBits().get(3);
		assertEquals("bit1", bit4.getName());
		assertEquals("1:1", bit4.getBit());
		RegisterBit bit5 = reg.getBits().getBits().get(4);
		assertEquals("bit0", bit5.getName());
		assertEquals("0:0", bit5.getBit());
		
		// add bit
		{
			reg.addBit("bit5", 6, 7, ReadWriteType.RO, "", "");
			RegisterBit addedBit = reg.getBits().getBits().get(2);
			assertEquals("bit5", addedBit.getName());
			assertEquals("7:6", addedBit.getBit());
			
			reg.addBit("add", ReadWriteType.RW, "", "");
			assertEquals("15:12", reg.getBit("add").getBit());
			
			reg.addBit();
			assertEquals("31:20", reg.getBits().getBits().get(0).getBit());
			
			
			reg.getBits().remove("bit5");
			reg.addBit();
			assertTrue(reg.getBits().getBits().get(4).getName().startsWith("NEW"));
			assertEquals("7:6", reg.getBits().getBits().get(4).getBit());
			reg.getBits().remove(0);
		}
		// remove bit
		{
			assertEquals(7, reg.getBits().getBits().size());
			List<Integer> indexes = Arrays.asList(0, 1);
			reg.getBits().removeAll(indexes);
			assertEquals(5, reg.getBits().getBits().size());
			assertEquals("add", reg.getBits().getBits().get(0).getName());
			reg.getBits().remove(4);
			assertEquals("bit1", reg.getBits().getBits().get(reg.getBits().getBits().size()-1).getName());
		}
		// update bit
		{
			reg.getBit("add").setBit("0:0");
			assertEquals("add", reg.getBits().getBits().get(reg.getBits().getBits().size()-1).getName());
			
		}
		// insert bit
		{
			
		}
				
	}

	@Test
	public void testPersistent() {
		RegisterSpecHolder holder = new RegisterSpecHolder(32);
		holder.newRegister("register 1", 0x10, "register 1 description").
			newBit("bit0", 0, 1, ReadWriteType.RW, "bit0").
			newBit("bit1", 16, 31, ReadWriteType.RO, "bit1");
		
		holder.newRegister("register 2", 0x20, "register 2 description").
			newBit("bit4", 0, 1, ReadWriteType.RW, "bit4 description").
			newBit("bit5", 3, 2, ReadWriteType.RW, "bit5 description");	
		
		holder.save(".");
		
		RegisterSpecHolder newHolder = new RegisterSpecHolder();
		newHolder.load(".");
		assertEquals(holder.getRegSize(), newHolder.getRegSize());
		assertEquals(holder.getRegisters().size(), newHolder.getRegisters().size());
		assertEquals(holder.getRegisters().keySet().iterator().next(), newHolder.getRegisters().keySet().iterator().next());

		assertEquals(holder.getRegisterList().get(0).getName(), newHolder.getRegisterList().get(0).getName());
		assertEquals(holder.getRegisterList().get(0).getAddress(), newHolder.getRegisterList().get(0).getAddress());
		assertEquals(holder.getRegisterList().get(0).getDecAddress(), newHolder.getRegisterList().get(0).getDecAddress());
		assertEquals(holder.getRegisterList().get(0).getDescription(), newHolder.getRegisterList().get(0).getDescription());
		assertEquals(holder.getRegisterList().get(0).getBits().getBits().get(0).getName(), newHolder.getRegisterList().get(0).getBits().getBits().get(0).getName());
		assertEquals(holder.getRegisterList().get(0).getBits().getBits().get(0).getBit(), newHolder.getRegisterList().get(0).getBits().getBits().get(0).getBit());
		assertEquals(holder.getRegisterList().get(0).getBits().getBits().get(0).getSize(), newHolder.getRegisterList().get(0).getBits().getBits().get(0).getSize());
		assertEquals(holder.getRegisterList().get(0).getBits().getBits().get(0).getDescription(), newHolder.getRegisterList().get(0).getBits().getBits().get(0).getDescription());
		assertEquals(holder.getRegisterList().get(0).getBits().getBits().get(0).getType(), newHolder.getRegisterList().get(0).getBits().getBits().get(0).getType());
		assertEquals(holder.getRegisterList().get(0).getBits().getBits().get(0).getStartBit(), newHolder.getRegisterList().get(0).getBits().getBits().get(0).getStartBit());
		assertEquals(holder.getRegisterList().get(0).getBits().getBits().get(0).getEndBit(), newHolder.getRegisterList().get(0).getBits().getBits().get(0).getEndBit());


	}
	
	@Test
	public void testGenerateSource() {
		RegisterSpecHolder holder = new RegisterSpecHolder(32);
		holder.newRegister("reg1", 0x10, "reg1").
			newBit("bit1", 0, 1, ReadWriteType.RW, "").
			newBit("bit2", 1, 1, ReadWriteType.RW, "");
		holder.newRegister("reg2", 0x14, "reg2").
			newBit("bit 3", 0, 1, ReadWriteType.RW, "").
			newBit("bit$4", 1, 1, ReadWriteType.RW, "");
		
		RegisterSourceGenerator generator = new RegisterSourceGenerator(holder);
//		generator.exportFile("src\\test\\java", "jp.silverbullet.register");
		
//		assertTrue(Files.exists(Paths.get("src/test/java/jp/silverbullet/register/UserRegister.java")));
	}
	
	@Test
	public void testRegisterJsonController() {
		RegisterSpecHolder holder = new RegisterSpecHolder(32);
		holder.newRegister("reg1", 0x10, "reg1").
			newBit("bit1", 0, 1, ReadWriteType.RW, "").
			newBit("bit2", 1, 1, ReadWriteType.RW, "");
		holder.newRegister("reg2", 0x14, "reg2").
		newBit("bit3", 0, 1, ReadWriteType.RW, "").
		newBit("bit4", 1, 1, ReadWriteType.RW, "");
		
		RegisterJsonController controller = new RegisterJsonController(holder);
		
		{
			List<KeyValue> keyValue = new ArrayList<>();
			keyValue.add(new KeyValue("addr_0", "0x1234"));
			keyValue.add(new KeyValue("name_1", "reg222"));
			keyValue.add(new KeyValue("desc_1", "desc1111"));
			keyValue.add(new KeyValue("bit_1_bit_0", "1:1")); // bit2
			keyValue.add(new KeyValue("bit_1_size_0", "4")); // bit2
			keyValue.add(new KeyValue("bit_1_name_1", "new name"));  // bit1
			keyValue.add(new KeyValue("bit_1_type_1", "RO"));
			keyValue.add(new KeyValue("bit_1_desc_1", "new description"));
			controller.handle(keyValue.toArray(new KeyValue[0]));
			
			assertEquals("0x1234", holder.getRegisterByName("REG222").getAddress());
			assertEquals("desc1111", holder.getRegisterByName("REG222").getDescription());
			assertEquals("bit2", holder.getRegisterByName("REG222").getBits().getBits().get(0).getName());
			assertEquals("NEW_NAME", holder.getRegisterByName("REG222").getBits().getBits().get(1).getName());
			assertEquals("4:1", holder.getRegisterByName("REG222").getBits().getBits().get(0).getBit());
			assertEquals(4, holder.getRegisterByName("REG222").getBits().getBits().get(0).getSize());
			assertEquals(ReadWriteType.RO, holder.getRegisterByName("REG222").getBits().getBits().get(1).getType());
			assertEquals("new description", holder.getRegisterByName("REG222").getBits().getBits().get(1).getDescription());
		}
		
		{
			List<KeyValue> keyValue = new ArrayList<>();
			keyValue.add(new KeyValue("bit_1_bit_0", "")); // bit2
			controller.handle(keyValue.toArray(new KeyValue[0]));
			assertEquals(1, holder.getRegisterByName("REG222").getBits().getBits().size());
			assertEquals("NEW_NAME", holder.getRegisterByName("REG222").getBits().getBits().get(0).getName());
		}
	}
	
	@Test
	public void testMap() {
		RegisterSpecHolder holder = new RegisterSpecHolder(32);
		holder.newRegister(UserRuntimeRegisterHolderForTest.Register.Reg1.toString(), 0x10, "ret1").
			newBit(UserRuntimeRegisterHolderForTest.Reg1.bit1.toString(), 0, 1, ReadWriteType.RW, "").
			newBit(UserRuntimeRegisterHolderForTest.Reg1.bit2.toString(), 1, 1, ReadWriteType.RW, "");
		holder.newRegister(UserRuntimeRegisterHolderForTest.Register.Reg2.toString(), 0x14, "reg2").
			newBit(UserRuntimeRegisterHolderForTest.Reg1.bit1.toString(), 0, 1, ReadWriteType.RW, "").
			newBit(UserRuntimeRegisterHolderForTest.Reg1.bit2.toString(), 1, 1, ReadWriteType.RW, "");
		holder.newMultiRegister("area", 0x20, 0x80, "");
		//////////////// Map ////////////////
		RuntimeRegisterMap registerMap = new RuntimeRegisterMap();
		RegisterAccessorListenerImpl listener = new RegisterAccessorListenerImpl();
		registerMap.addListener(listener);
		
		UserRuntimeRegisterHolderForTest runtimeReg = new UserRuntimeRegisterHolderForTest(registerMap);
		runtimeReg.reg1.set(UserRuntimeRegisterHolderForTest.Reg1.bit1, 0x01).set(UserRuntimeRegisterHolderForTest.Reg1.bit2, 0x01).write();
		runtimeReg.reg2.set(UserRuntimeRegisterHolderForTest.Reg2.bit4, 0x023);

		runtimeReg.reg1.clear();
		
		runtimeReg.reg2.set(UserRuntimeRegisterHolderForTest.Reg2.bit3, 0x01);
		
		runtimeReg.reg1.set(UserRuntimeRegisterHolderForTest.Reg1.bit1, 0x01).set(UserRuntimeRegisterHolderForTest.Reg1.bit2, 0x01).write();
		assertEquals(0x01, registerMap.readRegister(
				UserRuntimeRegisterHolderForTest.Register.Reg1, 
				UserRuntimeRegisterHolderForTest.Reg1.bit1));
		
		assertEquals(0x01, registerMap.readRegister(UserRuntimeRegisterHolderForTest.Register.Reg1, 
				UserRuntimeRegisterHolderForTest.Reg1.bit2));
		
		runtimeReg.reg1.set(UserRuntimeRegisterHolderForTest.Reg1.bit1, 0x00).set(UserRuntimeRegisterHolderForTest.Reg1.bit2, 0x00).write();
		assertEquals(0x00, registerMap.readRegister(
				UserRuntimeRegisterHolderForTest.Register.Reg1, 
				UserRuntimeRegisterHolderForTest.Reg1.bit1));
		
		assertEquals(0x00, registerMap.readRegister(
				UserRuntimeRegisterHolderForTest.Register.Reg1, 
				UserRuntimeRegisterHolderForTest.Reg1.bit2));
		
		// simualtor
		RegisterController simulator = new RegisterController();
		registerMap.addDevice(DeviceType.SIMULATOR, simulator);

		assertEquals(0x00, registerMap.readRegister(
				UserRuntimeRegisterHolderForTest.Register.Reg1, 
				UserRuntimeRegisterHolderForTest.Reg1.bit1));
		simulator.updateValue(UserRuntimeRegisterHolderForTest.Register.Reg1, 
				UserRuntimeRegisterHolderForTest.Reg1.bit1, 0x01);
		assertEquals(0x01, registerMap.readRegister(
				UserRuntimeRegisterHolderForTest.Register.Reg1, 
				UserRuntimeRegisterHolderForTest.Reg1.bit1));
		
		assertEquals(UserRuntimeRegisterHolderForTest.Register.Reg1, listener.getLastChangedReg());
		assertEquals(UserRuntimeRegisterHolderForTest.Reg1.bit1, listener.getLastChangedBit());
		assertEquals(0x01, listener.getLastChangedValue());
		
		simulator.updateValue(UserRuntimeRegisterHolderForTest.Register.Reg2, 
				UserRuntimeRegisterHolderForTest.Reg2.bit3, 0x01);
		registerMap.readRegister(
				UserRuntimeRegisterHolderForTest.Register.Reg2, 
				UserRuntimeRegisterHolderForTest.Reg2.bit3);
		
		byte[] image = new byte[256];
		image[0] = 0x01;
		image[2] = 0x01;
		simulator.updateValue(UserRuntimeRegisterHolderForTest.Register.Reg3, image);
		byte[] ret = registerMap.readRegister(UserRuntimeRegisterHolderForTest.Register.Reg3);
		assertEquals(256, ret.length);
		assertEquals(0x01, ret[0]);
		assertEquals(0x00, ret[1]);
		
		assertEquals(false, listener.isInterruptFired());
		simulator.triggerInterrupt();
		assertEquals(true, listener.isInterruptFired());
		
		SvRegisterJsonHolder json = new SvRegisterJsonHolder(holder);
		assertEquals(UserRuntimeRegisterHolderForTest.Register.Reg1.toString(), json.getRegisters().get(0).getName());
	
		UserRuntimeRegisterHolderForTest user = new UserRuntimeRegisterHolderForTest(registerMap);
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(110);
				} catch (InterruptedException e) {
				}
				simulator.triggerInterrupt();
			}
			
		}.start();
		long now = new Date().getTime();
		user.waitInterrupt();
		
		assertTrue(new Date().getTime() - now > 100);
	}

	@Test
	public void testHardware() {
		RegisterSpecHolder holder = new RegisterSpecHolder(32);
		holder.newRegister(UserRuntimeRegisterHolderForTest.Register.Reg1.toString(), 0x10, "ret1").
			newBit(UserRuntimeRegisterHolderForTest.Reg1.bit1.toString(), 0, 1, ReadWriteType.RW, "").
			newBit(UserRuntimeRegisterHolderForTest.Reg1.bit2.toString(), 1, 1, ReadWriteType.RW, "");
		holder.newRegister(UserRuntimeRegisterHolderForTest.Register.Reg2.toString(), 0x14, "reg2").
			newBit(UserRuntimeRegisterHolderForTest.Reg1.bit1.toString(), 0, 1, ReadWriteType.RW, "").
			newBit(UserRuntimeRegisterHolderForTest.Reg1.bit2.toString(), 1, 1, ReadWriteType.RW, "");
		holder.newMultiRegister("area", 0x20, 0x80, "");
		//////////////// Map ////////////////
		RuntimeRegisterMap registerMap = new RuntimeRegisterMap();
		RegisterAccessorImpl simulator = new RegisterAccessorImpl();
		RegisterAccessorImpl hardware = new RegisterAccessorImpl();
		
		registerMap.addDevice(DeviceType.SIMULATOR, simulator);
		registerMap.addDevice(DeviceType.HARDWARE, hardware);
		
		registerMap.write(UserRuntimeRegisterHolderForTest.Register.Reg1, Arrays.asList(new BitValue(UserRuntimeRegisterHolderForTest.Reg1.bit1, 1)));
		assertEquals(true, simulator.isWritten());
		assertEquals(true, hardware.isWritten());
		
		registerMap.readRegister(UserRuntimeRegisterHolderForTest.Register.Reg1);
		assertEquals(false, simulator.isBlockRead());
		assertEquals(true, hardware.isBlockRead());

		registerMap.readRegister(UserRuntimeRegisterHolderForTest.Register.Reg1, UserRuntimeRegisterHolderForTest.Reg1.bit1);
		assertEquals(false, simulator.isRegRead());
		assertEquals(true, hardware.isRegRead());
		
	}
}

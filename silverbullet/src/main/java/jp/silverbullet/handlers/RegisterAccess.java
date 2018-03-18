package jp.silverbullet.handlers;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;

import jp.silverbullet.register.RegisterValueCalculator;

abstract class Converter {
	private ByteArrayOutputStream stream = new ByteArrayOutputStream();
	public Converter() {
		try {
			
			DataOutputStream out = new DataOutputStream(stream);
			handle(out);
			out.flush();
			out.close();	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	byte[] getData() {
		return stream.toByteArray();
	}
	abstract protected void handle(DataOutputStream out) throws IOException;
}

public class RegisterAccess {
	private SvDevice driver;

	public RegisterAccess(SvDevice deviceDriver) {
		this.driver = deviceDriver;
	}
	
	public void addInterruptHandler(InterruptHandler interruptHandler) {
		this.driver.addInterruptHandler(interruptHandler);
	}
	
	public void removeInteruptHandler(InterruptHandler interruptHandler) {
		this.driver.removeInterruptHandler(interruptHandler);
	}
	
	public void writeIo(long address, final boolean value, int bit) {
//		int i = value ? 1 : 0;
//		final Integer v = i << bit;

//		Converter converter = new Converter() {
//			@Override
//			protected void handle(DataOutputStream out) throws IOException {
//				out.writeInt(v);
//			}
//		};
		
//		byte[] d = converter.getData();
		
		BitSet data =new BitSet(32);
		data.set(bit, value);
		BitSet mask = new BitSet(32);
		mask.set(bit);
		driver.writeIo(address, data, mask);
	}

	public void writeIo(long address, int value, int bitFrom, int bitTo) {
//		final Integer v = value << bitFrom;
//		Converter converter = new Converter() {
//			@Override
//			protected void handle(DataOutputStream out) throws IOException {
//				out.writeInt(v);
//			}
//		};
//		
//		byte[] d = converter.getData();
		BitSet data = new BitSet(32);
	//	String bit = Integer.toBinaryString(value);
		//data = BitSet.valueOf(new long[]{value});
		for (int i = bitFrom; i <= bitTo; i++) {
			data.set(i, ((value >> (i-bitFrom)) & 0x01) == 0x01 ? true : false);
		}
		
		BitSet mask = new BitSet(32);
		for (int i = bitFrom; i <= bitTo; i++) {
			mask.set(i);
		}
		driver.writeIo(address, data, mask);
	}

	public void writeIo(long address, float value, int bitFrom, int bitTo) {
		
	}

	public BitSet readIo(long address) {
		return driver.readIo(address);
	}
	
	public boolean readIoBoolean(long address, int bit) {
		return driver.readIo(address).get(bit);
	}

	public int readIoInteger(long address, int bitFrom, int bitTo) {
		return Integer.valueOf(RegisterValueCalculator.getValue(bitFrom, bitTo, driver.readIo(address)));
	}

	public byte[] readBlock(long address, int size) {
		return driver.readBlock(address, size);
	}


}

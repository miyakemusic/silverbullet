package jp.silverbullet.core.register2;

import java.util.ArrayList;
import java.util.List;

public 	class RuntimeRegister<T> {
	private List<BitValue> cache = new ArrayList<>();
	private Object regName;
	private RegisterAccessor accessor;
	public RuntimeRegister(Object regName, RegisterAccessor accessor) {
		this.regName = regName;
		this.accessor = accessor;
	}
	public RuntimeRegister<T> set(T bit, int value) {
		cache.add(new BitValue(bit, value));
		return this;
	}

	public long read(T bit) {
		return accessor.readRegister(this.getRegName(), bit);
	}

	public long readAndReset(T bit) {
		long ret = this.read(bit);
		this.accessor.write(this.getRegName(), bit, 0x00);
		return ret;
	}
	
	public void write() {
		accessor.write(this.getRegName(), cache);
		cache.clear();
	}

	public void clear() {
		accessor.clear(this.getRegName());
	}
	
	private Object getRegName() {
		return this.regName;
	}
	public byte[] read() {
		return this.accessor.readRegister(this.regName);
	}

}

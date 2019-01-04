package jp.silverbullet.register2;

import java.util.ArrayList;
import java.util.List;

public 	class RuntimeRegister<T> {
	private List<BitValue> cache = new ArrayList<>();
	private String regName;
	private RegisterAccessor accessor;
	public RuntimeRegister(String regName, RegisterAccessor accessor) {
		this.regName = regName;
		this.accessor = accessor;
	}
	public RuntimeRegister<T> set(T bit, int value) {
		cache.add(new BitValue(bit.toString(), value));
		return this;
	}

	public int read(T bit) {
		return accessor.readRegister(this.getRegName(), bit.toString());
	}

	public void write() {
		accessor.write(this.getRegName(), cache);
		cache.clear();
	}

	public void clear() {
		accessor.clear(this.getRegName());
	}
	
	private String getRegName() {
		return this.regName;
	}

}

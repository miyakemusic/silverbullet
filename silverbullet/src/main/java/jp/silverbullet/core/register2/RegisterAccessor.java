package jp.silverbullet.core.register2;

import java.util.List;

public interface RegisterAccessor {

	void write(Object regName, List<BitValue> data);

	long readRegister(Object regName, Object bitName);

	void clear(Object regName);

	void addListener(RegisterAccessorListener listener);

	byte[] readRegister(Object regName);

	void write(Object regName, Object bitName, int value);
}

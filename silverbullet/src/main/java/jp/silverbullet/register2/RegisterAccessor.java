package jp.silverbullet.register2;

import java.util.List;

public interface RegisterAccessor {

	void write(Object regName, List<BitValue> data);

	long readRegister(Object regName, Object bitName);

	void clear(Object regName);

	void addListener(RegisterAccessorListener listener);

	byte[] readRegister(Object regName);
}

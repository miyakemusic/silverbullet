package jp.silverbullet.register2;

import java.util.List;

public class RuntimeRegisterHolder {
	protected RegisterAccessor registerAccess;

	public RuntimeRegisterHolder(RegisterAccessor registerAccess) {
		this.registerAccess = registerAccess;
	}
	
	protected RegisterAccessor accessor = new RegisterAccessor() {
		@Override
		public void write(Object regName, List<BitValue> data) {
			registerAccess.write(regName, data);
		}

		@Override
		public int readRegister(Object regName, Object bitName) {
			return registerAccess.readRegister(regName, bitName);
		}

		@Override
		public void clear(Object regName) {
			registerAccess.clear(regName);
		}

		@Override
		public void addListener(RegisterAccessorListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public byte[] readRegister(Object regName) {
			return registerAccess.readRegister(regName);
		}
	
	};
}

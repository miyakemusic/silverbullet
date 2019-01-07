package jp.silverbullet.register2;

import java.util.List;

public class RuntimeRegisterHolder {
	protected RegisterAccessor registerAccessor;
	private Object sync = new Object();
	
	public RuntimeRegisterHolder(RegisterAccessor registerAccessor) {
		this.registerAccessor = registerAccessor;
		this.registerAccessor.addListener(new RegisterAccessorListener() {

			@Override
			public void onUpdate(Object regName, Object bitName, int value) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onUpdate(Object regName, byte[] image) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onInterrupt() {
				synchronized(sync) {
					sync.notifyAll();
				}
			}
			
		});
	}
	
	protected RegisterAccessor accessor = new RegisterAccessor() {
		@Override
		public void write(Object regName, List<BitValue> data) {
			registerAccessor.write(regName, data);
		}

		@Override
		public int readRegister(Object regName, Object bitName) {
			return registerAccessor.readRegister(regName, bitName);
		}

		@Override
		public void clear(Object regName) {
			registerAccessor.clear(regName);
		}

		@Override
		public void addListener(RegisterAccessorListener listener) {
			
		}

		@Override
		public byte[] readRegister(Object regName) {
			return registerAccessor.readRegister(regName);
		}
	
	};
	
	public void waitInterrupt() {
		try {
			synchronized(sync) {
				sync.wait();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

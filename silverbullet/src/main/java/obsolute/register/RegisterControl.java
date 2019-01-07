package obsolute.register;

import jp.silverbullet.handlers.InterruptHandler;

public class RegisterControl {
    private Object lock = new Object();
	private RegisterAccess registerAccess;
    
    public RegisterControl(RegisterAccess registerAccess) {
    	this.registerAccess = registerAccess;
    	
	    InterruptHandler interruptHandler = new InterruptHandler() {
	        @Override
	        public void onTrigger() {
	            synchronized(lock) {
	    	        lock.notify();
	            }
	        }  
	    };
	    this.registerAccess.addInterruptHandler(interruptHandler);
    }
    
    public void waitIntrrupt() {    		  
    	synchronized(lock) {
    		try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }
    
    protected boolean readIoBoolean(long address, int bit) {
        return registerAccess.readIoBoolean(address, bit);
    }
    protected int readIoInteger(long address, int bitFrom, int bitTo) {
        return registerAccess.readIoInteger(address, bitFrom, bitTo);
    }
    protected void writeIo(long address, boolean value, int bit) {
        registerAccess.writeIo(address, value, bit);
    }
    protected void writeIo(long address, int value, int bitFrom, int bitTo) {
        registerAccess.writeIo(address, value, bitFrom, bitTo);
    }
    protected void writeIo(long address, float value, int bitFrom, int bitTo) {
        registerAccess.writeIo(address, value, bitFrom, bitTo);
    }
}

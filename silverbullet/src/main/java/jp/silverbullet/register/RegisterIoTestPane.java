package jp.silverbullet.register;

import java.util.BitSet;

import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import jp.silverbullet.handlers.InterruptHandler;
import jp.silverbullet.handlers.RegisterAccess;
import jp.silverbullet.handlers.SvDevice;

public abstract class RegisterIoTestPane extends VBox {

	public RegisterAccess getRegisterAccess() {
		return registerAccess;
	}

	private SvDevice driver = new SvDevice() {

		@Override
		public BitSet readIo(long address) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int writeIo(long address, BitSet data, BitSet mask) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void addInterruptHandler(InterruptHandler interruptHandler) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public byte[] readBlock(long address, int size) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void removeInterruptHandler(InterruptHandler interruptHandler) {
			// TODO Auto-generated method stub
			
		}
		
	};
	private RegisterAccess registerAccess = new RegisterAccess(driver);
	
//		@Override
//		public void writeIo(long address, boolean value, int bit) {
//			int i = value ? 1 : 0;
//			Integer v = i << bit;
//			listView.getItems().add(toHex(address) + ":\t -> " + toHex(v));
//		}
//
//		@Override
//		public void writeIo(long address, int value, int bitFrom, int bitTo) {
//			Integer v = value << bitFrom;
//			listView.getItems().add(toHex(address) + ":\t -> " + toHex(v));
//		}
//
//		@Override
//		public void writeIo(long address, float value, int bitFrom, int bitTo) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public boolean readIoBoolean(long address, int bit) {
//			// TODO Auto-generated method stub
//			return false;
//		}
//
//		@Override
//		public int readIoInteger(long address, int bitFrom, int bitTo) {
//			// TODO Auto-generated method stub
//			return 0;
//		}
		
	private ListView<String> listView;
	
	public RegisterIoTestPane() {
		listView = new ListView<>();
		this.getChildren().add(listView);
	}

	protected String toHex(long address) {
		String hex = String.format("0x%04X", address);
		
		for (SvRegister register : this.getRegisterProperty().getRegisters()) {
			if (register.getAddress().equals(hex)) {
				return hex + " (" + register.getName() + ")";
			}
		}
		return hex;
		//return "0x" + Long.toHexString(address);
	}

	abstract protected RegisterProperty getRegisterProperty();
}

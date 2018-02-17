package jp.silverbullet.register;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class NullSimulator extends SvSimulator {

	
	
	@Override
	protected void writeIo(long address, BitSet data, BitSet mask) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void writeBlock(long address, byte[] data) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			FileBasedBlock object = (FileBasedBlock)ois.readObject();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

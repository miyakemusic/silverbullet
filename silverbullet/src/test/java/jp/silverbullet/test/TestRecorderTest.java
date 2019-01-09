package jp.silverbullet.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import jp.silverbullet.SequencerListener;
import jp.silverbullet.dependency2.RequestRejectedException;
import jp.silverbullet.property2.PropertyDef2;
import jp.silverbullet.property2.PropertyFactory;
import jp.silverbullet.property2.PropertyHolder2;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.property2.RuntimePropertyStore;
import jp.silverbullet.register2.BitValue;
import jp.silverbullet.register2.RegisterAccessor;
import jp.silverbullet.register2.RegisterAccessorListener;
import jp.silverbullet.register2.RegisterController;

public class TestRecorderTest {

	@Test
	public void test() {
		PropertyFactory factory = new PropertyFactory();
		PropertyHolder2 def = new PropertyHolder2();
		def.addProperty(factory.createBoolean("ID_BOOLEAN").title("Boolean2").defaultValue(PropertyDef2.True));
		def.addProperty(factory.createNumeric("ID_NUMERIC").defaultValue(200).decimals(2));
		RuntimePropertyStore store = new RuntimePropertyStore(def);
		
		RegisterController registerController = new RegisterController();
		List<String> log = new ArrayList<>();
		
		registerController.addListener(new RegisterAccessorListener() {

			@Override
			public void onUpdate(Object regName, Object bitName, int value) {
				log.add(regName.toString() + "." + bitName.toString() + "." + value);
			}

			@Override
			public void onUpdate(Object regName, byte[] image) {
				log.add(regName.toString() + "." + new String(image));
			}

			@Override
			public void onInterrupt() {
				log.add("INTERRUPT");
			}
			
		});
		TestRecorderInterface testRecorderInterface = new TestRecorderInterface() {

			@Override
			public void saveParameters(String string) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void requestChange(String id, String value) throws RequestRejectedException {
				log.add(id + "." + value);
			}

			@Override
			public List<RuntimeProperty> getProperties() {
				return store.getAllProperties();
			}

			@Override
			public RuntimeProperty getProperty(String id) {
				return store.get(id);
			}

			@Override
			public int getRegisterValue(String regName, String bitName) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public RegisterController getRegisterController() {
				return registerController;
			}
			
		};
		Object sync = new Object();
		
		TestRecorder testRecorder = new TestRecorder(testRecorderInterface);
		testRecorder.addListener(new TestRecorderListener() {
			@Override
			public void onTestFinished() {
				synchronized(sync) {
					sync.notifyAll();
				}
			}

			@Override
			public void onTestStart() {
				// TODO Auto-generated method stub
				
			}
		});
		SequencerListener sequencerListener = testRecorder;
		RegisterAccessorListener registerAccessorListener = testRecorder;
		
		testRecorder.startRecording(); // start recording
		sequencerListener.onChangedByUser("ID_CONTROL", "ID_CONTROL_START");
		registerAccessorListener.onUpdate("REG1", "BIT1", 1);
		byte[] image = new byte[2];
		//image[0] = 0x01; image[1] = 0x02;
		registerAccessorListener.onUpdate("REG2", "image".getBytes());
		registerAccessorListener.onInterrupt();
		testRecorder.stopRecording();
		
		testRecorder.playBack();
		
		synchronized(sync) {
			try {
				sync.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		assertEquals("ID_CONTROL.ID_CONTROL_START", log.get(0));
		assertEquals("REG1.BIT1.1", log.get(1));
		assertEquals("REG2.image", log.get(2));
		assertEquals("INTERRUPT", log.get(3));
	}

}

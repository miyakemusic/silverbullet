package jp.silverbullet.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import jp.silverbullet.core.dependency2.RequestRejectedException;
import jp.silverbullet.core.property2.PropertyDef2;
import jp.silverbullet.core.property2.PropertyFactory;
import jp.silverbullet.core.property2.PropertyHolder2;
import jp.silverbullet.core.property2.RuntimeProperty;
import jp.silverbullet.core.property2.RuntimePropertyStore;
import jp.silverbullet.core.register2.BitValue;
import jp.silverbullet.core.register2.RegisterAccessor;
import jp.silverbullet.core.register2.RegisterAccessorListener;
import jp.silverbullet.core.register2.RegisterController;
import jp.silverbullet.core.sequncer.SequencerListener;
import jp.silverbullet.dev.test.TestRecorder;
import jp.silverbullet.dev.test.TestRecorderInterface;
import jp.silverbullet.dev.test.TestRecorderListener;
import jp.silverbullet.dev.test.TestResultItem.PassFail;

public class TestRecorderTest {
	private Map<String, Integer> values = new HashMap<>();

	class RegisterAccessorListenerImpl implements RegisterAccessorListener {
		private List<String> log;
		
		public RegisterAccessorListenerImpl(List<String> log) {
			this.log = log;
		}

		@Override
		public void onUpdate(Object regName, Object bitName, int value) {
			log.add(regName.toString() + "." + bitName.toString() + "." + value);
			values.put(regName.toString()+bitName.toString(), value);
		}

		@Override
		public void onUpdate(Object regName, byte[] image) {
			log.add(regName.toString() + "." + new String(image));
		}

		@Override
		public void onInterrupt() {
			log.add("INTERRUPT");
		}		
	}
	
	class TestRecorderInterfaceImpl implements TestRecorderInterface {
		private List<String> log;
		private RegisterController registerControl;
		private RuntimePropertyStore store;

		public TestRecorderInterfaceImpl(List<String> log, RuntimePropertyStore store, RegisterController registerController) {
			this.log = log;
			this.store = store;
			this.registerControl = registerController;
		}

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
		public long getRegisterValue(String regName, String bitName) {
			return values.get(regName.toString() + bitName.toString());
		}

		@Override
		public RegisterController getRegisterController() {
			return registerControl;
		}
	}
	
	class TestRecorderListenerImpl implements TestRecorderListener {
		private List<String> log;

		public TestRecorderListenerImpl(List<String> log) {
			this.log = log;
		}
		@Override
		public void onTestFinished() {
			this.log.add("onTestFinished");
		}

		@Override
		public void onTestStart() {
			this.log.add("onTestStart");
		}

		@Override
		public void onAdd(String string) {
			this.log.add(string);
		}
		@Override
		public void onUpdate() {
			this.log.add("onUpdate");
		}
		
	};
	
	@Test
	public void testRecord() {
		PropertyFactory factory = new PropertyFactory();
		PropertyHolder2 def = new PropertyHolder2();
		def.addProperty(factory.createBoolean("ID_BOOLEAN").title("Boolean2").defaultValue(PropertyDef2.True));
		def.addProperty(factory.createNumeric("ID_NUMERIC").defaultValue(200).decimals(2));
		RuntimePropertyStore store = new RuntimePropertyStore(def);
		
		RegisterController registerController = new RegisterController();
		List<String> log = new ArrayList<>();
		
		registerController.addListener(new RegisterAccessorListenerImpl(log));
		TestRecorderInterface testRecorderInterface = new TestRecorderInterfaceImpl(log, store, registerController);
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
			}

			@Override
			public void onAdd(String string) {
			}

			@Override
			public void onUpdate() {
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
		
		testRecorder.addRegisterQuery("REG1", "BIT1", 1);
		testRecorder.addRegisterQuery("REG1", "BIT1", 0);
		testRecorder.addPropertyTest("ID_NUMERIC");
		
		assertEquals("PROPERTY.ID_CONTROL..ID_CONTROL_START", testRecorder.getScript(0).toString());
		assertEquals("REGISTER.REG1::BIT1..1", testRecorder.getScript(1).toString());
		assertEquals("REGISTER.REG2..file:REG2_0.bin", testRecorder.getScript(2).toString());
		assertEquals("REGISTER.*INTERRPT*..ON", testRecorder.getScript(3).toString());
		assertEquals("REGISTER_TEST.REG1::BIT1?.1.", testRecorder.getScript(4).toString());
		assertEquals("REGISTER_TEST.REG1::BIT1?.0.", testRecorder.getScript(5).toString());
		assertEquals("PROPERTY_TEST.ID_NUMERIC?.200.00.", testRecorder.getScript(6).toString());
		
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
		
		assertEquals("REGISTER_TEST.REG1::BIT1?.1.", testRecorder.getScript(4).toString());
		assertEquals(PassFail.PASS, testRecorder.getResult(testRecorder.getScript(4).getSerial()).getPassFail());
		assertEquals(PassFail.FAIL, testRecorder.getResult(testRecorder.getScript(5).getSerial()).getPassFail());
	
		testRecorder.save("tmp.register.test");
		
		TestRecorder testRecorder2 = new TestRecorder(testRecorderInterface);
		testRecorder2.loadTest("tmp.register.test");
		assertEquals("PROPERTY.ID_CONTROL..ID_CONTROL_START", testRecorder2.getScript(0).toString());
		assertEquals("REGISTER.REG1::BIT1..1", testRecorder2.getScript(1).toString());
		assertEquals("REGISTER.REG2..file:REG2_0.bin", testRecorder2.getScript(2).toString());
		assertEquals("REGISTER.*INTERRPT*..ON", testRecorder2.getScript(3).toString());
		assertEquals("REGISTER_TEST.REG1::BIT1?.1.", testRecorder2.getScript(4).toString());
		assertEquals("REGISTER_TEST.REG1::BIT1?.0.", testRecorder2.getScript(5).toString());

		testRecorder2.updateValue(testRecorder2.getScript(0).getSerial(), "ID_CONTROL_STOP");
		assertEquals("PROPERTY.ID_CONTROL..ID_CONTROL_STOP", testRecorder2.getScript(0).toString());
		
		testRecorder2.updateValue(testRecorder2.getScript(1).getSerial(), "3");
		assertEquals("REGISTER.REG1::BIT1..3", testRecorder2.getScript(1).toString());
		
		testRecorder2.updateExpected(testRecorder2.getScript(1).getSerial(), "3");
		assertEquals("REGISTER.REG1::BIT1.3.3", testRecorder2.getScript(1).toString());
	}

	@Test
	public void testNotRecordingIfNotStarted() {
//		PropertyFactory factory = new PropertyFactory();
		PropertyHolder2 def = new PropertyHolder2();
		RuntimePropertyStore store = new RuntimePropertyStore(def);
		
		RegisterController registerController = new RegisterController();
		List<String> log = new ArrayList<>();
		
		registerController.addListener(new RegisterAccessorListenerImpl(log));
		TestRecorderInterface testRecorderInterface = new TestRecorderInterfaceImpl(log, store, registerController);
		
		TestRecorder testRecorder = new TestRecorder(testRecorderInterface);
	
		testRecorder.addListener(new TestRecorderListenerImpl(log));
		SequencerListener sequencerListener = testRecorder;
		RegisterAccessorListener registerAccessorListener = testRecorder;
		
		sequencerListener.onChangedByUser("ID_CONTROL", "ID_CONTROL_START");
		registerAccessorListener.onUpdate("REG1", "BIT1", 1);
		byte[] image = new byte[2];
		registerAccessorListener.onUpdate("REG2", "image".getBytes());
		registerAccessorListener.onInterrupt();
		
		assertEquals(0, log.size());
	}
	
	@Test
	public void testEdit() throws Exception {
		PropertyFactory factory = new PropertyFactory();
		PropertyHolder2 def = new PropertyHolder2();
		def.addProperty(factory.createBoolean("ID_BOOLEAN").title("Boolean2").defaultValue(PropertyDef2.True));
		def.addProperty(factory.createList("ID_LIST").option("ID_LIST_1", "1", "").option("ID_LIST_2", "2", "").defaultId("ID_LIST_1"));
		def.addProperty(factory.createNumeric("ID_NUMERIC").defaultValue(200).decimals(2));
		RuntimePropertyStore store = new RuntimePropertyStore(def);
		RegisterController registerController = new RegisterController();
		List<String> log = new ArrayList<>();
		TestRecorderInterface testRecorderInterface = new TestRecorderInterfaceImpl(log, store, registerController);
		
		TestRecorder testRecorder = new TestRecorder(testRecorderInterface);
		assertEquals(0, testRecorder.getScript().getScripts().size());
		testRecorder.addListener(new TestRecorderListenerImpl(log));
		testRecorder.addPropertyCommand("ID_BOOLEAN");
		assertEquals(1, testRecorder.getScript().getScripts().size());
		testRecorder.addPropertyTest("ID_LIST");
		testRecorder.addRegisterQuery("reg1", "bit1", 1);
		assertEquals("PROPERTY.ID_BOOLEAN..true", log.get(0));		
		assertEquals("PROPERTY_TEST.ID_LIST?.ID_LIST_1.", log.get(1));
		assertEquals("REGISTER_TEST.reg1::bit1?.1.", log.get(2));
		
		assertEquals(3, testRecorder.getScript().getScripts().size());
		long serial = testRecorder.getScript().getScripts().get(1).getSerial();
		testRecorder.moveUp(serial);
		assertEquals(serial, testRecorder.getScript(0).getSerial());
		testRecorder.moveDown(serial);
		assertEquals(serial, testRecorder.getScript(1).getSerial());
		testRecorder.moveDown(serial);
		assertEquals(serial, testRecorder.getScript(2).getSerial());
		testRecorder.moveDown(serial);
		assertEquals(serial, testRecorder.getScript(2).getSerial());


	}
}

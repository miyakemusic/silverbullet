package jp.silverbullet.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.Zip;
import jp.silverbullet.dependency2.RequestRejectedException;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.register2.RegisterAccessorListener;
import jp.silverbullet.register2.RegisterController;
import jp.silverbullet.sequncer.SequencerListener;

public class TestRecorder implements SequencerListener, RegisterAccessorListener {
	private static final String TEST_FOLDER = "testdata/";
	
	private TestScript script = new TestScript();
	private TestResult result = new TestResult(script);
	private boolean redording;
	private TestRecorderInterface testRecorderInterface;
	private RegisterController registerContoller;

	private Set<TestRecorderListener> listeners = new HashSet<>();

	private long currentRowSerial = -1;
	
	public TestRecorder(TestRecorderInterface testRecorderInterface) {
		this.testRecorderInterface = testRecorderInterface;
		if (!Files.exists(Paths.get(TEST_FOLDER))) {      
            try {
				Files.createDirectory(Paths.get(TEST_FOLDER));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		registerContoller = testRecorderInterface.getRegisterController();

//		load();
	}

	private void load() {
		if (!Files.exists(Paths.get(TEST_FOLDER + "test.json"))) {
			return;
		}
		this.script = this.loadScript(TEST_FOLDER + "test.json");
		this.result = new TestResult(this.script);
	}

	@Override
	public void onChangedBySystem(String id, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onChangedByUser(String id, String value) {
		if (this.redording) {
			this.script.add(new TestItem(TestItem.TYPE_PROPERTY, id, value));
		}
	}

	@Override
	public void onInterrupt() {
		if (this.redording) {
			this.script.add(new TestItem(TestItem.TYPE_REGISTER, TestItem.INTERRPT, "ON"));
		}
	}

	@Override
	public void onUpdate(Object regName, Object bitName, int value) {
		if (!this.redording) {
			return;
		}
		this.script.add(new TestItem(TestItem.TYPE_REGISTER, regName.toString() + "::" + bitName.toString(), String.valueOf(value)));
	}

	@Override
	public void onUpdate(Object regName, byte[] image) {
		if (!this.redording) {
			return;
		}
		this.script.add(new TestItem(TestItem.TYPE_REGISTER, regName.toString(), TestItem.FILE + createBinaryFile(regName.toString(), image)));
		
	}

	private Map<String, Integer> binaryCounter = new HashMap<>();
	private String createBinaryFile(String regName, byte[] image) {
		if (!binaryCounter.containsKey(regName)) {
			binaryCounter.put(regName, 0);
		}
		String filename = regName + "_" + binaryCounter.get(regName) + ".bin";
		
		try {
			if (Files.exists(Paths.get(TEST_FOLDER + filename))) {
				Files.delete(Paths.get(TEST_FOLDER + filename));
			}
			Files.write(Paths.get(TEST_FOLDER + filename), image, StandardOpenOption.CREATE_NEW);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		binaryCounter.put(regName, binaryCounter.get(regName) + 1);
		
		return filename;
	}
//	
//	private String convertBlockData(String val) {
//		String str = val.replace("data:application/octet-stream;base64,", "");
//		String filename = "blockdata-" + String.valueOf(Calendar.getInstance().getTime().getTime() + ".block");
//		try {
//			Files.write(Paths.get(TEST_FOLDER + filename), Base64.decode(str));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		String ret = TestItem.FILE + filename;
//		return ret;
//	}

	public void startRecording() {
		this.script.clear();
		this.currentRowSerial = -1;
		try {
			FileUtils.cleanDirectory(new File(TEST_FOLDER));
		} catch (IOException e) {
			e.printStackTrace();
		}

		createSpanShot();
		this.redording = true;
	}

	private void createSpanShot() {
		this.testRecorderInterface.saveParameters(TEST_FOLDER + "snapshot.xml");
	}

	public void stopRecording() {
		this.redording = false;
		this.result = new TestResult(this.script);
	}

	private void addQueryTest(RuntimeProperty prop) {
		TestItem test = new TestItem(TestItem.TYPE_PROPERTY_TEST, prop.getId() + "?", "", prop.getCurrentValue()); 
		this.script.add(test, this.currentRowSerial);
		
		this.listeners.forEach(listener -> listener.onAdd(test.toString()));
	}

	public void overwrite() {
		saveScript(TEST_FOLDER + "test.json");
	}
	
	private TestScript loadScript(String filename) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			TestScript ret = mapper.readValue(new File(filename), TestScript.class);
			return ret;
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return new TestScript();
	}
	
	private void saveScript(String filename) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String s = mapper.writeValueAsString(script);
			Files.write(Paths.get(filename), Arrays.asList(s));
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void playBack() {
		this.result = new TestResult(this.script);
		fireTestStart();
		new Thread() {
			@Override
			public void run() {
				for (TestItem item : script.getScripts()) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							handle(item);
						}
					});
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						fireTestFinished();
					}

				});
			}
			
		}.start();
		
	}

	private void fireTestFinished() {
		this.listeners.forEach(listener -> listener.onTestFinished());
	}
	private void fireTestStart() {
		this.listeners.forEach(listener -> listener.onTestStart());
	}

	public void handle(TestItem item) {
		if (item.getType().equals(TestItem.TYPE_PROPERTY)) {
			requestChange(item);
		}
		else if (item.getType().equals(TestItem.TYPE_PROPERTY_TEST)) {
			RuntimeProperty prop = this.testRecorderInterface.getProperty(item.getTarget().replace("?", ""));
			this.result.addResult(item.getSerial(), prop.getCurrentValue(), item.getExpected().equals(prop.getCurrentValue()));
		}
		else if (item.getType().equals(TestItem.TYPE_REGISTER)) {
			String tmp[] = item.getTarget().split("::");
			String regName = tmp[0];
			if (item.isFile()) {
				String filename = item.blockFilename();
				try {
					byte[] data = Files.readAllBytes(Paths.get(TEST_FOLDER + filename));
					writeBlockData(regName, data);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if (item.isInterrupt()) {
				triggerInterrupt();
			}
			else {
				String bitName = tmp[1];
				String bitValue = item.bitValue();
				writeRegister(regName, bitName, Integer.valueOf(bitValue));
			}
		}
		else if (item.getType().equals(TestItem.TYPE_REGISTER_TEST)) {
			String[] tmp = item.getTarget().split("::");
			String regName = tmp[0];
			String bitName = tmp[1].replace("?", "");
			long value = testRecorderInterface.getRegisterValue(regName, bitName);
			this.result.addResult(item.getSerial(), String.valueOf(value), value == Long.valueOf(item.getExpected()));
		}
		else if (item.getType().equals(TestItem.TYPE_CONTROL)) {
			if (item.getTarget().equals("WAIT")) {
				try {
					Thread.sleep(Integer.valueOf(item.getValue()));
				} catch (NumberFormatException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void writeRegister(String regName, String bitName, Integer value) {
		registerContoller.updateValue(regName, bitName, value);
//		this.registerListeners.forEach(listener -> listener.onUpdate(regName, bitName, value));
	}

	private void updateRegister(String regName, String bitName, String value) {
		this.onUpdate(regName, bitName, Integer.valueOf(value));
	}

	private void triggerInterrupt() {
		registerContoller.triggerInterrupt();
	}

	private void writeBlockData(String regName, byte[] image) {
		this.registerContoller.updateValue(regName, image);
	}

	private void requestChange(TestItem item) {
		try {
			testRecorderInterface.requestChange(item.getTarget(), item.getValue());
		} catch (RequestRejectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public TestScript getScript() {
		return script;
	}

	public TestResult getResult() {
		return this.result;
	}

	public void addListener(TestRecorderListener listener) {
		this.listeners.add(listener);
	}

	public void remove(long serial) {
		this.script.remove(serial);
	}

	private TestItem getItem(long serial) {
		for (TestItem item : this.script.getScripts()) {
			if (item.getSerial() == serial) {
				return item;
			}
		}	
		return null;
	}
	
	public void updateValue(long serial, String value) {
		this.getItem(serial).setValue(value);
	}

	public void addPropertyTest(String id) {
		addQueryTest(this.testRecorderInterface.getProperty(id));
	}

	public void addPropertyCommand(String id) {
		RuntimeProperty prop = this.testRecorderInterface.getProperty(id);
		TestItem test = new TestItem(TestItem.TYPE_PROPERTY, prop.getId(), prop.getCurrentValue(), ""); 
		this.script.add(test, this.currentRowSerial);
		
		this.listeners.forEach(listener -> listener.onAdd(test.toString()));
	}
		
	public void moveUp(long serial) {
		this.script.moveUp(serial);
		this.listeners.forEach(listener -> listener.onUpdate());
	}
	
	public void moveDown(long serial) {
		this.script.moveDown(serial);
		this.listeners.forEach(listener -> listener.onUpdate());
	}

	public void addRegisterQuery(String regName, String bitName, long value) {
		TestItem test = new TestItem(TestItem.TYPE_REGISTER_TEST, regName + "::" + bitName + "?", "", String.valueOf(value));
		this.script.add(test, this.currentRowSerial);
		this.listeners.forEach(listener -> listener.onAdd(test.toString()));
	}

	public void save(String testName) {
		this.overwrite();
		if (!testName.toUpperCase().endsWith(".TEST")) {
			testName += ".test";
		}
		if (Files.exists(Paths.get(testName))) {
			try {
				Files.delete(Paths.get(testName));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Zip.zip(TestRecorder.TEST_FOLDER, testName);
	}

	public void updateExpected(long serial, String value) {
		this.getItem(serial).setExpected(value);
	}

	public List<String> getTestList() {
		String[] extensions = new String[] { "test" };
		Collection<File> files = FileUtils.listFiles(new File("."), extensions, true);
		List<String> ret = new ArrayList<>();
		for (File file : files) {
			ret.add(file.getName());
		}
		return ret;
	}

	public void loadTest(String testName) {
		try {
			FileUtils.cleanDirectory(new File(TEST_FOLDER));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Zip.unzip(testName, TEST_FOLDER);
		load();
	}

	public void selectRow(long serial) {
		this.currentRowSerial  = serial;
	}

	public TestItem getScript(int i) {
		return this.script.getScripts().get(i);
	}

	public TestResultItem getResult(long serial) {
		return this.result.getResult().get(serial);
	}

	public void changeId(String prevId, String newId) {
		this.script.changeId(prevId, newId);
	}

}

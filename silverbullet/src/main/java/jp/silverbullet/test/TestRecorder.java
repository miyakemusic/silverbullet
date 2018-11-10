package jp.silverbullet.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.core.util.Base64;

import jp.silverbullet.SequencerListener;
import jp.silverbullet.StaticInstances;
import jp.silverbullet.SvProperty;
import jp.silverbullet.Zip;
import jp.silverbullet.dependency.RequestRejectedException;
import jp.silverbullet.register.BitUpdates;
import jp.silverbullet.register.RegisterInfo;
import jp.silverbullet.register.RegisterMapListener;
import jp.silverbullet.register.RegisterUpdates;
import jp.silverbullet.register.SvSimulator;

public class TestRecorder implements SequencerListener, RegisterMapListener {
	private static final String TEST_FOLDER = "testdata/";
	
	private TestScript script = new TestScript();
	private TestResult result = new TestResult(script);
	private boolean redording;
	private TestRecorderInterface testRecorderInterface;
	private SvSimulator simulator;

	
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
		simulator = testRecorderInterface.createSimulator();
		
		load();
	}

	private void load() {
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
	public void onUpdate(RegisterUpdates updates) {

	}

	@Override
	public void onInterrupt() {
		if (this.redording) {
//			this.script.add(new TestItem(TestItem.TYPE_CONTROL, TestItem.WAIT, "100"));
			this.script.add(new TestItem(TestItem.TYPE_REGISTER, TestItem.INTERRPT, "ON"));
		}
	}

	@Override
	public void onUpdatedByHardware(RegisterUpdates updates) {
		if (!this.redording) {
			return;
		}
		
		for (BitUpdates bit : updates.getBits()) {
			String val = "";
			if (bit.getVal().startsWith("data:application/octet-stream;base64,")) {
				val = convertBlockData(bit.getVal());
			}
			else {
				val = bit.getVal();
			}
			
			this.script.add(new TestItem(TestItem.TYPE_REGISTER, updates.getName() + "::" + bit.getName(), val));
		}		
	}

	private String convertBlockData(String val) {
		String str = val.replace("data:application/octet-stream;base64,", "");
		String filename = "blockdata-" + String.valueOf(Calendar.getInstance().getTime().getTime() + ".block");
		try {
			Files.write(Paths.get(TEST_FOLDER + filename), Base64.decode(str));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String ret = TestItem.FILE + filename;
		return ret;
	}

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

	private void addQueryTest(SvProperty prop) {
		TestItem test = new TestItem(TestItem.TYPE_PROPERTY_TEST, prop.getId() + "?", "", prop.getCurrentValue()); 
		this.script.add(test, this.currentRowSerial);
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
				doLoop();
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

	private void doLoop() {
		for (TestItem item : this.script.getScript()) {
			if (item.getType().equals(TestItem.TYPE_PROPERTY)) {
				requestChange(item);
			}
			else if (item.getType().equals(TestItem.TYPE_PROPERTY_TEST)) {
				SvProperty prop = this.testRecorderInterface.getProperty(item.getTarget().replace("?", ""));
				this.result.addResult(item.getSerial(), prop.getCurrentValue(), item.getExpected().equals(prop.getCurrentValue()));
			}
			else if (item.getType().equals(TestItem.TYPE_REGISTER)) {
				String tmp[] = item.getTarget().split("::");
				String regName = tmp[0];
				if (item.isFile()) {
					String filename = item.blockFilename();
					try {
						byte[] data = Files.readAllBytes(Paths.get(TEST_FOLDER + filename));
						long address = StaticInstances.getInstance().getBuilderModel().getRegisterProperty().getRegisterByName(regName).getDecAddress();
						updateBlockData(data, address);

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
					RegisterInfo regInfo = new RegisterInfo(regName, bitName, bitValue, StaticInstances.getInstance().getBuilderModel().getRegisterProperty());
					StaticInstances.getInstance().getSimulator().updateRegister(regInfo.getIntAddress(), regInfo.getDataSet(), regInfo.getMask());	
					
					updateRegister(regInfo);
				}
			}
			else if (item.getType().equals(TestItem.TYPE_REGISTER_TEST)) {
				String[] tmp = item.getTarget().split("::");
				String regName = tmp[0];
				String bitName = tmp[1].replace("?", "");
				int value = StaticInstances.getInstance().getBuilderModel().getRegisterMapModel().getValue(regName, bitName);
				this.result.addResult(item.getSerial(), String.valueOf(value), value == Integer.valueOf(item.getExpected()));
			}
			else if (item.getType().equals(TestItem.TYPE_CONTROL)) {
				if (item.getTarget().equals("WAIT")) {
					try {
						Thread.sleep(Integer.valueOf(item.getValue()));
					} catch (NumberFormatException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void updateRegister(RegisterInfo regInfo) {
		simulator.updateRegister(regInfo.getIntAddress(), regInfo.getDataSet(), regInfo.getMask());
	}

	private void triggerInterrupt() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				StaticInstances.getInstance().getSimulator().triggerInterrupt();
			}
		});
	}

	private void updateBlockData(byte[] data, long address) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				StaticInstances.getInstance().getSimulator().updateBlockData(address, data);
			}
		});
	}

	private void requestChange(TestItem item) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					testRecorderInterface.requestChange(item.getTarget(), item.getValue());
				} catch (RequestRejectedException e) {
					e.printStackTrace();
				}
			}
		});
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
		for (TestItem item : this.script.getScript()) {
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

	public void addCommand(String type, String target, String value, long serial) {
		this.script.add(new TestItem(type, target, value), serial);
	}
	public void addCommand(String type, String target, String value) {
		this.script.add(new TestItem(type, target, value), this.currentRowSerial);
	}
	
	public void moveUp(long serial) {
		this.script.moveUp(serial);
	}
	
	public void moveDown(long serial) {
		this.script.moveDown(serial);
	}

	public void addRegisterQuery(String regName, String bitName, int value) {
		TestItem test = new TestItem(TestItem.TYPE_REGISTER_TEST, regName + "::" + bitName + "?", "", String.valueOf(value));
		this.script.add(test, this.currentRowSerial);
	}

	public void save(String testName) {
		this.overwrite();
		if (!testName.toUpperCase().endsWith(".TEST")) {
			testName += ".test";
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Zip.unzip(testName, TEST_FOLDER);
		load();
	}

	public void selectRow(long serial) {
		this.currentRowSerial  = serial;
	}


}

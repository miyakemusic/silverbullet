package jp.silverbullet.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Calendar;
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
			this.script.add(new TestItem(TestItem.TYPE_CONTROL, TestItem.WAIT, "100"));
			this.script.add(new TestItem(TestItem.TYPE_REGISTER, TestItem.INTERRPT, "ON"));
		}
	}

	@Override
	public void onUpdatedByHardware(RegisterUpdates updates) {
		if (!this.redording) {
			return;
		}
		String val = "";
		for (BitUpdates bit : updates.getBits()) {
			if (bit.getVal().startsWith("data:application/octet-stream;base64,")) {
				val += convertBlockData(bit.getVal());
			}
			else {
				val += bit.getName() + "=" + bit.getVal();
			}
		}
		System.out.println(updates.getName() + " " + val);
		
		this.script.add(new TestItem(TestItem.TYPE_REGISTER, updates.getName(), val));
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
//		for (SvProperty prop : this.testRecorderInterface.getProperties()) {
//			addQueryTest(prop);
//		}
		overwrite();
		
		this.result = new TestResult(this.script);
	}

	private void addQueryTest(SvProperty prop) {
		TestItem test = new TestItem(TestItem.TYPE_PROPERTY_TEST, prop.getId() + "?", "", prop.getCurrentValue());
		this.script.add(test);
	}

	private void overwrite() {
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
		return null;
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
		this.script = this.loadScript(TEST_FOLDER + "test.json");
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
				String bitName = item.getValue().split("=")[0];
				if (item.isFile()) {
					String filename = item.blockFilename();
					try {
						byte[] data = Files.readAllBytes(Paths.get(TEST_FOLDER + filename));
						long address = StaticInstances.getInstance().getBuilderModel().getRegisterProperty().getRegisterByName(item.getTarget()).getDecAddress();
						updateBlockData(data, address);

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else if (item.isInterrupt()) {
					triggerInterrupt();
				}
				else {
					String bitValue = item.bitValue();
					RegisterInfo regInfo = new RegisterInfo(item.getTarget(), bitName, bitValue, StaticInstances.getInstance().getBuilderModel().getRegisterProperty());
					StaticInstances.getInstance().getSimulator().updateRegister(regInfo.getIntAddress(), regInfo.getDataSet(), regInfo.getMask());	
					
					updateRegister(regInfo);
				}
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

	public void updateValue(long serial, String value) {
		for (TestItem item : this.script.getScript()) {
			if (item.getSerial() == serial) {
				item.setValue(value);
				break;
			}
		}
		overwrite();
	}

	public void addPropertyTest(String id) {
		addQueryTest(this.testRecorderInterface.getProperty(id));
	}
}

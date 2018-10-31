package jp.silverbullet.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.sun.jersey.core.util.Base64;

import jp.silverbullet.SequencerListener;
import jp.silverbullet.dependency.RequestRejectedException;
import jp.silverbullet.register.BitUpdates;
import jp.silverbullet.register.RegisterMapListener;
import jp.silverbullet.register.RegisterUpdates;

public class TestRecorder implements SequencerListener, RegisterMapListener {

	private static final String TYPE_PROPERTY = "PROPERTY";
	private static final String TYPE_REGISTER = "REGISTER";
	
	private static final String TEST_FOLDER = "testdata/";
	
	private List<TestItem> items = new ArrayList<>();
	private boolean redording;
	private TestRecorderInterface testRecorderInterface;
	
	public TestRecorder(TestRecorderInterface testRecorderInterface) {
		this.testRecorderInterface = testRecorderInterface;
		if (!Files.exists(Paths.get(TEST_FOLDER))) {      
            try {
				Files.createDirectory(Paths.get(TEST_FOLDER));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public List<TestItem> getItems() {
		return items;
	}

	@Override
	public void onChangedBySystem(String id, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onChangedByUser(String id, String value) {
		if (this.redording) {
			this.items.add(new TestItem(TYPE_PROPERTY, id, value));
		}
	}

	@Override
	public void onUpdate(RegisterUpdates updates) {

	}

	@Override
	public void onInterrupt() {
		if (this.redording) {
			this.items.add(new TestItem(TYPE_REGISTER, "*INTERRPT*", "ON"));
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
				val += bit.getName() + "=" + bit.getVal() + ";";
			}
		}
		this.items.add(new TestItem(TYPE_REGISTER, updates.getName(), val));
	}

	private String convertBlockData(String val) {
		String str = val.replace("data:application/octet-stream;base64,", "");
		String filename = "blockdata-" + String.valueOf(Calendar.getInstance().getTime().getTime() + ".block");
		try {
			Files.write(Paths.get(TEST_FOLDER + filename), Base64.decode(str));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String ret = "file:" + filename;
		return ret;
	}

	public void startRecording() {
		this.items.clear();
		
		createSpanShot();
		this.redording = true;
	}

	private void createSpanShot() {
		this.testRecorderInterface.saveParameters(TEST_FOLDER + "snapshot.xml");
	}

	public void stopRecording() {
		this.redording = false;
		List<String> list = new ArrayList<>();
		for (TestItem item : this.items) {
			list.add(item.toString());
		}
		try {
			Files.write(Paths.get(TEST_FOLDER + "test.txt"), list, StandardOpenOption.CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void playBack() {
		try {
			List<String> lines = Files.readAllLines(Paths.get(TEST_FOLDER + "test.txt"));
			for (String line : lines) {
				String[] tmp = line.split(":");
				String type = tmp[0];
				String id = tmp[1].split("=")[0];
				String value = tmp[1].split("=")[1];
				
				if (type.equals("PROPERTY")) {
					try {
						this.testRecorderInterface.requestChange(id, value);
					} catch (RequestRejectedException e) {
						e.printStackTrace();
					}
				}
				else if (type.equals("REGISTER")) {
					this.testRecorderInterface.setRegisterValue(id, value);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

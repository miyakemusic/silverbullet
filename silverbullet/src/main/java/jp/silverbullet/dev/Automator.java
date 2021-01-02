package jp.silverbullet.dev;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.core.dependency2.Id;
import jp.silverbullet.core.sequncer.SequencerListener;

public class Automator {
	private List<String> devices = new ArrayList<>();
	private List<String> lines = new ArrayList<>();
	private Integer deviceNumber = 1;
	private Integer variableNumber = 1;
	private long currentTime = 0;
	private AutomatorInterface automaterInterface;
	private AutomatorStore store = new AutomatorStore();
	
	public Automator(AutomatorInterface automaterInterface) {
		this.automaterInterface = automaterInterface;
	}
	
	public SequencerListener createListener(String device) {
		devices.add(device);
		
		deviceNumber++;
		AutomatorListener automatorListener = new AutomatorListener() {
			@Override
			public void onChangeByUser(String device, String id, String value) {
				if (currentTime > 0) {
					long diff = System.currentTimeMillis() - currentTime; 
					lines.add("sb.sleep(" + diff + ");");
				}
				String line = "sb.write(" + device + ", '" + id + "=" + value + "');";
				lines.add(line);

				currentTime = System.currentTimeMillis();
			}

			@Override
			public void onChangeBySystem(String device, String id, String value) {
//				String line = "sb.waitEqual(" + device.toLowerCase() + ", '" + id + "','" + value + "');";
//				lines.add(line);
			}
		};
		
		MySequencerListener listener = new MySequencerListener(device, automatorListener);
		return listener;
	}
	
	interface AutomatorListener {
		void onChangeByUser(String device, String id, String value);

		void onChangeBySystem(String device, String id, String value);
	}
	
	class MySequencerListener implements SequencerListener {

		private String device;
		private AutomatorListener automatorListener;

		public MySequencerListener(String device, AutomatorListener automatorListener) {
			this.device = device;
			this.automatorListener = automatorListener;
		}

		@Override
		public void onChangedBySystem(Id id, String value) {
			automatorListener.onChangeBySystem(device, id.getId(), value);
		}

		@Override
		public void onChangedByUser(Id id, String value) {
			automatorListener.onChangeByUser(device, id.getId(), value);
		}
		
	}

	public List<String> getLines() {
		return this.lines;
	}

	public enum Action {
		WAITFOR,
		JUDGE
	}
	public void addAction(String device, String id, String value, String action2) {
		Action action = Action.valueOf(action2);

		
		if (action.equals(Action.JUDGE)) {
			String line = "var v" + variableNumber + " = sb.read(" + device + ", '" + id + "');";
			lines.add(line);
			line = "if (v" + variableNumber + " == '" + value + "') {";
			lines.add(line);
			lines.add("  /// put code here");
			line = "}";
			lines.add(line);
			
			variableNumber++;
		}
		else if (action.equals(Action.WAITFOR)) {
			String line = "sb.waitEqual(" + device + ",'" + id + "','" + value + "');";
			lines.add(line);
		}
	}

	public void clear() {
		this.lines.clear();
		for (String device : devices) {
			//lines.add(0, "var " + device + " = '192.168.0." + deviceNumber.toString() + ":8080';");
			lines.add(0, "var " + device + " = '" + device + "';");
		}
	}

	public void playback(String script) {
		new Thread() {
			@Override
			public void run() {
				startScriptManager(script);
			}
		}.start();		
	}

	private Object sync = new Object();
	private String messageReply;
	
	protected void startScriptManager(String script) {
		new ScriptManager() {
			@Override
			public void write(String addr, String command) {
				//automaterInterface.write(tmp[1], tmp[3], tmp[4]);
				System.out.println(command);
				String[] tmp = command.split("=");
				automaterInterface.write(addr, tmp[0], tmp[1]);
			}

			@Override
			public String read(String addr, String query) {
				return automaterInterface.read(addr, query);
			}

			@Override
			public String waitEqual(String addr, String id, String value) {
				for (int i = 0; i < 100; i++) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {

					}
					String ret = read(addr, id);
					if (ret.equals(value)) {
						break;
					}
				}
				return "";
			}

			@Override
			public void debug(String arg) {
				automaterInterface.debug(arg);
			}

			@Override
			public String message(String addr, String message, String controls) {
				automaterInterface.message(addr, message, controls);
				synchronized (sync) {
					try {
						sync.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				return messageReply;
			}
			
		}.start(Arrays.asList(script.split("\n")));
	}

	public void register(String name, String script) {
		store.put(name, script);
	}

	public List<String> scriptList() {
		return store.nameList();
	}

	public void removeDevice(String device) {
		this.devices.remove(device);
	}

	public void onReplyMessage(String messageId, String reply) {
		this.messageReply = reply;
		synchronized(sync) {
			sync.notify();
		}
	}
	
	public void save(String filename) {
		try {
			new ObjectMapper().writeValue(new File(filename), this.store);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void load(String filename) {
		try {
			this.store = new ObjectMapper().readValue(new File(filename), AutomatorStore.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadScript(String name) {
		this.lines = new ArrayList<String>(Arrays.asList(this.store.getData().get(name).split("\n")));
	}
}

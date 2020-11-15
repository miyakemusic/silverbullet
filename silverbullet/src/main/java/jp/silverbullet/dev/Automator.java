package jp.silverbullet.dev;

import java.util.ArrayList;
import java.util.List;
import jp.silverbullet.core.dependency2.Id;
import jp.silverbullet.core.sequncer.SequencerListener;

public class Automator {

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
		lines.add(0, "var " + device + " = '192.168.0." + deviceNumber.toString() + ":8080';");
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
		this.lines.clear();;
	}

	public void playback(String script) {
		for (String line: script.split("\n")) {
			if (line.startsWith("sb.write")) {
				String[] tmp = line.split("[(',)=]+");
				this.automaterInterface.write(tmp[1], tmp[3], tmp[4]);
				//automaterInterface.write();
			}
			else if (line.startsWith("sb.sleep")){
				String[] tmp = line.split("[()]+");
				try {
					Thread.sleep(Integer.valueOf(tmp[1]));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void register(String name, String script) {
		store.put(name, script);
	}

	public List<String> scriptList() {
		return store.nameList();
	}
}

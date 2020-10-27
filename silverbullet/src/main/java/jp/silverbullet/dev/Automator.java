package jp.silverbullet.dev;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;

import jp.silverbullet.core.sequncer.SequencerListener;
import jp.silverbullet.web.ValueSetResult;

public class Automator {

	private List<String> lines = new ArrayList<>();
//	private Set<String> devices = new HashSet<>();
	private Integer deviceNumber = 1;
	private Integer variableNumber = 1;
	
	public Automator() {

	}
	
	public SequencerListener createListener(String device) {
		lines.add(0, "var " + device.toLowerCase() + " = '192.168.0." + deviceNumber.toString() + ":8080';");
		deviceNumber++;
		AutomatorListener automatorListener = new AutomatorListener() {
			@Override
			public void onChangeByUser(String device, String id, String value) {
				String line = "sb.write(" + device.toLowerCase() + ", '" + id + "=" + value + "');";
				lines.add(line);
			}

			@Override
			public void onChangeBySystem(String device, String id, String value) {
				String line = "sb.waitEqual(" + device.toLowerCase() + ", '" + id + "','" + value + "');";
				lines.add(line);
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
		public void onChangedBySystem(String id, String value) {
			automatorListener.onChangeBySystem(device, id, value);
		}

		@Override
		public void onChangedByUser(String id, String value) {
			automatorListener.onChangeByUser(device, id, value);
		}
		
	}

	public List<String> getLines() {
		return this.lines;
	}

	public void addEval(String device, String id, String value) {
		String line = "var v" + variableNumber + " = sb.read(" + device.toLowerCase() + ", '" + id + "');";
		lines.add(line);
		
		line = "if (v" + variableNumber + " == '" + value + "') {";
		lines.add(line);
		lines.add("  /// put code here");
		line = "}";
		lines.add(line);
		
		variableNumber++;
	}

	public void clear() {
		this.lines.clear();;
	}
}

package jp.silverbullet.web.ui;

import java.util.ArrayList;
import java.util.List;
import jp.silverbullet.web.KeyValue;

public class UiLayoutListenerImpl implements UiLayoutListener {
	private List<KeyValue> log = new ArrayList<>();
	
	@Override
	public void onLayoutChange(String div, String currentFilename) {
		log.add(new KeyValue(div, currentFilename));
	}

	public List<KeyValue> getLog() {
		return log;
	}

	public KeyValue getLastLog() {
		return log.get(log.size()-1);
	}

	public void clearLog() {
		this.log.clear();
	}

}

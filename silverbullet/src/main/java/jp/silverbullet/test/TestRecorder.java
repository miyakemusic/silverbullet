package jp.silverbullet.test;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import jp.silverbullet.SequencerListener;
import jp.silverbullet.dependency.ChangedItemValue;
import jp.silverbullet.dependency.DependencyListener;
import jp.silverbullet.register.BitUpdates;
import jp.silverbullet.register.RegisterMapListener;
import jp.silverbullet.register.RegisterUpdates;

public class TestRecorder implements SequencerListener, RegisterMapListener {

	private static final String TYPE_PROPERTY = "PROPERTY";
	private static final String TYPE_REGISTER = "REGISTER";
	
	private List<TestItem> items = new ArrayList<>();
	
	public List<TestItem> getItems() {
		return items;
	}

	@Override
	public void onChangedBySystem(String id, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onChangedByUser(String id, String value) {
		this.items.add(new TestItem(TYPE_PROPERTY, id, value));
	}

	@Override
	public void onUpdate(RegisterUpdates updates) {

	}

	@Override
	public void onInterrupt() {
		this.items.add(new TestItem(TYPE_REGISTER, "*INTERRPT*", ""));
	}

	@Override
	public void onUpdatedByHardware(RegisterUpdates updates) {
		String val = "";
		for (BitUpdates bit : updates.getBits()) {
			val += bit.getName() + "=" + bit.getVal() + ";";
		}
		this.items.add(new TestItem(TYPE_REGISTER, updates.getName(), val));
	}

}

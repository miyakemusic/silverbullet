package jp.silverbullet.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.silverbullet.SequencerListener;
import jp.silverbullet.dependency.ChangedItemValue;
import jp.silverbullet.dependency.DependencyListener;

public class TestRecorder implements SequencerListener {

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
		this.items.add(new TestItem(id, value));
	}

}

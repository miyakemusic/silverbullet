package jp.silverbullet.dev.test;

import java.util.ArrayList;
import java.util.List;

public class TestScript {
	private String scriptName;
	private List<TestItem> script = new ArrayList<>();
	public String getScriptName() {
		return scriptName;
	}
	public List<TestItem> getScripts() {
		return script;
	}
	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}
	public void setScript(List<TestItem> script) {
		this.script = script;
	}
	public void add(TestItem testItem) {
		this.script.add(testItem);
		testItem.setSerial(testItem.hashCode());
	}
	public void clear() {
		this.script.clear();
	}
	
	private TestItem getTestItem(long serial) {
		for (TestItem testItem : this.script) {
			if (testItem.getSerial() == serial) {
				return testItem;
			}
		}
		return null;
	}
	
	private int getIndex(long serial) {
		if (serial < 0) {
			return this.script.size() - 1;
		}
		return this.script.indexOf(getTestItem(serial));
	}
	
	public void remove(long serial) {
		this.script.remove(getTestItem(serial));
	}
	public void add(TestItem testItem, long serial) {
		int index = getIndex(serial);
		if ((this.script.size()) > index) {
			index++;
		}
		testItem.setSerial(testItem.hashCode());
		this.script.add(index, testItem);
	}
	
	abstract class Mover {
		public Mover(long serial) {
			TestItem item = getTestItem(serial);
			int index = getIndex(serial);
			index = calcIndex(index);
			script.remove(item);
			script.add(index, item);		
		}

		abstract protected int calcIndex(int index);
	}
	public void moveUp(long serial) {
		new Mover(serial) {
			@Override
			protected int calcIndex(int index) {
				if (index >= 1) {
					index--;
				}
				return index;
			}
		};
	}
	
	public void moveDown(long serial) {
		new Mover(serial) {
			@Override
			protected int calcIndex(int index) {
				if (index < (script.size()-1)) {
					index++;
				}
				return index;
			}
		};
	}
	public void changeId(String prevId, String newId) {
		for (TestItem testItem : this.script) {
			testItem.changeId(prevId, newId);
		}
	}
	
}

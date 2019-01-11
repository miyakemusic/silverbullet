package jp.silverbullet.test;

import java.util.ArrayList;
import java.util.List;

public class TestScriptPresentation {
	private List<PresentationItem> items = new ArrayList<>();
	
	public TestScriptPresentation(TestResult result) {
		int num = 1;
		for (TestItem testItem : result.getScript().getScripts()) {
			TestResultItem resultItem = result.getResult().get(testItem.getSerial());
			
			String result2 = "";
			String passFail = "";
			String time  = "";
			if (resultItem != null) {
				result2  = resultItem.getResult();
				passFail = resultItem.getPassFail().toString();
				time = resultItem.getElapsed();
			}
			items.add(new PresentationItem(testItem.getSerial(), num++, testItem.getType(), testItem.getTarget(), testItem.getValue(), testItem.getExpected(), result2, 
					passFail, time));
		}
	}

	public List<PresentationItem> getItems() {
		return items;
	}

	public void setItems(List<PresentationItem> items) {
		this.items = items;
	}

}

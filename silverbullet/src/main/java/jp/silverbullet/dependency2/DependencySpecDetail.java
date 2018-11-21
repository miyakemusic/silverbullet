package jp.silverbullet.dependency2;

import java.util.ArrayList;
import java.util.List;

public class DependencySpecDetail {

	private List<TriggerCondition> triggerConditions = new ArrayList<>();
	
	public void addTriggerCondition(String condition) {
		this.triggerConditions.add(new TriggerCondition(condition));
	}
}

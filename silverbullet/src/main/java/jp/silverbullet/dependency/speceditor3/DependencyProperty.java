package jp.silverbullet.dependency.speceditor3;

import java.util.ArrayList;
import java.util.List;

import jp.silverbullet.dependency.speceditor3.DependencyExpressionHolder.SettingDisabledBehavior;

public class DependencyProperty {
	private String id;
	private String selectionId;
	private DependencyTargetElement element;
	private String condition;
	private String value;
	private List<String> triggerIds = new ArrayList<>();
	private IdCollector idCollector = new IdCollector();
	private List<DependencyProperty> otherSources = new ArrayList<>();
	private DependencyProperty other;
	private boolean otherSourceConsumed = false;
	private SettingDisabledBehavior settingDisabledBehavior;
	
	public DependencyProperty(String id, String selectionId, DependencyTargetElement element, String expression, String value) {
		super();
		this.id = id;
		this.selectionId = selectionId;
		this.element = element;
		this.condition = expression;
		this.value = value;
	
		triggerIds.addAll(idCollector.collectIds(value));
		triggerIds.addAll(idCollector.collectIds(expression));
	}
	
	public DependencyProperty(String id, DependencyTargetElement element, String expression, String value) {
		super();
		this.id = id;
		this.element = element;
		this.condition = expression;
		this.value = value;
		
		triggerIds.addAll(idCollector.collectIds(value));
		triggerIds.addAll(idCollector.collectIds(expression));
	}
	public String getId() {
		return id;
	}
	public String getSelectionId() {
		return selectionId;
	}
	public DependencyTargetElement getElement() {
		return element;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setSelectionId(String selectionId) {
		this.selectionId = selectionId;
	}
	public void setElement(DependencyTargetElement element) {
		this.element = element;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String expression) {
		this.condition = expression;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<String> getTriggerIds() {
		return triggerIds;
	}

	public void addOtherSource(DependencyProperty dp) {
		this.otherSources .add(dp);
	}

	public void setOther(DependencyProperty other) {
		this.other = other;
	}

	public void cosumed() {
		if (other != null) {
			this.other.consumed(this);
		}
	}

	private void consumed(DependencyProperty consumed) {
		this.otherSourceConsumed  = true;
	}

	public boolean isOtherSatisfied() {
		return isElseCondition() && !this.otherSourceConsumed;
	}

	public boolean isElseCondition() {
		return this.getCondition().equals(DependencyExpression.ELSE);
	}

	public void setSettingDisabledBehavior(SettingDisabledBehavior settingDisabledBehavior) {
		this.settingDisabledBehavior = settingDisabledBehavior;
	}

	public SettingDisabledBehavior getSettingDisabledBehavior() {
		return settingDisabledBehavior;
	}
	
}

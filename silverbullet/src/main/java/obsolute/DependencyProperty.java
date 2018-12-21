package obsolute;

import java.util.ArrayList;
import java.util.List;

import obsolute.DependencyExpressionHolder.SettingDisabledBehavior;

public class DependencyProperty {
	private String id;
	private String selectionId;
	private DependencyTargetElement element;
	private String condition;
	private String value;
	private List<String> triggerIds = new ArrayList<>();
	private IdCollector idCollector = new IdCollector();
	private DependencyProperty elseProperty;
	private boolean otherSourceConsumed = false;
	private SettingDisabledBehavior settingDisabledBehavior;
	private DependencyExpression pointer;
	private boolean consumed = false;
	private DependencyProperty parent;
	
	public DependencyProperty(String id, String selectionId, DependencyTargetElement element, 
			String condition2, String value, DependencyExpression pointer) {
		super();
		this.id = id;
		this.selectionId = selectionId;
		this.element = element;
		this.condition = condition2;
		this.value = value;
		this.pointer = pointer;
		
		triggerIds.addAll(idCollector.collectIds(value));
		triggerIds.addAll(idCollector.collectIds(condition2));
	}
	
	public DependencyProperty(String id, DependencyTargetElement element, 
			String expression, String value, DependencyExpression pointer) {
		super();
		this.id = id;
		this.element = element;
		this.condition = expression;
		this.value = value;
		this.pointer = pointer;
		
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

//	public void addOtherSource(DependencyProperty dp) {
//		this.otherSources .add(dp);
//	}

	public void setElse(DependencyProperty other) {
		this.elseProperty = other;
	}

	
	public DependencyProperty getElseProperty() {
		return elseProperty;
	}

	public void cosumed() {
		consumed  = true;
		if (elseProperty != null) {
			this.elseProperty.consumed(this);
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

	public DependencyExpression getPointer() {
		return pointer;
	}

	public boolean isConfirmationRequired() {
		return this.pointer.isConfirmationRequired();
	}

	public boolean isConsumed() {
		return consumed;
	}

	public void setParent(DependencyProperty parent) {
		this.parent = parent;
	}

	public DependencyProperty getParent() {
		return parent;
	}
	
}

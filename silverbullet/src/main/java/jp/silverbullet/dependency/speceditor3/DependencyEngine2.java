package jp.silverbullet.dependency.speceditor3;

import java.util.ArrayList;
import java.util.List;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.engine.RequestRejectedException;
import jp.silverbullet.dependency.speceditor2.DependencySpecHolder;

public abstract class DependencyEngine2 {
	protected abstract DepProperyStore getPropertiesStore();
	protected abstract DependencySpecHolder2 getDependencyHolder();
	 
	private ExpressionCalculator calculator = new ExpressionCalculator() {
		@Override
		protected SvProperty getProperty(String id) {
			return DependencyEngine2.this.getProperty(id);
		}
	};
	
	public void requestChange(String id, String value) throws RequestRejectedException {
		DependencyBuilder3 builder = new DependencyBuilder3(id, getDependencyHolder());

		List<ChangedProperty> ret = null;
		for (int layer = 0; layer < builder.getLayerCount(); layer++) {
			List<DependencyProperty> specs = builder.getSpecs(layer);
			
			// removes unnecessary specs
			if (ret != null) {
				List<DependencyProperty> removed = new ArrayList<>();
				for (DependencyProperty spec : specs) {
					for (ChangedProperty changed : ret) {
						if (spec.getTriggerIds().contains(changed.getId() + "." + changed.getElement())) {
							removed.add(spec);
						}
					}
				}
				ret.clear();
				specs = removed;
			}
			ret = doDependency(id, value, builder, specs);
		}
	}
	
	private List<ChangedProperty> doDependency(String id, String value, DependencyBuilder3 builder, List<DependencyProperty> specs) throws RequestRejectedException {
		SvProperty targetProperty = getPropertiesStore().getProperty(id);
		targetProperty.setCurrentValue(value);
		
		List<ChangedProperty> changed = new ArrayList<>();
		
		for (DependencyProperty spec : specs) {
			if (satisfies(spec)) {
				SvProperty prop = getProperty(spec.getId());
				if (!spec.getSelectionId().equals(DependencySpec2.DefaultItem)){
					Boolean v = !Boolean.valueOf(calcResult(spec));
					prop.addListMask(spec.getSelectionId(), v);
					changed.add(new ChangedProperty(prop.getId(), spec.getSelectionId(), spec.getElement()));
					
					// if masked item was current, it should be change
					if (v && prop.getCurrentValue().equals(spec.getSelectionId())) {
						prop.setCurrentValue(prop.getAvailableListDetail().get(0).getId());
						changed.add(new ChangedProperty(prop.getId(), spec.getElement()));
					}
				}
				else {
					String v = calcResult(spec);
					if (prop.isListProperty()) {
						v = v.replace("%", "");
					}
					else if (prop.isNumericProperty()) {
						RangeChecker rangeChecker = new RangeChecker(prop.getMin(), prop.getMax(), v);
						if (!rangeChecker.isSatisfied()) {
							if (spec.getSettingDisabledBehavior().equals(DependencyExpressionHolder.SettingDisabledBehavior.Reject)) {
								throw new RequestRejectedException(RequestRejectedException.OUT_OF_RANGE);
							}
							else if (spec.getSettingDisabledBehavior().equals(DependencyExpressionHolder.SettingDisabledBehavior.Adjust)) {
								if (rangeChecker.isUnderRange()) {
									v = prop.getMin();
								}
								else if (rangeChecker.isOverRange()) {
									v = prop.getMax();
								}
							}
							else if (spec.getSettingDisabledBehavior().equals(DependencyExpressionHolder.SettingDisabledBehavior.DependsOnStrength)) {
								
							}
						}		
					}
		
					prop.setCurrentValue(v);
					changed.add(new ChangedProperty(prop.getId(), spec.getElement()));
				}
				
				spec.cosumed();
			}
		}
		return changed;
	}
	
	private String calcResult(DependencyProperty spec) {
		if (spec.getValue().startsWith(ExpressionBuilder.SCRIPT)) {
			return calculator.calculate(strip(spec));
		}
		else {
			String ret = calculator.calculate("ret=" + spec.getValue()/*strip(spec)*/);
			return ret;
		}

//		else {
//			return spec.getValue();
//		}
	}
	private String strip(DependencyProperty spec) {
		try {
		return spec.getValue().split("[\\[\\]]+")[1];
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	private SvProperty getProperty(String id) {
		return getPropertiesStore().getProperty(id);
	}
	private boolean satisfies(DependencyProperty spec) {
		
		String condition = spec.getCondition();
		if (spec.isElseCondition()) {
			return spec.isOtherSatisfied();
		}
		
		if (condition.isEmpty()) {
			return true;
		}
		else if (condition.contains(DependencyExpression.AnyValue)) {
			return true;
		}
		else /*if (condition.startsWith(ExpressionBuilder.EXPRESSION))*/ {
			//String exp = condition.replace(ExpressionBuilder.EXPRESSION + "[", "").replaceAll("]", "");
			return calculator.isSatisfied("ret=" + condition);
		}
//		else {
//			String comparator = "";
//			String[] comparators = {DependencyExpression.Equals, DependencyExpression.NotEquals, DependencyExpression.LargerThan, DependencyExpression.SmallerThan};
//			for (String c : comparators) {
//				if (condition.contains(c)) {
//					comparator = c;
//					break;
//				}
//			}
//			String left = condition.split(comparator)[0];
//			String right = condition.split(comparator)[1];
//			
//			if (right.equals(DependencyExpression.AnyValue)) {
//				return true;
//			}
//			else {
//				condition = calculator.replaceWithRealValue(condition).replaceAll("[\\$%]+", "");
//				left = condition.split(comparator)[0];
//				right = condition.split(comparator)[1];
//				if (comparator.equals(DependencyExpression.Equals)) {
//					return left.equals(right);
//				}
//				else if (comparator.equalsIgnoreCase(DependencyExpression.NotEquals)) {
//					return !left.equals(right);
//				}
//				else {
//					
//				}
//			}
//		}
//		return true;
	}
}
class ChangedProperty {
	private String id;
	private String selectionId;
	private DependencyTargetElement element;
	
	public ChangedProperty(String id, DependencyTargetElement element) {
		this.id = id;
		this.element = element;
	}
	
	public ChangedProperty(String id, String selectionId, DependencyTargetElement element) {
		this.id = id;
		this.selectionId = selectionId;
		this.element = element;
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
	
}

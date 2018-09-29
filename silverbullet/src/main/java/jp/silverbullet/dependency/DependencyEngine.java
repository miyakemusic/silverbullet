package jp.silverbullet.dependency;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.silverbullet.SvProperty;


public abstract class DependencyEngine {
	protected abstract DepPropertyStore getPropertiesStore();
	protected abstract DependencySpecHolder getDependencyHolder();
	private CachedPropertyStore cachedPropertyStore;
	
	private ExpressionCalculator calculator = new ExpressionCalculator() {
		@Override
		protected SvProperty getProperty(String id) {
			return DependencyEngine.this.getProperty(id);
		}
	};

	private List<DependencyListener> listeners = new ArrayList<>();
	
	public void requestChange(String id, String value) throws RequestRejectedException {
		this.cachedPropertyStore = new CachedPropertyStore(getPropertiesStore());
		
		this.cachedPropertyStore.getProperty(id).setCurrentValue(value);
		
		DependencyBuilder builder = new DependencyBuilder(id, getDependencyHolder());

		List<DependencyProperty> confirmations = new ArrayList<>();
		
		for (int layer = 0; layer < builder.getLayerCount(); layer++) {
			List<DependencyProperty> specs = builder.getSpecs(layer);
			doDependency(builder, specs);
			
			for (DependencyProperty spec : specs) {
				if (spec.isConsumed() && spec.isConfirmationRequired()) {
					confirmations.add(spec);
				}
			}
		}
		
		if (!confirmations.isEmpty()) {
			for (DependencyListener listener : this.listeners) {
				String message = "";
				for (DependencyProperty spec : confirmations ) {
					List<ChangedItemValue> items = this.getChagedItems().get(spec.getId());
					if (items == null) {
						continue;
					}
					for (ChangedItemValue v : items) {
						if (v.getElement().equals(spec.getElement())) {
							message += spec.getId() + "." + v.getElement().name() + "=" + v.getValue() +"\n";
						}
					}
				}
				if (listener.confirm(message)) {
					this.cachedPropertyStore.commit();
				}
				else {
					
				}
			}
		}
		else {
			this.cachedPropertyStore.commit();
		}
		
		for (DependencyListener listener : this.listeners) {
			listener.onCompleted(this.getChangedIds().toString().replace("[", "").replace("]", "").replaceAll(" ", ""));
		}
	}
	
	private List<ChangedProperty> doDependency(DependencyBuilder builder, List<DependencyProperty> specs) throws RequestRejectedException {

		for (DependencyProperty spec : specs) {
			if (satisfies(spec)) {
				SvProperty prop = getProperty(spec.getId());
				if (!spec.getSelectionId().equals(DependencySpec.DefaultItem)){
					Boolean v = !Boolean.valueOf(calcResult(spec));
					prop.addListMask(spec.getSelectionId(), v);
//					changed.add(new ChangedProperty(prop.getId(), spec.getSelectionId(), spec.getElement(), spec.isConfirmationRequired()));
					
					// if masked item was current, it should be change
					if (v && prop.getCurrentValue().equals(spec.getSelectionId())) {
						prop.setCurrentValue(prop.getAvailableListDetail().get(0).getId());
//						changed.add(new ChangedProperty(prop.getId(), spec.getElement(), spec.isConfirmationRequired()));
					}
				}
				else {
					String v = calcResult(spec);
					
					if (spec.getElement().equals(DependencyTargetElement.Enabled)) {
						prop.setEnabled(Boolean.valueOf(v));
					}
					else if(spec.getElement().equals(DependencyTargetElement.Visible)) {
						prop.setVisible(Boolean.valueOf(v));
					}
					else if (spec.getElement().equals(DependencyTargetElement.Value)) {
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
					}
//					changed.add(new ChangedProperty(prop.getId(), spec.getElement(), spec.isConfirmationRequired()));
				}
				
				spec.cosumed();
			}
		}
//		return changed;
		return null;
	}
	
	private String calcResult(DependencyProperty spec) {
		if (spec.getValue().startsWith(ExpressionBuilder.SCRIPT)) {
			return calculator.calculate(strip(spec));
		}
		else {
			String ret = calculator.calculate("ret=" + spec.getValue()/*strip(spec)*/);
			return ret;
		}
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
		return cachedPropertyStore.getProperty(id);
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
		else  {
			return calculator.isSatisfied("ret=" + condition);
		}

	}
	public void addDependencyListener(DependencyListener dependencyListener) {
		listeners .add(dependencyListener);
	}
	public List<String> getChangedIds() {
		return this.cachedPropertyStore.getChangedIds();
	}
	
	public Map<String, List<ChangedItemValue>> getChagedItems() {
		return this.cachedPropertyStore.getChangedHistory();
	}
	
	public CachedPropertyStore getCachedPropertyStore() {
		return this.cachedPropertyStore;
	}
	
	public List<String> getDebugLog() {
		return this.cachedPropertyStore.getDebugLog();
	}
}
class ChangedProperty {
	private String id;
	private String selectionId;
	private DependencyTargetElement element;
	private boolean confirmation;
	

	public ChangedProperty(String id, String selectionId, DependencyTargetElement element, boolean confirmation) {
		this.id = id;
		this.selectionId = selectionId;
		this.element = element;
		this.confirmation = confirmation;
	}

	public ChangedProperty(String id, DependencyTargetElement element, boolean confirmationRequired) {
		this.id = id;
		this.element = element;
		this.confirmation = confirmationRequired;
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

	public boolean isConfirmation() {
		return confirmation;
	}
	
}

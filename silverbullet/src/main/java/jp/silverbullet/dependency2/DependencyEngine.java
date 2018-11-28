package jp.silverbullet.dependency2;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.CachedPropertyStore;
import jp.silverbullet.dependency.DepPropertyStore;
import jp.silverbullet.dependency.ExpressionCalculator;
import jp.silverbullet.dependency.RequestRejectedException;
import jp.silverbullet.property.ListDetailElement;

public class DependencyEngine {

	private DepPropertyStore store;
	private DependencySpecHolder specHolder;
	private CachedPropertyStore cachedPropertyStore;
	private ExpressionCalculator calculator;
	private String userChangedId;
	
	public DependencyEngine(DependencySpecHolder specHolder, DepPropertyStore store) {
		this.store = store;
		this.specHolder = specHolder;
		calculator = new ExpressionCalculator() {
			@Override
			protected SvProperty getProperty(String id) {
				return cachedPropertyStore.getProperty(id);
			}
			
		};
	}

	public void requestChanges(List<IdValue> idValues) throws RequestRejectedException {
		this.cachedPropertyStore = new CachedPropertyStore(store);
		for (IdValue idValue : idValues) {
			changeValue(idValue.getId(), idValue.getValue());
		}
	}
	
	public void requestChange(String id, String value) throws RequestRejectedException {
		this.cachedPropertyStore = new CachedPropertyStore(store);
		changeValue(id, value);
	}

	private void changeValue(String id, String value) throws RequestRejectedException {
		this.userChangedId = id;
		ChangedProperties prevChangedProperties = new ChangedProperties(Arrays.asList(id));
		this.cachedPropertyStore.addCachedPropertyStoreListener(prevChangedProperties);
		setCurrentValue(cachedPropertyStore.getProperty(id), value);
		handle(id, value);
		this.cachedPropertyStore.removeCachedPropertyStoreListener(prevChangedProperties);
		
		while(prevChangedProperties.getIds().size() > 0) {
			prevChangedProperties = handleNext(prevChangedProperties);
		}
	}

	private ChangedProperties handleNext(ChangedProperties prevChangedProperties) throws RequestRejectedException {
		ChangedProperties changedProperties2 = new ChangedProperties(prevChangedProperties.getIds());
		this.cachedPropertyStore.addCachedPropertyStoreListener(changedProperties2);
		for (String nextId :  prevChangedProperties.getIds()) {
			handle(nextId, this.cachedPropertyStore.getProperty(nextId).getCurrentValue());
		}
		this.cachedPropertyStore.removeCachedPropertyStoreListener(changedProperties2);
		return changedProperties2;
	}
	
	private void handle(String id, String value) throws RequestRejectedException {	
		List<RuntimeDependencySpec> specs = this.specHolder.getRuntimeSpecs(id);
		for (RuntimeDependencySpec spec : specs) {	
			if (spec.getId().equals(this.userChangedId)) {
				continue;
			}
			if (spec.isElse()) {
				if (!spec.otherConsumed()) {
					spec.setExecutionConditionSatistied(true);
				}
				else {
					spec.setExecutionConditionSatistied(false);
				}
			}
			else {
				spec.setExecutionConditionSatistied(Boolean.valueOf(calculator.calculate("ret=" + spec.getExpression().getTrigger())));
				if (spec.getExpression().isValueCalculationEnabled()) {
					spec.getExpression().setValue(calculator.calculate("ret=" + spec.getExpression().getValue()));
				}
			}
			
			if (!spec.getExpression().getCondition().isEmpty()) {
				spec.setExecutionConditionSatistied(Boolean.valueOf(calculator.calculate("ret=" + spec.getExpression().getCondition())));
			}
			if (!spec.isExecutionConditionSatistied()) {
				continue;
			}
			SvProperty property = this.cachedPropertyStore.getProperty(spec.getId());
			
			if (spec.isOptionEnabled()) {
				for (ListDetailElement e: property.getListDetail()) {
					if (e.getId().equals(spec.getTargetOption())) {
						property.addListMask(e.getId(), !spec.getExpression().getValue().equals(DependencySpec.True));
						break;
					}
				}
				
				// selects other one if current is masked
				if (property.isListElementMasked(property.getCurrentValue())) {
					if (spec.isReject()) {
						throw new RequestRejectedException(property.getId());
					}
					else {
						reselectNearestValue(property);
					}
				}
			}
			else if (spec.isEnable()) {
				property.setEnabled(spec.getExpression().getValue().equals(DependencySpec.True));
			}
			else if (spec.isValue()) {
				String val = spec.getExpression().getValue();
				setCurrentValue(property, val);
			}
			else if (spec.isMin()) {
				String min = spec.getExpression().getValue();
				if (this.isLeftLarger(min, property.getCurrentValue())) {
					if (spec.isReject()) {
						throw new RequestRejectedException(property.getId());
					}
					else {
						property.setMin(min);
						property.setCurrentValue(min);
					}
					
				}
				else {
					property.setMin(min);
				}
			}
			else if (spec.isMax()) {
				String max = spec.getExpression().getValue();
				if (this.isLeftLarger(property.getCurrentValue(), max)) {
					if (spec.isReject()) {
						throw new RequestRejectedException(property.getId());
					}
					else {
						property.setMax(max);
						property.setCurrentValue(max);
					}
					
				}
				else {
					property.setMax(max);
				}
			}
			spec.consumed();
		}
	}

	private void setCurrentValue(SvProperty property, String val) throws RequestRejectedException {
		if (property.isNumericProperty()) {
			if (isLeftLarger(property.getMin(), val)) {
				throw new RequestRejectedException(property.getId() + " Under Min.");
			}
			else if (isLeftLarger(val, property.getMax())) {
				throw new RequestRejectedException(property.getId() + " Over Max.");
			}
			else {
				property.setCurrentValue(val);
			}
		}
		else {
			property.setCurrentValue(val);
		}
	}

	private boolean isLeftLarger(String val1, String val2) {
		return Double.valueOf(val1) > Double.valueOf(val2);
	}

	private void reselectNearestValue(SvProperty property) {
		List<String> listIds = property.getListIds();
		int currentIndex = listIds.indexOf(property.getCurrentValue());
		int limit = Math.max(listIds.size() - currentIndex, currentIndex);
		for (int width = 1; width < limit; width++) {
			int index = currentIndex + width;
			if (canSelect(index, listIds, property.getListMask())) {
				property.setCurrentValue(listIds.get(index));
				return;
			}
			index = currentIndex - width;
			if (canSelect(index, listIds, property.getListMask())) {
				property.setCurrentValue(listIds.get(index));
				return;
			}
		}
	}


	private boolean canSelect(int index, List<String> listIds, Map<String, Boolean> listMask) {
		if (index < 0 || index >= listIds.size()) {
			return false;
		}
		Boolean mask = listMask.get(listIds.get(index));
		
		return (mask == null) || !mask;
	}

	public CachedPropertyStore getCachedPropertyStore() {
		return this.cachedPropertyStore;
	}

}

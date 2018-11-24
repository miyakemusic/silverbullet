package jp.silverbullet.dependency2;

import java.util.Arrays;
import java.util.List;
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

	public void requestChange(String id, String value) throws RequestRejectedException {
		this.userChangedId = id;
		this.cachedPropertyStore = new CachedPropertyStore(store);
		
		ChangedProperties prevChangedProperties = new ChangedProperties(Arrays.asList(id));
		this.cachedPropertyStore.addCachedPropertyStoreListener(prevChangedProperties);
		this.cachedPropertyStore.getProperty(id).setCurrentValue(value);
		handle(id, value);
		this.cachedPropertyStore.removeCachedPropertyStoreListener(prevChangedProperties);
		
		while(prevChangedProperties.getIds().size() > 0) {
			prevChangedProperties = handleNext(prevChangedProperties);
		}
	}

	private ChangedProperties handleNext(ChangedProperties prevChangedProperties) {
		ChangedProperties changedProperties2 = new ChangedProperties(prevChangedProperties.getIds());
		this.cachedPropertyStore.addCachedPropertyStoreListener(changedProperties2);
		for (String nextId :  prevChangedProperties.getIds()) {
			handle(nextId, this.cachedPropertyStore.getProperty(nextId).getCurrentValue());
		}
		this.cachedPropertyStore.removeCachedPropertyStoreListener(changedProperties2);
		return changedProperties2;
	}
	
	private void handle(String id, String value) {	
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
			}
			else if (spec.isEnable()) {
				property.setEnabled(spec.getExpression().getValue().equals(DependencySpec.True));
			}
			else if (spec.isValue()) {
				property.setCurrentValue(spec.getExpression().getValue());
			}
			else if (spec.isMin()) {
				property.setMin(spec.getExpression().getValue());
			}
			else if (spec.isMax()) {
				property.setMax(spec.getExpression().getValue());
			}
			spec.consumed();
		}
	}

	public CachedPropertyStore getCachedPropertyStore() {
		return this.cachedPropertyStore;
	}

}

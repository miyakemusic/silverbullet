package jp.silverbullet.dependency2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.silverbullet.property2.ListDetailElement;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.web.ui.PropertyGetter;

public class DependencyEngine {

	private DepPropertyStore  store;
	private DependencySpecHolder specHolder;
	private CachedPropertyStore cachedPropertyStore;
	private ExpressionCalculator calculator;
	private Id userChangedId;
	private List<DependencyListener> listeners = new ArrayList<>();
	private CommitListener commitListener;
	
	public DependencyEngine(DependencySpecHolder specHolder, DepPropertyStore  store) {
		this.store = store;
		this.specHolder = specHolder;
		calculator = new ExpressionCalculator() {
			@Override
			protected RuntimeProperty getProperty(String id) {
				return cachedPropertyStore.getProperty(id);
			}
		};
	}

	public void requestChanges(List<IdValue> idValues) throws RequestRejectedException {
		this.cachedPropertyStore = new CachedPropertyStore(store);
		for (IdValue idValue : idValues) {
			changeValue(idValue.getId(), idValue.getValue());
		}
		
		this.cachedPropertyStore.commit();
		fireCompleteEvent();
	}
	
	public void requestChange(Id id, String value) throws RequestRejectedException {
		for (DependencyListener listener : this.listeners) {
			listener.onStart(id, value);
		}
		this.cachedPropertyStore = new CachedPropertyStore(store);
		changeValue(id, value);
		
		if (this.commitListener != null) {
			CommitListener.Reply reply = this.commitListener.confirm("");
			if (reply.equals(CommitListener.Reply.Accept)) {
				this.cachedPropertyStore.commit();
				fireCompleteEvent();					
			}
			else if (reply.equals(CommitListener.Reply.Reject)) {
				// Do nothing
			}
			else if (reply.equals(CommitListener.Reply.Pending)) {
				
			}
		}
		else {
			this.cachedPropertyStore.commit();
			fireCompleteEvent();			
		}
	}

	private void fireCompleteEvent() {
		String changedIds = this.getChangedIds().toString().replace("[", "").replace("]", "").replaceAll(" ", "");
		if (!changedIds.isEmpty()) {
			for (DependencyListener listener : this.listeners) {
				listener.onCompleted(changedIds);
			}
		}
	}

	private void changeValue(Id id, String value) throws RequestRejectedException {
		this.userChangedId = id;
		ChangedProperties prevChangedProperties = new ChangedProperties(new ArrayList<Id>(Arrays.asList(id)));
		this.cachedPropertyStore.addCachedPropertyStoreListener(prevChangedProperties);
		setCurrentValue(cachedPropertyStore.getProperty(id.toString()), value);
		handle(id, value);
		this.cachedPropertyStore.removeCachedPropertyStoreListener(prevChangedProperties);
		
		while(prevChangedProperties.getIds().size() > 0) {
			prevChangedProperties = handleNext(prevChangedProperties);
		}
	}

	private ChangedProperties handleNext(ChangedProperties prevChangedProperties) throws RequestRejectedException {
		ChangedProperties changedProperties2 = new ChangedProperties(prevChangedProperties.getIds());
		this.cachedPropertyStore.addCachedPropertyStoreListener(changedProperties2);
		for (Id nextId :  prevChangedProperties.getIds()) {
			handle(nextId, this.cachedPropertyStore.getProperty(nextId.toString()).getCurrentValue());
		}
		this.cachedPropertyStore.removeCachedPropertyStoreListener(changedProperties2);
		return changedProperties2;
	}
	
	private void handle(Id id, String value) throws RequestRejectedException {	
		List<RuntimeDependencySpec> specs = this.getSpecHolder().getRuntimeSpecs(id.getId());
		specs = findSatisfiedSpecs(specs, id.getIndex());
		for (RuntimeDependencySpec spec : specs) {	
			if (spec.getId().equals(this.userChangedId.getId())) {
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
				if (spec.getExpression().isValueCalculationEnabled()) {
					spec.getExpression().setValue(calculator.calculate("ret=" + spec.getExpression().getValue()));
				}
			}
			
			if (!spec.isExecutionConditionSatistied()) {
				continue;
			}
			RuntimeProperty property = this.cachedPropertyStore.getProperty(spec.getId() + "@" + id.getIndex());
			
			if (spec.isOptionEnabled()) {
				property.disableOption(spec.getTargetOption(), !spec.getExpression().getValue().equalsIgnoreCase(DependencySpec.True));

				// previous code
//				for (ListDetailElement e: property.getListDetail()) {
//					if (e.getId().equals(spec.getTargetOption())) {
//						property.disableOption(e.getId(), !spec.getExpression().getValue().equalsIgnoreCase(DependencySpec.True));
//						break;
//					}
//				}
				
				// selects other one if current is masked
				if (property.isOptionDisabled(property.getCurrentValue())) {
					if (spec.isReject()) {
						throw new RequestRejectedException(property.getId() + "Selection is valid");
					}
					else {
						reselectNearestValue(property);
					}
				}
			}
			else if (spec.isEnable()) {
				property.setEnabled(spec.getExpression().getValue().equalsIgnoreCase(DependencySpec.True));
			}
			else if (spec.isValue()) {
				String val = spec.getExpression().getValue();
				if (property.isListProperty()) {
					val = val.replace("%", "");
				}
				setCurrentValue(property, val);
			}
			else if (spec.isMin()) {
				String min = spec.getExpression().getValue();
				if (this.isLeftLarger(min, property.getCurrentValue())) {
					if (spec.isReject()) {
						throw new RequestRejectedException(property.getId()+ ":  Changing Min. is rejected");
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
						throw new RequestRejectedException(property.getId() + ": Changing Max. is rejected");
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
			else if (spec.isArraySize()) {
				String size = spec.getExpression().getValue();
				property.setSize(Integer.valueOf(size));
			}
			spec.consumed();
		}
	}

	private List<RuntimeDependencySpec> findSatisfiedSpecs(List<RuntimeDependencySpec> specs, int index) {
		List<RuntimeDependencySpec> ret = new ArrayList<RuntimeDependencySpec> ();
		Map<String, Set<RuntimeDependencySpec>> values = new HashMap<String, Set<RuntimeDependencySpec>>();
		for (RuntimeDependencySpec spec: specs) {	
			spec.getExpression().setValue(applyIndex(spec.getExpression().getValue(), index));
			spec.getExpression().setTrigger(applyIndex(spec.getExpression().getTrigger(), index));
			spec.getExpression().setCondition(applyIndex(spec.getExpression().getCondition(), index));
			
			if (spec.isElse()) {
				spec.setExecutionConditionSatistied(true);
			}
			else {
				boolean satisfied = true;
				satisfied &= Boolean.valueOf(calculator.calculate("ret=" + spec.getExpression().getTrigger()));
				if (spec.getExpression().isValueCalculationEnabled()) {
				//	satisfied &= Boolean.valueOf(calculator.calculate("ret=" + spec.getExpression().getValue()));
				}
				if (spec.getExpression().isConditionEnabled()) {
					satisfied &= Boolean.valueOf(calculator.calculate("ret=" + spec.getExpression().getCondition()));
				}
				
				spec.setExecutionConditionSatistied(satisfied);
				
				if (spec.isExecutionConditionSatistied() && spec.getTarget().equals(DependencySpec.Value)) {
					// remember id, value and spec
					if (!values.keySet().contains(spec.getId())) {
						values.put(spec.getId(), new HashSet<RuntimeDependencySpec>());
					}
					values.get(spec.getId()).add(spec);
				}
			}
			
			if (spec.isExecutionConditionSatistied()) {
				ret.add(spec);
			}
		}
		
		/// selects only one spec.
		selectOnlyOneSpec(values, ret);
		return ret;
	}

	private String applyIndex(String string, int index) {
		for (String id : IdCollector.sortByLength(IdCollector.collectIds(string))) {
			string = string.replace("$" + id, "$" + id + "@" + index);
		}
		return string;
	}

	private void selectOnlyOneSpec(Map<String, Set<RuntimeDependencySpec>> values, List<RuntimeDependencySpec> ret) {
		RuntimeDependencySpec targetSpec = null;
		for (String id : values.keySet()) {
			Set<RuntimeDependencySpec> satisfiedSpecs = values.get(id);
			if (satisfiedSpecs.size() == 1) {
				continue;
			}
			
			RuntimeProperty currentProperty = this.cachedPropertyStore.getProperty(id);
			String currentValue = currentProperty.getCurrentValue();
			List<String> list = currentProperty.getListIds();
	
			int min = list.size();
			int indexCurrent = list.indexOf(currentValue);
			
			for (RuntimeDependencySpec spec : satisfiedSpecs) {
				String option = spec.getExpression().getValue();
				int index = list.indexOf(option);
				int diff = Math.abs(indexCurrent - index);
				if (diff < min) {
					min = diff;
					targetSpec = spec;
				}
			}
			
			if (targetSpec != null) {
				ret.add(targetSpec);
				targetSpec = null;
			}
		}


	}

	protected DependencySpecHolder getSpecHolder() {
		return this.specHolder;
	}

	private void setCurrentValue(RuntimeProperty property, String val) throws RequestRejectedException {
		if (property.isNumericProperty()) {
			if (isLeftLarger(property.getMin(), val)) {
				throw new RequestRejectedException(property.getId() + " Under Min. " + property.getMin());
			}
			else if (isLeftLarger(val, property.getMax())) {
				throw new RequestRejectedException(property.getId() + " Over Max. " + property.getMax());
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

	private void reselectNearestValue(RuntimeProperty property) {
		List<String> listIds = property.getListIds();
		int currentIndex = listIds.indexOf(property.getCurrentValue());
		int limit = Math.max(listIds.size() - currentIndex, currentIndex);
		for (int width = 1; width < limit; width++) {
			int index = currentIndex + width;
			//if (canSelect(index, listIds, property.getListMask())) {
			if (property.isOptionEnabled(index)) {
				property.setCurrentValue(listIds.get(index));
				return;
			}
			index = currentIndex - width;
			//if (canSelect(index, listIds, property.getListMask())) {
			if (property.isOptionEnabled(index)) {
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

	public List<String> getDebugLog() {
		return cachedPropertyStore.getDebugLog();
	}

	public List<String> getChangedIds() {
		return cachedPropertyStore.getChangedIds();
	}

	public Map<String, List<ChangedItemValue>> getChagedItems() {
		return cachedPropertyStore.getChangedHistory();
	}

	public void addDependencyListener(DependencyListener dependencyListener) {
		listeners .add(dependencyListener);
	}

	public void removeDependencyListener(DependencyListener listener) {
		this.listeners.remove(listener);
	}

	public void setCommitListener(CommitListener commitListener) {
		this.commitListener = commitListener;
	}

	public void requestChange(String id, int index, String value) throws RequestRejectedException {
		this.requestChange(new Id(id, index), value);
	}

	public void requestChange(String id, String value) throws RequestRejectedException {
		requestChange(id, 0, value);
	}

}

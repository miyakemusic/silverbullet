package jp.silverbullet.dependency2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.silverbullet.dependency2.CommitListener.Reply;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.web.ui.PropertyGetter;

/**
 * @author çNçG
 *
 */
/**
 * @author çNçG
 *
 */
public abstract class DependencyEngine {

	private PropertyGetter  store;
//	private DependencySpecHolder specHolder;
	private CachedPropertyStore cachedPropertyStore;
	private ExpressionCalculator calculator;
	private Id userChangedId;
	private List<DependencyListener> listeners = new ArrayList<>();
	private CommitListener commitListener;
	
	public DependencyEngine(PropertyGetter  store) {
		this.store = store;
//		this.specHolder = specHolder;
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
			try {
				changeValue(idValue.getId(), idValue.getValue(), false);
			}
			catch (RequestRejectedException e) {
				e.setSource(idValue.getId());
				throw e;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		this.cachedPropertyStore.commit();
		fireCompleteEvent();
	}

	public void requestChange(Id id, String value) throws RequestRejectedException {
		requestChange(id, value, false);
	}
	
	public void requestChange(Id id, String value, boolean forceChange) throws RequestRejectedException {
		this.listeners.forEach(listener -> listener.onStart(id, value));

		this.cachedPropertyStore = new CachedPropertyStore(store);

		try {
			changeValue(id, value, forceChange);
			if (this.commitListener != null) {
				CommitListener.Reply reply = this.commitListener.confirm(this.cachedPropertyStore.getChangedHistory());
				if (reply.equals(CommitListener.Reply.Accept)) {
					this.cachedPropertyStore.commit();
					fireCompleteEvent();					
				}
				else if (reply.equals(CommitListener.Reply.Reject)) {
					// Do nothing
					fireRejectedEvent(id);
				}
				else if (reply.equals(CommitListener.Reply.Pend)) {
					
				}
			}
			else {
				this.cachedPropertyStore.commit();
				fireCompleteEvent();			
			}
		}
		catch (RequestRejectedException e) {
			e.setSource(id);
			fireRejectedEvent(e.getSource());
			throw e;
		}
	}

	private void fireRejectedEvent(Id sourceId) {
		this.listeners.forEach(listener -> listener.onRejected(sourceId));
	}
	
	private void fireCompleteEvent() {
		String changedIds = this.getChangedIds().toString().replace("[", "").replace("]", "").replaceAll(" ", "");
		if (!changedIds.isEmpty()) {
			this.listeners.forEach(listener -> listener.onCompleted(changedIds));
		}
	}

	private void changeValue(Id id, String value, boolean forceChange) throws RequestRejectedException {
		this.userChangedId = id;
		ChangedProperties prevChangedProperties = new ChangedProperties(new HashSet<Id>(Arrays.asList(id)));
		this.cachedPropertyStore.addCachedPropertyStoreListener(prevChangedProperties);
		setCurrentValue(cachedPropertyStore.getProperty(id.toString()), value, forceChange);
		
		fireProgress(this.cachedPropertyStore.getDebugLog());
		
		Set<Id> needsFindSelection = new HashSet<>();
		handle(id, value, forceChange, needsFindSelection);
		selectAvailableOption(needsFindSelection);
		
		this.cachedPropertyStore.removeCachedPropertyStoreListener(prevChangedProperties);
		
		fireProgress(this.cachedPropertyStore.getDebugLog());
		
		Set<Id> done = new LinkedHashSet<>();
		while(prevChangedProperties.getIds().size() > 0) {
			prevChangedProperties = handleNext(prevChangedProperties, forceChange);
			fireProgress(this.cachedPropertyStore.getDebugLog());
			// avoids infinite loop. but this design is not the best
			if (contains(done, prevChangedProperties.getIds())) {
				break;
			}
			done.addAll(prevChangedProperties.getIds());
		}
		this.listeners.forEach(listener -> listener.onResult(this.cachedPropertyStore.getChangedHistory()));
	}

	private void fireProgress(List<String> log) {
		if (log.size()==0) {
			return;
		}
		this.listeners.forEach(listener -> listener.onProgress(log));
		log.clear();
	}

	private boolean contains(Set<Id> done, Set<Id> ids) {
		for (Id id : ids) {
			for (Id id2: done) {
				if (id.equals(id2)) {
					return true;
				}
			}
		}
		return false;
	}

	private ChangedProperties handleNext(ChangedProperties prevChangedProperties, boolean forceChange) throws RequestRejectedException {
		ChangedProperties changedProperties2 = new ChangedProperties(prevChangedProperties.getIds());
		this.cachedPropertyStore.addCachedPropertyStoreListener(changedProperties2);
		
		Set<Id> needsFindSelection = new HashSet<>();
		
		for (Id nextId :  prevChangedProperties.getIds()) {
			handle(nextId, this.cachedPropertyStore.getProperty(nextId.toString()).getCurrentValue(), forceChange, needsFindSelection);
		}
		
		selectAvailableOption(needsFindSelection);
		this.cachedPropertyStore.removeCachedPropertyStoreListener(changedProperties2);
		return changedProperties2;
	}

	private void selectAvailableOption(Set<Id> needsFindSelection) throws RequestRejectedException {
		for (Id id : needsFindSelection) {
			RuntimeProperty property = this.cachedPropertyStore.getProperty(id.getId(), id.getIndex());
			
			boolean reselected = reselectClosestValue(property);
			if (!reselected) {
				throw new RequestRejectedException(id, property.getId() + "Nothing can be selected"); 
			}
		}
	}
	
	private void handle(Id id, String value, boolean forceChange, Set<Id> needsFindSelection) throws RequestRejectedException {	
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
			RuntimeProperty property = this.cachedPropertyStore.getProperty(RuntimeProperty.createIdText(spec.getId(), id.getIndex()));
			
			if (spec.isOptionEnabled()) {
				property.enableOption(spec.getTargetOption(), spec.getExpression().getValue().equalsIgnoreCase(DependencySpec.True));
				
				// selects other one if current is masked
				if (property.isOptionDisabled(property.getCurrentValue())) {
					Id targetId = new Id(property.getId(), property.getIndex());
					if (spec.isReject()) {
						throw new RequestRejectedException(targetId, property.getId() + "Selection is valid");
					}
					else {
						needsFindSelection.add(targetId);
//						boolean reselected = reselectClosestValue(property);
//						if (!reselected) {
//							throw new RequestRejectedException(property.getId() + "Nothing can be selected"); 
//						}
					}
				}
			}
			else if (spec.isEnable()) {
				property.setEnabled(spec.getExpression().getValue().equalsIgnoreCase(DependencySpec.True));
			}
			else if (spec.isValue()) {
				String val = spec.getExpression().getValue();
				if (val.contains("$")) {
					val = this.calculator.calculate(val);
				}
				if (property.isList()) {
					val = val.replace("%", "");
				}
				setCurrentValue(property, val, forceChange);
			}
			else if (spec.isMin()) {
				String min = spec.getExpression().getValue();
				if (min.contains("$")) {
					min = this.calculator.calculate(min);
				}
				if (this.isLeftLarger(min, property.getCurrentValue())) {
					if (spec.isReject()) {
						throw new RequestRejectedException(new Id(property.getId(), property.getIndex()), property.getId()+ ":  Changing Min. is rejected");
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
				if (max.contains("$")) {
					max = this.calculator.calculate(max);
				}
				if (this.isLeftLarger(property.getCurrentValue(), max)) {
					if (spec.isReject()) {
						throw new RequestRejectedException(new Id(property.getId(), property.getIndex()), property.getId() + ": Changing Max. is rejected");
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
		
		/// selects only one spec when multiple candidates exist
		selectOnlyOneSpec(values, ret);
		return ret;
	}

	private String applyIndex(String string, int index) {
		for (String id : IdUtility.sortByLength(IdUtility.collectIds(string))) {
			string = string.replace("$" + id, "$" + RuntimeProperty.createIdText(id, index));
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

	abstract protected DependencySpecHolder getSpecHolder();

	private void setCurrentValue(RuntimeProperty property, String val, boolean forceChange) throws RequestRejectedException {
		property.setForceChange(forceChange);
		if (property.isNumericProperty()) {
			Id id = new Id(property.getId(), property.getIndex());
			if (isLeftLarger(property.getMin(), val)) {
				throw new RequestRejectedException(id, property.getId() + " Under Min. " + property.getMin());
			}
			else if (isLeftLarger(val, property.getMax())) {
				throw new RequestRejectedException(id, property.getId() + " Over Max. " + property.getMax());
			}
			else {
				property.setCurrentValue(val);
			}
		}
		else {
			property.setCurrentValue(val);
		}
		property.setForceChange(false);
	}

	private boolean isLeftLarger(String val1, String val2) {
		try {
			return Double.valueOf(val1) > Double.valueOf(val2);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private boolean reselectClosestValue(RuntimeProperty property) {
		if (!property.isOptionDisabled(property.getCurrentValue())) {
			return true;
		}
		List<String> listIds = property.getListIds();
		int currentIndex = listIds.indexOf(property.getCurrentValue());
		int limit = Math.max(listIds.size() - currentIndex, currentIndex);
		for (int width = 1; width <= limit; width++) {
			int index = currentIndex + width;
			if (index < listIds.size()) {
				if (!property.isOptionDisabled(index)) {
					property.setCurrentValue(listIds.get(index));
					return true;
				}
			}
			index = currentIndex - width;
			if (index >= 0) {
				if (!property.isOptionDisabled(index)) {
					property.setCurrentValue(listIds.get(index));
					return true;
				}
			}
		}
		return false;
	}


//	private boolean canSelect(int index, List<String> listIds, Map<String, Boolean> listMask) {
//		if (index < 0 || index >= listIds.size()) {
//			return false;
//		}
//		Boolean mask = listMask.get(listIds.get(index));
//		
//		return (mask == null) || !mask;
//	}

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

//	public void requestChange(String id, int index, String value) throws RequestRejectedException {
//		this.requestChange(new Id(id, index), value);
//	}
//
	public void requestChange(String id, String value) throws RequestRejectedException {
		requestChange(new Id(id, 0), value);
	}

	public void setPendedReply(Reply accept) {
		if (accept.equals(Reply.Accept)) {
			this.cachedPropertyStore.commit();
		}
	}

}

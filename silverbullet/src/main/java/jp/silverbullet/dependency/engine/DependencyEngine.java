package jp.silverbullet.dependency.engine;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import jp.silverbullet.ChangedItemValue;
import jp.silverbullet.SvProperty;
import jp.silverbullet.SvPropertyStore;
import jp.silverbullet.XmlPersistent;
import jp.silverbullet.dependency.speceditor2.DependencyFormula;
import jp.silverbullet.dependency.speceditor2.DependencySpec;
import jp.silverbullet.dependency.speceditor2.DependencySpecDetail;
import jp.silverbullet.dependency.speceditor2.DependencySpecHolder;
import jp.silverbullet.property.ListDetailElement;
import jp.silverbullet.web.WebSocketBroadcaster;

public abstract class DependencyEngine {

	protected abstract SvPropertyStore getPropertiesStore();
	private XmlPersistent<DependencySpecHolder> persister = new XmlPersistent<>();
	private DependencySpecHolder specHolder = new DependencySpecHolder();
	private TentativePropertyStore tentativeStore;
	private Set<DependencyListener> dependencyListeners = new HashSet<DependencyListener>();
	
	private SvCalculator calculator = new SvCalculator(new SvCalculatorModel() {
		@Override
		public List<String> getAllIds() {
			return getPropertiesStore().getAllIds();
		}

		@Override
		public String getCurrentValue(String id) {
			return tentativeStore.getProperty(id).getCurrentValue();
		}		
	});
	
	public DependencyEngine() {
		this.specHolder = new DependencySpecHolder();
	}
	
	public DependencySpecHolder getSpecHolder() {
		return specHolder;
	}

	public void requestChange(String id, String value) throws RequestRejectedException {
		
		DependencyBuilder dependencyBuilder = new DependencyBuilder(id, specHolder, new MarcoExtractor(getPropertiesStore()));
		
		tentativeStore = new TentativePropertyStore(getPropertiesStore());
		
		try {
			setCurrentValue(tentativeStore.getProperty(id), value);
			for (List<DependencySpecDetail> specs : dependencyBuilder.getLayers().values()) {
				for (DependencySpecDetail spec : specs ) {
					doDependency(spec, tentativeStore);
				}
			}
			
			if (isConfirmEnabled()) {
				String message = "";//tentativeStore.getMessage(id);
				for (String id2 : tentativeStore.getChangedIds()) {
					List<ChangedItemValue> changes = tentativeStore.getChanged(id2);
					for (ChangedItemValue item : changes) {
						DependencySpec spec = this.specHolder.getSpecs().get(id2);
						if (spec != null && spec.isConfirmEnabled(item.element)) {
							if (!id.equals(id2)) {
								message += id2 + "." + item.element + " -> " + item.value;
							}
						}
					}
				}
				
				if (!message.isEmpty()) {
					if (confirmDependency(message)) {
						tentativeStore.commit();
					}
					else {
						throw new RequestRejectedException("");
					}
				}
				else {
					tentativeStore.commit();
				}
			}
			else {
				tentativeStore.commit();
			}
			
			fireResult();

		}
		catch (DependencyException e) {
			throw new RequestRejectedException(e.getMessage());
		}
	}

	protected void fireResult() {
		WebSocketBroadcaster.getInstance().sendMessage(this.getChangedIds().toString().replace("[", "").replace("]", ""));
		for (DependencyListener listener : dependencyListeners){
			listener.onResult(this.tentativeStore.getChangedHistory());
		}
	}

	private boolean confirmDependency(String message) {
		boolean ret = true;
		for (DependencyListener listener : this.dependencyListeners) {
			ret &= listener.confirm(message);
		}
		return ret;
	}

	protected boolean isConfirmEnabled() {
		return dependencyListeners.size() > 0;
	}
	
	public Map<String, List<ChangedItemValue>> getChagedItems() {
		return tentativeStore.getChangedHistory();
	}

	private void doDependency(DependencySpecDetail specDetail, TentativePropertyStore propertyStore) throws DependencyException {
		SvProperty changedProperty = propertyStore.getProperty(specDetail.getPassiveId());
//		System.out.println(changedProperty.getId());
		Boolean conditionSatisfied = false;
		String value = this.tentativeStore.getProperty(specDetail.getSpecification().getId()).getCurrentValue();
		
		conditionSatisfied = isConditionSatisfied(specDetail, value);
		
		if (!conditionSatisfied) {
			return;
		}
		String passiveElement = specDetail.getPassiveElement();
		// Common dependency
		if (passiveElement.equals(DependencyFormula.ENABLED)) {
			Boolean b = calcAndBoolean(specDetail, passiveElement);
			changedProperty.setEnabled(b);
		}
		else if (passiveElement.equals(DependencyFormula.VISIBLE)) {
			Boolean b = calcAndBoolean(specDetail, passiveElement);
			changedProperty.setVisible(b);
		}
		else {	
			// Specific dependency
			if (changedProperty.isListProperty()) {
				if (passiveElement.equals(DependencyFormula.VALUE)) {
					changedProperty.setCurrentValue(specDetail.getSpecification().getValueMatched());
				}
				else {
					if (specDetail.getSpecification().getElement().equals(DependencyFormula.VALUE)) {
						String maskId = passiveElement; 		
						Boolean mask = !Boolean.parseBoolean(specDetail.getSpecification().getValueMatched());//changedProperty.getListMask().get(maskId);
						if (mask && changedProperty.getCurrentValue().equals(maskId)) {
							for (ListDetailElement e: changedProperty.getListDetail()) {
								Boolean candidate = changedProperty.getListMask().get(e.getId());
								if (candidate == null || candidate.equals(true)) {
									changedProperty.setCurrentValue(e.getId());
									break;
								}
							}
						}
						changedProperty.addListMask(maskId, mask);
					}
				}
			}
			else if (changedProperty.isNumericProperty()) {
				if (passiveElement.equals(DependencyFormula.VALUE)) {
					String tmpValue = "";
					tmpValue = calculator.calculate(specDetail.getSpecification().getValueMatched());				
					setCurrentValue(changedProperty, tmpValue);
				}
				else if (passiveElement.equals(DependencyFormula.MAX)) {
					if (conditionSatisfied) {
						changedProperty.setMax(specDetail.getSpecification().getValueMatched());
					}
					if (Double.valueOf(changedProperty.getCurrentValue()) > Double.valueOf(changedProperty.getMax())) {
						changedProperty.setCurrentValue(changedProperty.getMax());
					}
				}
				else if (passiveElement.equals(DependencyFormula.MIN)) {
					if (conditionSatisfied) {
						changedProperty.setMin(specDetail.getSpecification().getValueMatched());
					}
					if (Double.valueOf(changedProperty.getCurrentValue()) < Double.valueOf(changedProperty.getMin())) {
						changedProperty.setCurrentValue(changedProperty.getMin());
					}
				}
			}
			else if (changedProperty.isActionProperty() || changedProperty.isBooleanProperty()) {
				changedProperty.setCurrentValue(specDetail.getSpecification().getValueMatched());
			}
		}
	}

	protected Boolean calcAndBoolean(DependencySpecDetail specDetail,
			String passiveElement) {
		Boolean b = Boolean.parseBoolean(specDetail.getSpecification().getValueMatched());
		for (DependencySpecDetail d : this.specHolder.getPassiveRelations(specDetail.getPassiveId(), passiveElement)) {
			if (!d.equals(specDetail) && d.getPassiveElement().equals(passiveElement)) {
				boolean satisfied = isConditionSatisfied(d, this.tentativeStore.getProperty(d.getSpecification().getId()).getCurrentValue());
				if (satisfied) {
					b &= Boolean.parseBoolean(d.getSpecification().getValueMatched());
				}
			}
		}
		return b;
	}

	protected Boolean isConditionSatisfied(DependencySpecDetail specDetail,
			String value) {
		Boolean conditionSatisfied;
		if (specDetail.getSpecification().getRightSide().equals(DependencyFormula.ANY)) {
			conditionSatisfied = true;
		}
//		else if (specDetail.getSpecification().getRightSide().equals(DependencyFormula.OTHER)) {
//			conditionSatisfied = true;
//		}
		else if (specDetail.getSpecification().getEvalution().equals(DependencyFormula.EQUAL)) {
			conditionSatisfied = specDetail.getSpecification().getRightSide().equals(value);
		}
		else if (specDetail.getSpecification().getEvalution().equals(DependencyFormula.NOTEQUAL)) {
			conditionSatisfied = !specDetail.getSpecification().getRightSide().equals(value);
		}
		else {
			String formula = value + specDetail.getSpecification().getEvalution() + specDetail.getSpecification().getRightSide() ;
			String ret = this.calculator.calculate(formula);
			conditionSatisfied = Boolean.parseBoolean(ret);
		}
		return conditionSatisfied;
	}
	

	protected void setCurrentValue(SvProperty svProperty, String value) throws DependencyException {
		if (svProperty.isNumericProperty()) {
			if (DependencyFormula.isLarger(value, svProperty.getMax())) {
				throw new DependencyException(svProperty.getTitle() + " (" + svProperty.getId() + ")" + " Over Max (" + svProperty.getMin() + " to " + svProperty.getMax() + ")");
			}
			else if (DependencyFormula.isLarger(svProperty.getMin(), value)) {
				throw new DependencyException(svProperty.getTitle() + " (" + svProperty.getId() + ")" + " Under Min (" + svProperty.getMin() + " to " + svProperty.getMax() + ")");
			}
			else {
				svProperty.setCurrentValue(value);
			}
		}
		else {
			svProperty.setCurrentValue(value);
		}
	}
	
	public void saveSpec(String filename) {
		try {
			persister.save(this.specHolder, filename, DependencySpecHolder.class);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void loadSpec(String filename) {
		try {
			this.specHolder = persister.load(filename, DependencySpecHolder.class);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addDependencyListener(DependencyListener dependencyListener) {
		this.dependencyListeners.add(dependencyListener);
	}

	public List<String> getChangedIds() {
		return tentativeStore.getChangedIds();
	}

}

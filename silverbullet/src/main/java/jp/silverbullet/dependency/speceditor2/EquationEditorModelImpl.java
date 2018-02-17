package jp.silverbullet.dependency.speceditor2;

import java.util.List;

import jp.silverbullet.SvProperty;
import jp.silverbullet.property.PropertyHolder;

public class EquationEditorModelImpl implements EquationEditorModel {

	private DependencySpecEditorModel model;
	private String element = "";
	private String operator = "";
	private String answer = "";
	private String decision = "";
	private String id = "";
	private SvProperty property;
	private EquationEditorModelListener listener;
	private boolean updating;

	private static SvProperty prevSelected = null;
	
	private CandidatesFatory candidatesFactory = new DefaultCandidatesFactory(property);
	private TargetCandidatesFactory targetCandidatesFactory = new DefaultTargetCandidatesFactory();
		
	public EquationEditorModelImpl(DependencySpecEditorModel model, String targetElement, MyNodeItem prev) {
		this.model = model; 
		if (targetElement.equals(DependencyFormula.VALUE)) {
			targetCandidatesFactory = new ValueTargetCandidatesFactory(model.getProperty());
		}
		else if (targetElement.equals(DependencyFormula.ENABLED) || targetElement.equals(DependencyFormula.VISIBLE)) {
			targetCandidatesFactory = new BooleanTargetCandidatesFactory(model.getProperty());
		}
		else if (targetElement.equals(DependencyFormula.MIN)) {
			targetCandidatesFactory = new MinTargetCandidatesFactory(model.getProperty());
		}
		else if (targetElement.equals(DependencyFormula.MAX)) {
			targetCandidatesFactory = new MaxTargetCandidatesFactory(model.getProperty());
		}
		else {
			
		}
		
		if (prev != null) {
			this.property = model.getPropertyStore().getProperty(prev.spec.getSpecification().getId());
			this.setId(property.getId());
			this.setDefault(prev);
		}
		else {
			if (prevSelected == null) {
				this.property = model.getPropertyStore().getAllProperties().get(0);
			}
			else {
				this.property = prevSelected;
			}	
			this.setId(property.getId());
		}
		
	}
	
	@Override
	public List<String> getAllIds() {
		return model.getPropertyStore().getAllIds();
	}

	@Override
	public PropertyHolder getPropertiesHolder() {
		return model.getPropertyHolder();
	}

	@Override
	public SvProperty getProperty(String id) {
		return model.getPropertyStore().getProperty(id);
	}

	@Override
	public List<String> getOperatorCandicates() {
		return this.candidatesFactory.getOperatorCandidates();
	}
	
	@Override
	public List<String> getAnswerCandidates() {
		return this.candidatesFactory.getAnswerCandidates();
	}
	
	@Override
	public void setElement(String selectedItem) {
		if (selectedItem == null)return;
		this.element = selectedItem;
		this.candidatesFactory.setElement(element);
		updateValues();
		this.fireChangeEvent();
	}
	@Override
	public void setOperator(String selectedItem) {
		if (selectedItem == null)return;
		this.operator = selectedItem;
		this.fireChangeEvent();
	}
	@Override
	public void setAnswer(String selectedItem) {
		if (selectedItem == null)return;
		this.answer = selectedItem;
		this.fireChangeEvent();
	}
	@Override
	public void setResult(String selectedItem) {
		if (selectedItem == null)return;
		this.decision = selectedItem;
		this.fireChangeEvent();
	}
	@Override
	public List<String> getResultCandidates() {
		return targetCandidatesFactory.getDecisionCandidates();
	}
	
	@Override
	public String getOperator() {
		return this.operator;
	}
	@Override
	public String getAnswer() {
		return this.answer;
	}
	@Override
	public String getElement() {
		return this.element;
	}
	@Override
	public String getResult() {
		return this.decision;
	}
	@Override
	public List<String> getElementCandidates() {
		return this.candidatesFactory.getElementCandidates();
	}
	@Override
	public void setId(String selected) {
		if (selected == null)return;
		this.id = selected;
		this.property = model.getPropertyStore().getProperty(id);
		if (this.property.isListProperty()) {
			this.candidatesFactory = new ListCandidatesFactory(property);
		}
		else if (this.property.isNumericProperty()) {
			this.candidatesFactory = new NumericCandidatesFactory(property);
		}
		else if (this.property.isActionProperty()) {
			this.candidatesFactory = new BooleanCandidatesFactory(property);
		}
		else {
			this.candidatesFactory = new DefaultCandidatesFactory(property);
		}
		candidatesFactory.setElement(candidatesFactory.getElementCandidates().get(0));
		
		prevSelected = this.property;
		updateValues();
		
		this.fireChangeEvent();
	}
	protected void updateValues() {
		if (!this.getElementCandidates().contains(this.element)) {
			this.element = this.getElementCandidates().get(0);
		}
		if (!this.getResultCandidates().contains(this.decision)) {
			this.decision = this.getResultCandidates().get(0);
		}

		if (!this.getOperatorCandicates().contains(this.operator)) {
			this.operator = this.getOperatorCandicates().get(0);
		}
		if (!this.getAnswerCandidates().contains(this.answer)) {
			this.answer = this.getAnswerCandidates().get(0);
		}
	}
	@Override
	public String getId() {
		return this.id;
	}
	@Override
	public void setOnChange(
			EquationEditorModelListener equationEditorModelListener) {
		this.listener = equationEditorModelListener;
	}
	
	private void fireChangeEvent() {
		if (this.updating) {
			return;
		}
		this.updating = true;
		if (this.listener != null) {
			listener.onChanged();
		}
		this.updating = false;
	}

	@Override
	public DependencyFormula getFormula() {
		DependencyFormula formula = new DependencyFormula(
				this.id, 
				this.element, 
				this.operator,
				this.answer);
		formula.setValueMatched(this.decision);
		return formula;
	}

	private void setDefault(MyNodeItem myNodeItem) {
		DependencySpecDetail spec = myNodeItem.spec;		
		this.setElement( spec.getSpecification().getElement() );
		this.setOperator( spec.getSpecification().getEvalution() );
		this.setAnswer( spec.getSpecification().getRightSide() );
		this.setResult( spec.getSpecification().getValueMatched() );
	}

	@Override
	public void setDefaultChoice(String string) {
		this.setElement(DependencyFormula.VALUE);
		this.setAnswer(string);
	}

}

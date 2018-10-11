package jp.silverbullet.trash.speceditor2;

import java.util.ArrayList;
import java.util.List;

import jp.silverbullet.SvProperty;
import jp.silverbullet.SvPropertyStore;
import jp.silverbullet.property.ListDetailElement;
import jp.silverbullet.property.PropertyHolder;
import jp.silverbullet.uidesigner.pane.SvPanelModel;

public class DependencySpecEditorModelImpl implements DependencySpecEditorModel {


	private SvPanelModel model;
	private SvProperty property;
	public DependencySpecEditorModelImpl(String id, SvPanelModel model) {
		this.model = model;
		this.property = model.getProperty(id);
	}

	@Override
	public SvProperty getProperty() {
		return property;
	}

	@Override
	public List<DependencySpecDetail> getSpecs(String element) {
		element = stripBracket(element);
//		DependencySpec spec = model.getDi().getDependencySpecHolder().getSpecs().get(property.getId());
//		if (spec == null) {
//			return new ArrayList<DependencySpecDetail>();
//		}
//		return spec.getSpecs(element);
		return null;
	}

	@Override
	public SvPropertyStore getPropertyStore() {
		return model.getPropertyStore();
	}

	@Override
	public PropertyHolder getPropertyHolder() {
		return model.getPropertyHolder();
	}

	@Override
	public DependencySpecHolder getDependencySpecHolder() {
		return null;
		//return model.getDi().getDependencySpecHolder();
	}

	@Override
	public List<String> getAllNodes() {
		SvProperty prop = this.getProperty();
		List<String> ret = new ArrayList<String>();
		ret.add(DependencySpecDetail.VISIBLE);
		ret.add(DependencySpecDetail.ENABLED);
		ret.add(DependencySpecDetail.VALUE);
		if (prop.isListProperty()) {
			for (ListDetailElement e : prop.getListDetail()) {
				ret.add(wrapBracket(e.getId()));
			}
		}
		else if (prop.isNumericProperty()) {
			ret.add(DependencySpecDetail.MIN);
			ret.add(DependencySpecDetail.MAX);
		}
		return ret;
	}

	@Override
	public void removeSpec(String id, DependencySpecDetail spec) {
		getDependencySpecHolder().removeSpec(id, spec);
	}

	@Override
	public void addSpec(String id, String element, DependencyFormula formula) {
		element = stripBracket(element);
		getDependencySpecHolder().addSpec(id, element, formula);

//		if (this.getDependencySpecHolder().getPassiveRelations(id, element).size() == 1) {
//			addAlternativeCondition(id, element, formula);
//		}
	}

	private void addAlternativeCondition(String id, String element, DependencyFormula formula) {
		if (formula.getValueMatched().equals(DependencyFormula.TRUE) || formula.getValueMatched().equals(DependencyFormula.FALSE)) {
			
			DependencyFormula formula2 = formula.clone();
			if (formula.getEvalution().equals(DependencyFormula.EQUAL)) {
				formula2.setEvalution(DependencyFormula.NOTEQUAL);
			}
			else if (formula.getEvalution().equals(DependencyFormula.NOTEQUAL)) {
				formula2.setEvalution(DependencyFormula.EQUAL);
			}
			formula2.setValueMatched(DependencyFormula.FALSE);
			
			if (formula.getValueMatched().equals(DependencyFormula.TRUE)) {
				formula2.setValueMatched(DependencyFormula.FALSE);
			}
			else if (formula.getValueMatched().equals(DependencyFormula.FALSE)) {
				formula2.setValueMatched(DependencyFormula.TRUE);
			}
			 
			getDependencySpecHolder().addSpec(id, element, formula2);
		}
	}
	
	private String wrapBracket(String id) {
		return "<" + id + ">";
	}
	
	private String stripBracket(String element) {
		return element.replace("<", "").replace(">", "");
	}

	@Override
	public void copy(DependencySpecDetail spec, List<String> selected) {
		getDependencySpecHolder().copy(spec, selected);
	}

	@Override
	public void copyAll(List<String> selected) {
		getDependencySpecHolder().copy(this.property.getId(), selected);
	}

	@Override
	public void setConfirmRequired(String element) {
		this.getDependencySpecHolder().getSpecs().get(this.getProperty().getId()).setConfirmEnabled(element, true);;
	}

	@Override
	public void addOpositeCondition(String element, DependencySpecDetail spec) {
//		element = stripBracket(element);
		DependencyFormula newSpec = spec.getSpecification().clone();
		if (this.getProperty().isListProperty()) {
			if (spec.getPassiveElement().equals(DependencySpecDetail.VISIBLE)) {
				
			}
			else if (spec.getPassiveElement().equals(DependencySpecDetail.ENABLED)) {
				
			}
			else {
				if (newSpec.getValueMatched().equals(DependencyFormula.TRUE)) {
					newSpec.setValueMatched(DependencyFormula.FALSE);
				}
				else {
					newSpec.setValueMatched(DependencyFormula.TRUE);
				}
				newSpec.setRightSide(DependencyFormula.OTHER);
			}
			
		}
		else if (this.getProperty().isBooleanProperty()) {
			if (spec.getPassiveElement().equals(DependencySpecDetail.VISIBLE)) {
				
			}
			else if (spec.getPassiveElement().equals(DependencySpecDetail.ENABLED)) {
				
			}
			else {
				if (spec.getSpecification().getRightSide().equals(DependencyFormula.TRUE)) {
					newSpec.setRightSide(DependencyFormula.FALSE);
				}
				else if (spec.getSpecification().getRightSide().equals(DependencyFormula.FALSE)) {
					newSpec.setRightSide(DependencyFormula.TRUE);
				}
				if (newSpec.getValueMatched().equals(DependencyFormula.TRUE)) {
					newSpec.setValueMatched(DependencyFormula.FALSE);
				}
				else {
					newSpec.setValueMatched(DependencyFormula.TRUE);
				}
			}
		}
		else {
			return;
		}
		getDependencySpecHolder().addSpec(getProperty().getId(), spec.getPassiveElement(), newSpec);
	}

}

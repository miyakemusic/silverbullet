package jp.silverbullet.dependency.speceditor2;

import java.util.List;

import jp.silverbullet.SvProperty;
import jp.silverbullet.property.PropertyHolder;

public interface EquationEditorModel {

	List<String> getAllIds();

	PropertyHolder getPropertiesHolder();

	SvProperty getProperty(String id);

	List<String> getOperatorCandicates();

	List<String> getAnswerCandidates();

	void setElement(String selectedItem);

	void setOperator(String selectedItem);

	void setAnswer(String selectedItem);

	void setResult(String selectedItem);

	List<String> getResultCandidates();

	String getOperator();

	String getAnswer();

	String getElement();

	String getResult();

	List<String> getElementCandidates();

	void setId(String selected);

	String getId();

	void setOnChange(EquationEditorModelListener equationEditorModelListener);

	DependencyFormula getFormula();

	void setDefaultChoice(String string);
}

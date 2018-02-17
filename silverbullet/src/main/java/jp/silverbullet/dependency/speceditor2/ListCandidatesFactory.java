package jp.silverbullet.dependency.speceditor2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.silverbullet.SvProperty;
import jp.silverbullet.property.ListDetailElement;

public class ListCandidatesFactory implements CandidatesFatory {

	private SvProperty property;
	private List<String> answerCandidates = new ArrayList<String>();
	
	public ListCandidatesFactory(SvProperty property) {
		this.property = property;
	}

	@Override
	public List<String> getAnswerCandidates() {
		return answerCandidates;
	}

	@Override
	public List<String> getElementCandidates() {
		List<String> ret = new ArrayList<String>();
		ret.add(DependencyFormula.VISIBLE);
		ret.add(DependencyFormula.ENABLED);
		ret.add(DependencyFormula.VALUE);
		return ret;
	}

	@Override
	public List<String> getOperatorCandidates() {
		List<String> ret = new ArrayList<String>();
		ret.add("=");
		ret.add("!=");
		return ret;
	}

	@Override
	public void setElement(String element) {
		this.answerCandidates.clear();
		if (element.equals(DependencyFormula.ENABLED) || element.equals(DependencyFormula.VISIBLE)) {
			answerCandidates.add(DependencyFormula.TRUE);
			answerCandidates.add(DependencyFormula.FALSE);
		}
		else if (element.equals(DependencyFormula.VALUE)) {
			for (ListDetailElement e : property.getListDetail()) {
				answerCandidates.add(e.getId());
			}
		}
	}

}

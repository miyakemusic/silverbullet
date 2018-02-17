package jp.silverbullet.dependency.speceditor2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.silverbullet.SvProperty;

public class NumericCandidatesFactory implements CandidatesFatory {

	private List<String> answerCandidates = new ArrayList<String>();
	private SvProperty property;
	
	public NumericCandidatesFactory(SvProperty property) {
		this.property = property;
	}

	@Override
	public List<String> getAnswerCandidates() {
		return answerCandidates;
	}

	@Override
	public List<String> getElementCandidates() {
		List<String> ret =  new ArrayList<String>(Arrays.asList(DependencyFormula.VALUE, DependencyFormula.VISIBLE, DependencyFormula.ENABLED));
		ret.add(DependencyFormula.MIN);
		ret.add(DependencyFormula.MAX);
		return ret;
	}

	@Override
	public List<String> getOperatorCandidates() {
		List<String> ret = new ArrayList<String>();
		ret.add(DependencyFormula.EQUAL);
		ret.add(DependencyFormula.NOTEQUAL);
		ret.add(DependencyFormula.LARGER);
		ret.add(DependencyFormula.SMALLER);

		return ret;
	}

	@Override
	public void setElement(String element) {
		answerCandidates.clear();
		if (element.equals(DependencyFormula.VALUE)) {
			answerCandidates.add(DependencyFormula.ANY);
			answerCandidates.add(this.property.getMin());
			answerCandidates.add(this.property.getMax());
		}
		else {
			answerCandidates.add(DependencyFormula.TRUE);
			answerCandidates.add(DependencyFormula.FALSE);
		}
	}

}

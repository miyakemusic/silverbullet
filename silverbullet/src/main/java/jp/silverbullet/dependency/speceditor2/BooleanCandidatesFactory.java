package jp.silverbullet.dependency.speceditor2;

import java.util.Arrays;
import java.util.List;

import jp.silverbullet.SvProperty;

public class BooleanCandidatesFactory implements CandidatesFatory {

	public BooleanCandidatesFactory(SvProperty property) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<String> getAnswerCandidates() {
		return Arrays.asList(DependencyFormula.ANY, DependencyFormula.TRUE, DependencyFormula.FALSE, DependencyFormula.OTHER);
	}

	@Override
	public List<String> getElementCandidates() {
		return Arrays.asList(DependencySpecDetail.VISIBLE, DependencySpecDetail.ENABLED, DependencySpecDetail.VALUE);
	}

	@Override
	public List<String> getOperatorCandidates() {
		return Arrays.asList(DependencyFormula.EQUAL);
	}

	@Override
	public void setElement(String element) {

	}

}

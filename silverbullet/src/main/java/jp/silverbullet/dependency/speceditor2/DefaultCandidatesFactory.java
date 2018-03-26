package jp.silverbullet.dependency.speceditor2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.silverbullet.SvProperty;

public class DefaultCandidatesFactory implements CandidatesFatory {

	public DefaultCandidatesFactory(SvProperty property) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<String> getAnswerCandidates() {
		List<String> ret = new ArrayList<String>();
		ret.add(DependencyFormula.TRUE);
		ret.add(DependencyFormula.FALSE);
		return ret;
	}

	@Override
	public List<String> getElementCandidates() {
		List<String> ret = new ArrayList<String>();
		ret.add(DependencySpecDetail.VISIBLE);
		ret.add(DependencySpecDetail.ENABLED);
		return ret;
	}

	@Override
	public List<String> getOperatorCandidates() {
		List<String> ret = new ArrayList<String>();
		ret.add(DependencyFormula.EQUAL);
		ret.add(DependencyFormula.NOTEQUAL);
		return ret;
	}

	@Override
	public void setElement(String element) {

	}

}

package jp.silverbullet.trash.speceditor2;

import java.util.Arrays;
import java.util.List;

import jp.silverbullet.SvProperty;

public class BooleanTargetCandidatesFactory implements TargetCandidatesFactory {

	public BooleanTargetCandidatesFactory(SvProperty property) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<String> getDecisionCandidates() {
		return Arrays.asList(DependencyFormula.TRUE, DependencyFormula.FALSE);
	}

}

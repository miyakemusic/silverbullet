package jp.silverbullet.dependency.speceditor2;

import java.util.Arrays;
import java.util.List;

public class DefaultTargetCandidatesFactory implements TargetCandidatesFactory {

	@Override
	public List<String> getDecisionCandidates() {
		return Arrays.asList(DependencyFormula.TRUE, DependencyFormula.FALSE);
	}

}

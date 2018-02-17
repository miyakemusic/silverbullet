package jp.silverbullet.dependency.speceditor2;

import java.util.ArrayList;
import java.util.List;

import jp.silverbullet.SvProperty;

public class MaxTargetCandidatesFactory implements TargetCandidatesFactory {

	private SvProperty property;

	public MaxTargetCandidatesFactory(SvProperty property) {
		this.property = property;
	}

	@Override
	public List<String> getDecisionCandidates() {
		List<String> ret = new ArrayList<String>();
		ret.add(property.getMax());
		return ret;
	}

}

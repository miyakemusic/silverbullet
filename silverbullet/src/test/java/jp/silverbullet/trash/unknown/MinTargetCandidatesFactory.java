package jp.silverbullet.trash.speceditor2;

import java.util.ArrayList;
import java.util.List;

import jp.silverbullet.SvProperty;

public class MinTargetCandidatesFactory implements TargetCandidatesFactory {

	private SvProperty property;

	public MinTargetCandidatesFactory(SvProperty property) {
		this.property = property;
	}

	@Override
	public List<String> getDecisionCandidates() {
		List<String> ret = new ArrayList<String>();
		ret.add(property.getMin());
		return ret;
	}

}

package jp.silverbullet.dependency.speceditor2;

import java.util.ArrayList;
import java.util.List;

import jp.silverbullet.SvProperty;
import jp.silverbullet.property.ListDetailElement;

public class ValueTargetCandidatesFactory implements TargetCandidatesFactory {

	private SvProperty property;

	public ValueTargetCandidatesFactory(SvProperty property) {
		this.property = property;
	}

	@Override
	public List<String> getDecisionCandidates() {
		List<String> ret = new ArrayList<String>();
		if (property.isListProperty()) {
			
			for (ListDetailElement e : property.getListDetail()) {
				ret.add(e.getId());
			}
		}
		else if (property.isNumericProperty()) {
			ret.add(property.getMin());
			ret.add(property.getMax());
		}
		else if (property.isActionProperty()) {
			ret.add(DependencyFormula.ANY);
		}
		else if (property.isBooleanProperty()) {
			ret.add(DependencyFormula.TRUE);
			ret.add(DependencyFormula.FALSE);
		}
		return ret;
	}

}

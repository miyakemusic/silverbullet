package jp.silverbullet.trashdependency.engine;

import java.util.ArrayList;
import java.util.List;

import jp.silverbullet.SvPropertyStore;
import jp.silverbullet.trash.speceditor2.DependencyFormula;
import jp.silverbullet.trash.speceditor2.DependencySpecDetail;

public class MarcoExtractor {

	private SvPropertyStore propertiesStore;

	public MarcoExtractor(SvPropertyStore propertiesStore) {
		this.propertiesStore = propertiesStore;
	}

	public List<DependencySpecDetail> extractMacros(List<DependencySpecDetail> specs) {
		if (specs.size() == 0) {
			return specs;
		}
		List<DependencySpecDetail>  ret = new ArrayList<>();
		DependencySpecDetail target = null;
		for (DependencySpecDetail detail : specs) {
			if (detail.getSpecification().getRightSide().equals(DependencyFormula.OTHER)) {
				target = detail;
				break;
			}
		}
		if (target == null) {
			return specs;
		}
		List<String> condition = new ArrayList<String>();
		for (DependencySpecDetail detail : specs) {
			if (!detail.getSpecification().getRightSide().equals(DependencyFormula.OTHER)) {
				if (detail.getPassiveElement().equals(target.getPassiveElement()) && !detail.getSpecification().getValueMatched().equals(target.getSpecification().getValueMatched())) {
					condition.add(detail.getSpecification().getRightSide());
				}
				ret.add(detail);
			}
		}
		List<String> allIds = new ArrayList<String>(propertiesStore.getProperty(target.getSpecification().getId()).getListIds());
		allIds.removeAll(condition);
		for (String id2 : allIds) {
			DependencySpecDetail d = target.clone();
			d.getSpecification().setRightSide(id2);
			
			d.getSpecification().setValueMatched(target.getSpecification().getValueMatched());
			ret.add(d);
		}
		return ret;
	}
}

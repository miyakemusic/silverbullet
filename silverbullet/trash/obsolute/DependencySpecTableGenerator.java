package obsolute;

import java.util.ArrayList;
import java.util.List;

public class DependencySpecTableGenerator {

	private DependencyEditorModel dependencyEditorModel;

	public DependencySpecTableGenerator(DependencyEditorModel dependencyEditorModel) {
		this.dependencyEditorModel = dependencyEditorModel;
	}

	public List<DependencyTableRowData> get(DependencySpec spec) {
		List<DependencyTableRowData> ret = new ArrayList<>();
		for (DependencyTargetElement e: spec.getDepExpHolderMap().keySet()) {
			DependencyExpressionHolderMap map = spec.getDepExpHolderMap().get(e);
			for (String key : map.keySet()) {
				for (DependencyExpressionHolder h : map.get(key)) {
					for (String k : h.getExpressions().keySet()) {
						DependencyExpression exp = h.getExpressions().get(k);
//						for (DependencyExpression exp : list.getDependencyExpressions()) {
							String presentation = this.dependencyEditorModel.convertPresentationElement(key, e);
							ret.add(new DependencyTableRowData(presentation, k, exp.getExpression().getExpression(),  exp.isConfirmationRequired(), exp));
//						}
					}
				}
			}
		}
		return ret;
	}

}

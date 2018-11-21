package jp.silverbullet.dependency2;

import java.util.List;

import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.CachedPropertyStore;
import jp.silverbullet.dependency.DepPropertyStore;
import jp.silverbullet.dependency.ExpressionCalculator;
import jp.silverbullet.dependency.RequestRejectedException;
import jp.silverbullet.property.ListDetailElement;

public class DependencyEngine {

	private DepPropertyStore store;
	private DependencySpecHolder specHolder;
	private CachedPropertyStore cachedPropertyStore;
	private ExpressionCalculator calculator;

	public DependencyEngine(DependencySpecHolder specHolder, DepPropertyStore store) {
		this.store = store;
		this.specHolder = specHolder;
		calculator = new ExpressionCalculator() {
			@Override
			protected SvProperty getProperty(String id) {
				return cachedPropertyStore.getProperty(id);
			}
			
		};
	}

	public void requestChange(String id, String value) throws RequestRejectedException {
		this.cachedPropertyStore = new CachedPropertyStore(store);

		this.cachedPropertyStore.getProperty(id).setCurrentValue(value);
		
		List<RuntimeDependencySpec> specs = this.specHolder.getRuntimeSpecs(id, value);
		for (RuntimeDependencySpec spec : specs) {
			
			boolean qualify = false;
			
			if (spec.isElse()) {
				if (!spec.otherConsumed()) {
					qualify = true;
				}
				else {
					qualify = false;
				}
			}
			else {
				String ret = calculator.calculate("ret=" + spec.getExpression().getTrigger());
				qualify = Boolean.valueOf(ret);
			}
			
			if (!qualify) {
				continue;
			}
			SvProperty property = this.cachedPropertyStore.getProperty(spec.getId());
			
			if (spec.isOptionEnabled()) {
				for (ListDetailElement e: property.getListDetail()) {
					if (e.getId().equals(spec.getTargetOption())) {
						property.addListMask(e.getId(), spec.getExpression().getValue().equals(DependencySpec.True));
						break;
					}
				}
			}
			else if (spec.isEnable()) {
				property.setEnabled(spec.getExpression().getValue().equals(DependencySpec.True));
			}
			else if (spec.isValue()) {
				
			}
			
			spec.consumed();
		}
	}

	public CachedPropertyStore getCachedPropertyStore() {
		return this.cachedPropertyStore;
	}

}

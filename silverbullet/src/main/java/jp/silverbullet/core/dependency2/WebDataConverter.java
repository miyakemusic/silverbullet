package jp.silverbullet.core.dependency2;

import java.util.List;

import jp.silverbullet.web.ui.PropertyGetter;

public class WebDataConverter {

	private DependencySpecHolder holder;
	private PropertyGetter properties;
	public WebDataConverter(DependencySpecHolder holder, PropertyGetter properties) {
		this.holder = holder;
		this.properties = properties;
	}
	public WebDependencySpec getSpec(String id) {
		WebDependencySpec ret = new WebDependencySpec();
		DependencySpec spec = this.holder.getSpec(id);
		
		createList(DependencySpec.Enable, ret, spec.getExpression(DependencySpec.Enable));
		createList(DependencySpec.Value, ret, spec.getExpression(DependencySpec.Value));
		
		if (isNumeric(id)) {
			createList(DependencySpec.Min, ret, spec.getExpression(DependencySpec.Min));
			createList(DependencySpec.Max, ret, spec.getExpression(DependencySpec.Max));
		}
		
		if (isList(id)) {
			for (String optionId : this.properties.getProperty(id).getListIds()) {
				createList(optionId, ret, spec.getExpression(DependencySpec.createOptionEnableId(optionId)));
			}
		}
		
		createList(DependencySpec.ArraySize, ret, spec.getExpression(DependencySpec.ArraySize));
		return ret;
	}
	private boolean isList(String id) {
		return this.properties.getProperty(id).isList();
	}
	private boolean isNumeric(String id) {
		return this.properties.getProperty(id).isNumericProperty();
	}
	private void createList(String name, WebDependencySpec ret, List<Expression> expressions) {
		if (expressions == null) {
			ret.add(name, createDummy());
			return;
		}
		for (Expression expression : expressions) {
			create(name, ret, expression);
		}
		ret.add(name, createDummy());
	}
	private WebDependencyElement createDummy() {
		WebDependencyElement ret = new WebDependencyElement();
		ret.value = DependencySpec.Null;
		ret.condition = DependencySpec.Null;
		ret.trigger = DependencySpec.Null;
		ret.silentChange = "";
		return ret;
	}
	private void create(String name, WebDependencySpec ret, Expression expression) {
		WebDependencyElement e = new WebDependencyElement();
		e.condition = expression.getCondition();
		e.trigger = expression.getTrigger();
		e.value = expression.getValue();
		e.silentChange = String.valueOf(expression.isSilentChange());
		ret.add(name, e);
	}
}

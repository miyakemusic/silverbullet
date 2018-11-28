package jp.silverbullet.dependency2;

import java.util.List;

import jp.silverbullet.dependency.DepPropertyStore;

public class WebDataConverter {

	private DependencySpecHolder holder;
	private DepPropertyStore properties;
	public WebDataConverter(DependencySpecHolder holder, DepPropertyStore properties) {
		this.holder = holder;
		this.properties = properties;
	}
	public WebDependencySpec getSpec(String id) {
		WebDependencySpec ret = new WebDependencySpec();
		DependencySpec spec = this.holder.getSpec(id);
		
		createList("Enable", ret, spec.getExpression(DependencySpec.Enable));
		createList("Value", ret, spec.getExpression(DependencySpec.Value));
		createList("Min", ret, spec.getExpression(DependencySpec.Min));
		createList("Max", ret, spec.getExpression(DependencySpec.Max));
		
		for (String optionId : this.properties.getProperty(id).getListIds()) {
			createList(optionId, ret, spec.getExpression(DependencySpec.OptionEnable + "#" + optionId));
		}
		
		return ret;
	}
	private void createList(String name, WebDependencySpec ret, List<Expression> expressions) {
		if (expressions == null) {
			ret.add(name, createDummy());
			return;
		}
		for (Expression expression : expressions) {
			create(name, ret, expression);
		}
	}
	private WebDependencyElement createDummy() {
		WebDependencyElement ret = new WebDependencyElement();
		ret.value = "";
		ret.condition = "";
		ret.trigger = "";
		return ret;
	}
	private void create(String name, WebDependencySpec ret, Expression expression) {
		WebDependencyElement e = new WebDependencyElement();
		e.condition = expression.getCondition();
		e.trigger = expression.getTrigger();
		e.value = expression.getValue();
		ret.add(name, e);
	}
}

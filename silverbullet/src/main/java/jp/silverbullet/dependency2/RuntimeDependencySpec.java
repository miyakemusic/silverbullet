package jp.silverbullet.dependency2;

import java.util.ArrayList;
import java.util.List;

public class RuntimeDependencySpec {
	private String id;
	private String target;
	private Expression expression;
	private List<RuntimeDependencySpec> elseSources = new ArrayList<>();
	private boolean consumed = false;
	private boolean executionConditionSatistied;
	
	public RuntimeDependencySpec(String id, String target, Expression expression) {
		this.id = id;
		this.target = target;
		this.expression = expression;
	}

	public String getId() {
		return this.id;
	}

	public boolean isValue() {
		return this.target.equals(DependencySpec.Value);
	}

	public boolean isEnable() {
		return this.target.equals(DependencySpec.Enable);
	}
	
	public boolean isOptionEnabled() {
		return this.target.startsWith("ID_");
	}

	public Expression getExpression() {
		return expression;
	}

	public String getTargetOption() {
		return this.target;
	}

	public void addElseSource(RuntimeDependencySpec spec) {
		elseSources.add(spec);
	}

	public void consumed() {
		this.consumed  = true;
	}

	public boolean isElse() {
		return this.expression.isElse();
	}

	public boolean otherConsumed() {
		for (RuntimeDependencySpec spec: this.elseSources) {
			if (spec.isConsumed()) {
				return true;
			}
		}
		return false;
	}

	private boolean isConsumed() {
		return this.consumed;
	}

	public boolean isMin() {
		return this.target.equals(DependencySpec.Min);
	}

	public boolean isMax() {
		return this.target.equals(DependencySpec.Max);
	}

	public void setExecutionConditionSatistied(boolean executionConditionSatistied) {
		this.executionConditionSatistied = executionConditionSatistied;
	}

	public boolean isExecutionConditionSatistied() {
		return executionConditionSatistied;
	}
	
}

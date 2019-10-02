package jp.silverbullet.dependency2;

import java.util.ArrayList;
import java.util.List;

public class RuntimeDependencySpec {
	private String id;
	private String target;
	private Expression expression;
	private List<RuntimeDependencySpec> elseSources = new ArrayList<>();
	private boolean consumed = false;
	private boolean executionConditionSatistied = true;
	private boolean reject;
	private RuntimeDependencySpec elseSpec;
	private boolean silentChange;
	private boolean blockPropagation;
	
	public RuntimeDependencySpec(String id, String target, Expression expression, boolean reject, 
			boolean silentChange, boolean blockPropagation) {
		this.id = id;
		this.target = target;
		this.expression = expression.clone();
		this.reject = reject;
		this.silentChange = silentChange;
		this.blockPropagation = blockPropagation;
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
		return this.target.startsWith(DependencySpec.OptionEnable);
	}

	public boolean isArraySize() {
		return this.target.equals(DependencySpec.ArraySize);
	}
	
	public Expression getExpression() {
		return expression;
	}

	public String getTargetOption() {
		return this.target.split(DependencySpec.SEPARATOR)[1];
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
		this.executionConditionSatistied &= executionConditionSatistied;
	}

	public boolean isExecutionConditionSatistied() {
		return executionConditionSatistied;
	}

	public boolean isReject() {
		return reject;
	}

	public List<RuntimeDependencySpec> getElseSources() {
		return elseSources;
	}


	public RuntimeDependencySpec getElseSpec() {
		return elseSpec;
	}

	public void setElseSpec(RuntimeDependencySpec elseSpec) {
		this.elseSpec = elseSpec;
	}

	public boolean hasElse() {
		return this.elseSpec != null;
	}

	public String getTarget() {
		return target;
	}

	public boolean isSilentChange() {
		return silentChange;
	}

	public boolean isBlockPropagation() {
		return blockPropagation;
	}


}

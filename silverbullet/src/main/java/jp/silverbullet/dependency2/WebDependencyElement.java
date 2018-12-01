package jp.silverbullet.dependency2;

import org.codehaus.jackson.annotate.JsonProperty;

public class WebDependencyElement {
	@JsonProperty(DependencySpec.Value)
	public String value;
	
	@JsonProperty(Expression.Trigger)
	public String trigger;
	
	@JsonProperty(Expression.Condition)
	public String condition;
}
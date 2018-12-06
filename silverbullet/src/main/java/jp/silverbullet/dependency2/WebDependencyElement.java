package jp.silverbullet.dependency2;

import org.codehaus.jackson.annotate.JsonProperty;

public class WebDependencyElement {
	@JsonProperty(DependencySpec.Value)
	public String value;
	
	@JsonProperty(DependencySpec.Trigger)
	public String trigger;
	
	@JsonProperty(DependencySpec.Condition)
	public String condition;
}
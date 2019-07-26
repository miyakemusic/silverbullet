package jp.silverbullet.dependency2;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebDependencyElement {
	@JsonProperty(DependencySpec.Value)
	public String value;
	
	@JsonProperty(DependencySpec.Trigger)
	public String trigger;
	
	@JsonProperty(DependencySpec.Condition)
	public String condition;

	@JsonProperty(DependencySpec.SilentChange)
	public String silentChange;
}
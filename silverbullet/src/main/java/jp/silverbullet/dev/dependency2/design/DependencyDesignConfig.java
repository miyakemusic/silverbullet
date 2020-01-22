package jp.silverbullet.dev.dependency2.design;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DependencyDesignConfig {

	public List<String> triggers = new ArrayList<>();
	public List<String> targets = new ArrayList<>();
	
	public DependencyDesignConfig() {}
	public DependencyDesignConfig(String[] triggers2, String[] targets2) {
		this.triggers = Arrays.asList(triggers2);
		this.targets = Arrays.asList(targets2);
	}
	
	public void update(String triggers2, String targets2) {
		this.triggers = Arrays.asList(triggers2.split(","));
		this.targets = Arrays.asList(targets2.split(","));
	}
	
}

package jp.silverbullet.dev.dependency2.design;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DependencyRelation {
	public String relation = "";
	public String condition = "";
	public boolean blockPropagation = false;
	
	public List<String> candidates = new ArrayList<>();
	
	public DependencyRelation() {
		
	}
	public DependencyRelation(String relation, String condition) {
		this.relation = relation;
		this.condition = condition;
	}
	public DependencyRelation(String relation) {
		this.relation = relation;
	}
	
	@JsonIgnore
	public boolean isEmpty() {
		return this.relation.isEmpty();
	}
	
	
}

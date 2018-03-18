package jp.silverbullet.spec;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class SpecElement {
	private List<SpecElement> children = new ArrayList<SpecElement>();
	private String name = "no name";
	private String description = "";
	
	enum Type {
		Node,
		Spec, Story, Task, Statement
	}
	private Type type = Type.Node;
	
	public void addStoryElement() {
		children.add(new SpecElement());
	}

	public List<SpecElement> getChildren() {
		return children;
	}

	public void setChildren(List<SpecElement> children) {
		this.children = children;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void addAll(SpecElement loadSpec) {
		this.children.addAll(loadSpec.getChildren());
	}
	
}

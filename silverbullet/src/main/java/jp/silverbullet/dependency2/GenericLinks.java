package jp.silverbullet.dependency2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GenericLinks {

	private List<GenericLink> links;
	private Set<String> loops;

	public GenericLinks(List<GenericLink> links) {
		this.links = links;
		this.loops = new HashSet<>();
	}
	
	public GenericLinks(List<GenericLink> links, Set<String> loops) {
		this.links = links;
		this.loops = loops;
	}

	public List<GenericLink> getLinks() {
		return this.links;
	}

	public Set<String> getLoops() {
		return loops;
	}


}

package jp.silverbullet.dependency2;

import java.util.ArrayList;
import java.util.List;

public class GenericLinks {

	private List<GenericLink> links;
	private boolean loop;

	public GenericLinks(List<GenericLink> links, boolean loop) {
		this.links = links;
		this.loop = loop;
	}

	public GenericLinks filter(String id) {
		List<GenericLink> ret = new ArrayList<>();
		for (GenericLink link : links) {
			if (link.containsId(id)) {
				ret.add(link);
			}
		}
		return new GenericLinks(ret, false);
	}

	public List<GenericLink> getLinks() {
		return this.links;
	}

	public boolean isLoop() {
		return loop;
	}

}

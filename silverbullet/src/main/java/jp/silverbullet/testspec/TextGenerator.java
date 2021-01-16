package jp.silverbullet.testspec;

import java.util.ArrayList;
import java.util.List;

public class TextGenerator {

	private List<String> recursive(String key, TsNode node) {
		List<String> ret = new ArrayList<>();
		
		for (String k : node.getSubNodes().keySet()) {
			TsNode subNode = node.getSubNodes().get(k);
						
			List<String> rr = recursive(k, subNode);
			if (rr.size() > 0) {
				for (String r : rr) {
					ret.add(key + "." + node.getId() + "." + r);
				}
			}
			else {
				ret.add(key + "." + node.getId() + "." + k + "." + subNode.getId());
			}
		}
		
		return ret;
	}

	public String getText(TsNode node) {
		return recursive("root", node).toString().replace(",", "\n").replace("[", "").replace("]", "").replace("\s", "");
	}
	
}

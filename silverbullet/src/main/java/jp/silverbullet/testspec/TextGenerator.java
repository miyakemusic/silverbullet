package jp.silverbullet.testspec;

import java.util.ArrayList;
import java.util.List;

public class TextGenerator {

	private List<String> recursive(String key, TsNode node) {
		List<String> ret = new ArrayList<>();
		
		for (String k : node.getOutputs().keySet()) {
			TsNode subNode = node.getOutputs().get(k).owner();
						
			List<String> rr = recursive(k, subNode);
			if (rr.size() > 0) {
				for (String r : rr) {
					ret.add(key + "." + node.getName() + "." + r);
				}
			}
			else {
				ret.add(key + "." + node.getName() + "." + k + "." + subNode.getName());
			}
		}
		
		return ret;
	}

	public String getText(TsNode node) {
		return recursive("root", node).toString().replace(",", "\n").replace("[", "").replace("]", "").replace("\s", "");
	}
	
}

package jp.silverbullet.web.ui.part2;

import java.util.Map;

public class LinkResolver {

	private Map<String, Pane> roots;

	public LinkResolver(Map<String, Pane> roots) {
		this.roots = roots;
	}

	public Pane resolve(Pane pane) {
		Pane ret = pane.clone();
		ret.widgets.clear();
		
		walkThrough(pane, ret);
//		for (Pane subPane : pane.widgets) {
//			if (subPane.optional.startsWith("$LINK")) {
//				String linkId = subPane.optional.split("=")[1];
//				Pane linkPane = findPane(linkId);
//				if (linkPane != null) {
//					subPane.addChild(linkPane);
//				}
//			}
//			
//			resolve(subPane);
//		}
		return ret;
	}

	private void walkThrough(Pane original, Pane clonPane) {
		
		if (original.optional.startsWith("$LINK")) {
			String linkId = original.optional.split("=")[1];
			Pane linkPane = findPane(linkId);
			if (linkPane != null) {
				clonPane.addChild(linkPane);
			}
		}
		
		for (Pane subPane : original.widgets) {
			Pane subClone = subPane.clone();
			
			subClone.widgets.clear();
			clonPane.addChild(subClone);
			walkThrough(subPane, subClone);
		}
	}
	
	private Pane findPane(String linkId) {
		for (Pane pane: this.roots.values()) {
			Pane targetPane = pane.findLink(linkId);
			if (targetPane != null) {
				//pane.addChild(targetPane);
				return targetPane;
			}
		}
		return null;
	}

}

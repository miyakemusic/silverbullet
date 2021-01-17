package jp.silverbullet.testspec;

import java.util.Arrays;

public class TsLine {

	public int x1;
	public int y1;
	public int x2;
	public int y2;

	public TsLine() {}
	
	public TsLine(TsPresentationNode parent, TsPresentationNode node) {
		int index = 0;
		for (String key : parent.node().getSubNodes().keySet()) {
			TsNode subNode = parent.node().getSubNodes().get(key);
			if (subNode.getId().equals(node.getId())) {
				
				break;
			}
			index++;
		}
//		int index = Arrays.asList(parent.getOutput()).indexOf(node.getId());
		x1 = parent.getLeft() + parent.getWidth();
		y1 = parent.getTop() + index * node.unitHeight + node.unitHeight / 2;
		x2 = node.getLeft();
		y2 = node.getTop() + node.unitHeight / 2;
	}

}

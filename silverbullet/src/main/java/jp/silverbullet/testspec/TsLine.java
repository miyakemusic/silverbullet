package jp.silverbullet.testspec;

import java.util.Arrays;

public class TsLine {

	public int x1;
	public int y1;
	public int x2;
	public int y2;

	public TsLine() {}
	
	public TsLine(TsPresentationPort port1, TsPresentationPort port2) {
//		int index = 0;
//		for (String key : parent.node().getOutputs().keySet()) {
//			TsPort subNode = parent.node().getOutputs().get(key);
//			if (subNode.owner().getId().equals(node.getId())) {
//				
//				break;
//			}
//			index++;
//		}
//		x1 = parent.getLeft() + parent.getWidth();
//		y1 = parent.getTop() + index * node.unitHeight + node.unitHeight / 2;
//		x2 = node.getLeft();
//		y2 = node.getTop() + node.unitHeight / 2;
		
		x1 = port1.getLeft();
		y1 = port1.getTop() + port1.getHeight() / 2;
		
		x2 = port2.getLeft() + port2.getWidth();
		y2 = port2.getTop() + port2.getHeight() / 2;
	}

}

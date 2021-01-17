package jp.silverbullet.testspec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TsPresentationNodes {

	private int width = 150;
	private int horizontal_gap = 50;
//	private int unitHeight = 20;

	private List<TsPresentationNode> allNodes = new ArrayList<>();
	private List<TsLine> allLines = new ArrayList<>();
	
	private Map<Integer, List<TsPresentationNode>> tmp = new HashMap<>();
	
	public TsPresentationNodes(NetworkConfiguration networkConfiguraton) {
		recursive(networkConfiguraton.getRootNode(), null, 0);
		
		Map<Integer, Integer> top = new HashMap<>();
		
		
		List<Integer> layers = new ArrayList<Integer>(tmp.keySet());
		Collections.sort(layers);
		
		//for (TsPresentationNode node : allNodes) {
		for (Integer layer : layers) {
			for (TsPresentationNode node : tmp.get(layer)) {
				if (!top.containsKey(node.getLeft())) {
					top.put(node.getLeft(), 0);
				}
				
				int topv = top.get(node.getLeft());
				if (topv < node.parentTop()) {
					topv = node.parentTop();
				}				
				node.setTop(topv);
				node.setWidth(width);
				
				top.put(node.getLeft(), topv + node.getHeight());
				System.out.println(node.getId() + ", top=" + node.getTop() + ", left=" + node.getLeft());
				
				allNodes.add(node);
				
				if (node.parent() != null) {
					TsLine line = new TsLine(node.parent(), node);
					allLines.add(line);
				}
			}
		}
	}

	
	public List<TsPresentationNode> getAllNodes() {
		return allNodes;
	}

	public void setAllNodes(List<TsPresentationNode> allNodes) {
		this.allNodes = allNodes;
	}

	public List<TsLine> getAllLines() {
		return allLines;
	}


	public void setAllLines(List<TsLine> allLines) {
		this.allLines = allLines;
	}


	private void recursive(TsNode node, TsPresentationNode parent, int layer) {
		Map<String, TsNode> subNodes = node.getSubNodes();
		TsPresentationNode presNode = new TsPresentationNode(node, parent,
				node.getSubNodes().keySet().toArray(new String[0]), layer * (width + horizontal_gap), node.allNodesCount());
		
		if (!tmp.containsKey(layer)) {
			tmp.put(layer, new ArrayList<>());
		}
		tmp.get(layer).add(presNode);
		//allNodes.add(presNode);
		
		for (String nodeId : subNodes.keySet()) {
			TsNode subNode = subNodes.get(nodeId);
			
			recursive(subNode, presNode, layer + 1);
		}
	}

}

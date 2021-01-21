package jp.silverbullet.testspec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TsPresentationNodes {

	private int width = 120;
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
				
				top.put(node.getLeft(), topv + node.getHeight() + 20);
//				System.out.println(node.getId() + ", top=" + node.getTop() + ", left=" + node.getLeft());
				
				allNodes.add(node);
				
				node.getInput().forEach(ip -> {
					TsPresentationPort pair = fintPresentationOutputPort(ip.tsPort().pairPort());
					System.out.println(ip.getId() + " <--> " + pair.getId());
					TsLine line = new TsLine(ip, pair);
					allLines.add(line);
				});
			}
		}
	}

	
	private TsPresentationPort fintPresentationOutputPort(TsPort pairPort) {
		for (TsPresentationNode node : allNodes) {
			for (TsPresentationPort pn : node.getOutput()) {
				if (pn.tsPort() == pairPort) {
					return pn;
				}
			}
		}
		return null;
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
		Map<String, TsPort> outputs = node.getOutputs();
		TsPresentationNode presNode = new TsPresentationNode(generateSerial("D"), node, parent,
				node.getInputs().keySet().toArray(new String[0]),
				node.getOutputs().keySet().toArray(new String[0]), 
				layer * (width + horizontal_gap));
		
		if (!tmp.containsKey(layer)) {
			tmp.put(layer, new ArrayList<>());
		}
		tmp.get(layer).add(presNode);
		//allNodes.add(presNode);
		
		Set<TsNode> nextNodes = new LinkedHashSet<>();
		for (String nodeId : outputs.keySet()) {
			TsPort output = outputs.get(nodeId);
			if (!output.terminated()) {
				nextNodes.add(output.pairPort().owner());
			}
			
		}
		for (TsNode n : nextNodes) {
			recursive(n, presNode, layer + 1);
		}
	}

	private int serialno = 0;
	private String generateSerial(String prefix) {
		return prefix + serialno++;
	}

}

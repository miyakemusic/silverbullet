package jp.silverbullet.dependency;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import jp.silverbullet.StaticInstances;


public class LinkGenerator {
	private DepChainPair[] link;

	public LinkGenerator(String id) {
		this.link = createDependencyLink(id);
	}

	private DepChainPair[] createDependencyLink(final String id) {
		DependencySpecHolder holder = StaticInstances.getInstance().getBuilderModel().getDependencySpecHolder();
		DependencyBuilder builder = new DependencyBuilder(id, holder);

		Set<DepChainPair> set = new LinkedHashSet<>();
		createLinkList(id, DependencyTargetElement.Value.toString(), builder.getTree(), set);
		
		DependencySpec spec = holder.getSpec(id);
		if (spec != null) {
			for (DependencyTargetElement e : spec.getDepExpHolderMap().keySet()) {
				DependencyExpressionHolderMap exHolderMap = spec.getDepExpHolderMap().get(e);
				for (String selectionId : exHolderMap.keySet()) {
					DependencyExpressionHolderList list = exHolderMap.getDependencyExpressionHolderMap().get(selectionId);
					for (DependencyExpressionHolder exHolder : list.getDependencyExpressionHolders()) {
						for (String value : exHolder.getExpressions().keySet()) {
							DependencyExpressionList exList = exHolder.getExpressions().get(value);
							for (DependencyExpression expression : exList.getDependencyExpressions()) {
								List<IdElement> depChains = expression.getIdElement();
								for (IdElement depChain : depChains) {
									DepChainPair pair = new DepChainPair(depChain, new IdElement(id, DependencyTargetConverter.convertToString(e, selectionId)));
									set.add(pair);
								}								
							}
						}
					}

				}

			}
		}
		return set.toArray(new DepChainPair[0]);
	}
	


	private void createLinkList(String fromId, String fromElement, DependencyNode node, Set<DepChainPair> set) {
		for (DependencyNode child : node.getChildren()) {
			String toId = child.getDependencyProperty().getId();
			addDepChain(fromId, fromElement, set, child, toId);
			String toElement = getElement(child);
			createLinkList(child.getDependencyProperty().getId(), toElement, child, set);
		}
	}

	private void addDepChain(String fromId, String fromElement, Set<DepChainPair> set, DependencyNode child,
			String toId) {
		
		DepChainPair depChain = new DepChainPair(new IdElement(fromId, fromElement), new IdElement(toId, getElement(child)));
		boolean found = false;
		for (DepChainPair p : set) {
			if (p.equals(depChain)) {
				found = true;
				break;
			}
		}
		if (!found) {
			set.add(depChain);
		}
	}

	private String getElement(DependencyNode child) {
		return DependencyTargetConverter.convertToString(child.getDependencyProperty().getElement(), child.getDependencyProperty().getSelectionId());
	}

	public DepChainPair[] getLink() {
		return this.link;
	}

	public Set<String> getRelatedIds() {
		Set<String> ret = new HashSet<>();
		for (DepChainPair pair : this.link) {
			ret.add(pair.from.id);
			ret.add(pair.to.id);
		}
		return ret;
	}
}


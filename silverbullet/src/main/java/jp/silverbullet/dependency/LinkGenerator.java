package jp.silverbullet.dependency;

import java.util.ArrayList;
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
				for (String value : spec.getDependencyExpressionHolder(e).getExpressions().keySet()) {
					DependencyExpressionList list = spec.getDependencyExpressionHolder(e).getExpressions().get(value);
					for (DependencyExpression expression : list.getDependencyExpressions()) {
						List<IdElement> depChains = expression.getIdElement();
						for (IdElement depChain : depChains) {
							DepChainPair pair = new DepChainPair(depChain, new IdElement(id, e.toString()));
							set.add(pair);
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
}


package jp.silverbullet.dependency2;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

import jp.silverbullet.core.dependency2.DependencyNode;
import jp.silverbullet.core.dependency2.DependencySpec;
import jp.silverbullet.core.dependency2.DependencySpecAnalyzer;
import jp.silverbullet.core.dependency2.DependencySpecHolder;
import jp.silverbullet.core.dependency2.GenericLink;
import jp.silverbullet.core.dependency2.LinkGenerator.LinkLevel;

public class DependencySpecAnalyzerTest {

	@Test
	public void test() {
		PropertyStoreForTest store = new PropertyStoreForTest();
		store.addListProperty("ID_ROOT", Arrays.asList("ID_ROOT_A", "ID_ROOT_B", "ID_ROOT_C"), "ID_ROOT_A");
		store.addListProperty("ID_MIDDLE", Arrays.asList("ID_MIDDLE_A", "ID_MIDDLE_B", "ID_MIDDLE_C"), "ID_MIDDLE_A");
		store.addListProperty("ID_LEAF", Arrays.asList("ID_LEAF_A", "ID_LEAF_B", "ID_LEAF_C"), "ID_LEAF_A");
		
		DependencySpec specMiddle = new DependencySpec("ID_MIDDLE");
		specMiddle.addOptionSelect("ID_MIDDLE_A", "$ID_ROOT==%ID_ROOT_A");
		specMiddle.addOptionSelect("ID_MIDDLE_B", "$ID_ROOT==%ID_ROOT_B");
		specMiddle.addOptionSelect("ID_MIDDLE_C", "$ID_ROOT==%ID_ROOT_C");
		
		DependencySpec specLeaf = new DependencySpec("ID_LEAF");
		specLeaf.addOptionEnabled("ID_LEAF_A", DependencySpec.True, "$ID_MIDDLE==%ID_MIDDLE_A");
		specLeaf.addOptionEnabled("ID_LEAF_A", DependencySpec.False, DependencySpec.Else);
		
		specLeaf.addOptionEnabled("ID_LEAF_B", DependencySpec.True, "$ID_MIDDLE==%ID_MIDDLE_B");
		specLeaf.addOptionEnabled("ID_LEAF_B", DependencySpec.False, DependencySpec.Else);
		
		specLeaf.addOptionEnabled("ID_LEAF_C", DependencySpec.True, "$ID_MIDDLE==%ID_MIDDLE_C");
		specLeaf.addOptionEnabled("ID_LEAF_C", DependencySpec.False, DependencySpec.Else);
		
		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(specMiddle);
		specHolder.addSpec(specLeaf);
		
		DependencySpecAnalyzer analyzer = new DependencySpecAnalyzer(specHolder);
		
		{
			DependencyNode nodeRoot = analyzer.getNode("ID_ROOT");
			
			List<DependencyNode> children = nodeRoot.getChildren();
			assertEquals(1, children.size());
			DependencyNode firstChild = children.get(0);
			assertEquals("ID_MIDDLE", firstChild.getId());
			assertEquals("ID_MIDDLE", nodeRoot.getChildLinks().get(0).getId());
			assertEquals(DependencySpec.Value, nodeRoot.getChildLinks().get(0).getTargetElement());

			List<DependencyNode> parents = nodeRoot.getParents();
			assertEquals(0, parents.size());
		}
		
		{
			DependencyNode nodeRoot = analyzer.getNode("ID_MIDDLE");
			
			List<DependencyNode> children = nodeRoot.getChildren();
			assertEquals(1, children.size());
			DependencyNode child = children.get(0);
			assertEquals(3, child.getParentLinks().size());
			assertEquals(DependencySpec.OptionEnable + "#" + "ID_LEAF_A", child.getParentLinks().get(0).getTargetElement());
			assertEquals(DependencySpec.OptionEnable + "#" + "ID_LEAF_C", child.getParentLinks().get(1).getTargetElement());
			assertEquals(DependencySpec.OptionEnable + "#" + "ID_LEAF_B", child.getParentLinks().get(2).getTargetElement());
			
			List<DependencyNode> parents = nodeRoot.getParents();
			assertEquals(1, parents.size());
			DependencyNode parent = parents.get(0);
			assertEquals(1, parent.getChildLinks().size());
			assertEquals(DependencySpec.Value, parent.getChildLinks().get(0).getTargetElement());
		}
		
		{
			DependencyNode nodeRoot = analyzer.getNode("ID_LEAF");
			
			List<DependencyNode> parents = nodeRoot.getParents();
			assertEquals(1, parents.size());
			DependencyNode parent = parents.get(0);
			assertEquals(3, parent.getChildLinks().size());
			assertEquals(DependencySpec.OptionEnable + "#" + "ID_LEAF_A", parent.getChildLinks().get(0).getTargetElement());
			assertEquals(DependencySpec.OptionEnable + "#" + "ID_LEAF_C", parent.getChildLinks().get(1).getTargetElement());
			assertEquals(DependencySpec.OptionEnable + "#" + "ID_LEAF_B", parent.getChildLinks().get(2).getTargetElement());
			
			List<GenericLink> links = analyzer.getLinkGenerator().generateLinks(LinkLevel.Detail).getLinks();
			for (GenericLink link : links) {
				System.out.println(link.getFrom() + " " + link.getTo() + " " + link.getType());
			}
		}
	}

	
}

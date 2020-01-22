package jp.silverbullet.obsolute;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import jp.silverbullet.core.dependency2.CachedPropertyStore;
import jp.silverbullet.core.dependency2.DependencyEngine;
import jp.silverbullet.core.dependency2.DependencySpec;
import jp.silverbullet.core.dependency2.DependencySpecHolder;
import jp.silverbullet.core.dependency2.Expression;
import jp.silverbullet.core.dependency2.RequestRejectedException;
import jp.silverbullet.dependency2.PropertyStoreForTest;
import jp.silverbullet.web.trash.DependencySpecRebuilder;

public class TestAlternativeDependency {

	@Test
	public void test() {
		PropertyStoreForTest store = new PropertyStoreForTest();
		store.addListProperty("ID_ROOT", Arrays.asList("ID_ROOT_A", "ID_ROOT_B", "ID_ROOT_C"), "ID_ROOT_A");
		store.addListProperty("ID_MIDDLE", Arrays.asList("ID_MIDDLE_A", "ID_MIDDLE_B", "ID_MIDDLE_C"), "ID_MIDDLE_A");
		store.addListProperty("ID_LEAF", Arrays.asList("ID_LEAF_A", "ID_LEAF_B", "ID_LEAF_C"), "ID_LEAF_A");
		
		DependencySpec specMiddle = new DependencySpec("ID_MIDDLE");
		specMiddle.addOptionEnabled("ID_MIDDLE_A", DependencySpec.True, "$ID_ROOT==%ID_ROOT_A");
		specMiddle.addOptionEnabled("ID_MIDDLE_A", DependencySpec.False, DependencySpec.Else);

		specMiddle.addOptionEnabled("ID_MIDDLE_B", DependencySpec.True, "$ID_ROOT==%ID_ROOT_B");
		specMiddle.addOptionEnabled("ID_MIDDLE_B", DependencySpec.False, DependencySpec.Else);

		specMiddle.addOptionEnabled("ID_MIDDLE_C", DependencySpec.True, "$ID_ROOT==%ID_ROOT_C");
		specMiddle.addOptionEnabled("ID_MIDDLE_C", DependencySpec.False, DependencySpec.Else);

		
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
		
		DependencySpecRebuilder alternative = new DependencySpecRebuilder(specHolder, store);
		DependencySpecHolder newHolder = alternative.getNewHolder();
		
		DependencySpec newRootSpec = newHolder.getSpec("ID_ROOT");
		List<Expression> newRootExpressions = newRootSpec.getExpression(DependencySpec.Value);
		int index = 0;
		assertEquals("ID_ROOT_C", newRootExpressions.get(index).getValue());
		assertEquals("$ID_MIDDLE==%ID_MIDDLE_C", newRootExpressions.get(index).getTrigger());

		index++;
		assertEquals("ID_ROOT_B", newRootExpressions.get(index).getValue());
		assertEquals("$ID_MIDDLE==%ID_MIDDLE_B", newRootExpressions.get(index).getTrigger());

		index++;
		assertEquals("ID_ROOT_A", newRootExpressions.get(index).getValue());
		assertEquals("$ID_MIDDLE==%ID_MIDDLE_A", newRootExpressions.get(index).getTrigger());
		
		index = 0;
		DependencySpec newMiddleSpec = newHolder.getSpec("ID_MIDDLE");
		List<Expression> newMiddleExpressions = newMiddleSpec.getExpression(DependencySpec.Value);
		assertEquals("ID_MIDDLE_C", newMiddleExpressions.get(index).getValue());
		assertEquals("$ID_ROOT==%ID_ROOT_C", newMiddleExpressions.get(index).getTrigger());	
		
		index++;
		assertEquals("ID_MIDDLE_B", newMiddleExpressions.get(index).getValue());
		assertEquals("$ID_ROOT==%ID_ROOT_B", newMiddleExpressions.get(index).getTrigger());	
		
		index++;
		assertEquals("ID_MIDDLE_A", newMiddleExpressions.get(index).getValue());
		assertEquals("$ID_ROOT==%ID_ROOT_A", newMiddleExpressions.get(index).getTrigger());	
		
		index++;
		assertEquals("ID_MIDDLE_A", newMiddleExpressions.get(index).getValue());
		assertEquals("$ID_LEAF==%ID_LEAF_A", newMiddleExpressions.get(index).getTrigger());
		
		index++;
		assertEquals("ID_MIDDLE_C", newMiddleExpressions.get(index).getValue());
		assertEquals("$ID_LEAF==%ID_LEAF_C", newMiddleExpressions.get(index).getTrigger());	
		
		index++;
		assertEquals("ID_MIDDLE_B", newMiddleExpressions.get(index).getValue());
		assertEquals("$ID_LEAF==%ID_LEAF_B", newMiddleExpressions.get(index).getTrigger());
		
		index = 0;
		DependencySpec newLeafSpec = newHolder.getSpec("ID_LEAF");
		List<Expression> newLeafExpressions = newLeafSpec.getExpression(DependencySpec.Value);
		assertEquals("ID_LEAF_A", newLeafExpressions.get(index).getValue());
		assertEquals("$ID_MIDDLE==%ID_MIDDLE_A", newLeafExpressions.get(index).getTrigger());	
		
		index++;
		assertEquals("ID_LEAF_C", newLeafExpressions.get(index).getValue());
		assertEquals("$ID_MIDDLE==%ID_MIDDLE_C", newLeafExpressions.get(index).getTrigger());	
		
		index++;
		assertEquals("ID_LEAF_B", newLeafExpressions.get(index).getValue());
		assertEquals("$ID_MIDDLE==%ID_MIDDLE_B", newLeafExpressions.get(index).getTrigger());
	}

	@Test
	public void test2() {
		PropertyStoreForTest store = new PropertyStoreForTest();
		store.addListProperty("ID_ROOT", Arrays.asList("ID_ROOT_A", "ID_ROOT_B", "ID_ROOT_C", "ID_ROOT_C2"), "ID_ROOT_A");
		store.addListProperty("ID_MIDDLE", Arrays.asList("ID_MIDDLE_A", "ID_MIDDLE_B", "ID_MIDDLE_C", "ID_MIDDLE_C2"), "ID_MIDDLE_A");
		store.addListProperty("ID_LEAF", Arrays.asList("ID_LEAF_A", "ID_LEAF_B", "ID_LEAF_C"), "ID_LEAF_A");
		
		DependencySpec specMiddle = new DependencySpec("ID_MIDDLE");
		specMiddle.addOptionEnabled("ID_MIDDLE_A", DependencySpec.True, "$ID_ROOT==%ID_ROOT_A");
		specMiddle.addOptionEnabled("ID_MIDDLE_A", DependencySpec.False, DependencySpec.Else);

		specMiddle.addOptionEnabled("ID_MIDDLE_B", DependencySpec.True, "$ID_ROOT==%ID_ROOT_B");
		specMiddle.addOptionEnabled("ID_MIDDLE_B", DependencySpec.False, DependencySpec.Else);

		specMiddle.addOptionEnabled("ID_MIDDLE_C", DependencySpec.True, "$ID_ROOT==%ID_ROOT_C");
		specMiddle.addOptionEnabled("ID_MIDDLE_C", DependencySpec.False, DependencySpec.Else);

		specMiddle.addOptionEnabled("ID_MIDDLE_C", DependencySpec.True, "$ID_ROOT==%ID_ROOT_C2");
		specMiddle.addOptionEnabled("ID_MIDDLE_C", DependencySpec.False, DependencySpec.Else);
		
		DependencySpec specLeaf = new DependencySpec("ID_LEAF");
		specLeaf.addOptionEnabled("ID_LEAF_A", DependencySpec.True, "$ID_MIDDLE==%ID_MIDDLE_A");
		specLeaf.addOptionEnabled("ID_LEAF_A", DependencySpec.False, DependencySpec.Else);
		
		specLeaf.addOptionEnabled("ID_LEAF_B", DependencySpec.True, "$ID_MIDDLE==%ID_MIDDLE_B");
		specLeaf.addOptionEnabled("ID_LEAF_B", DependencySpec.False, DependencySpec.Else);
		
		specLeaf.addOptionEnabled("ID_LEAF_C", DependencySpec.True, "$ID_MIDDLE==%ID_MIDDLE_C");
		specLeaf.addOptionEnabled("ID_LEAF_C", DependencySpec.False, DependencySpec.Else);
		
		specLeaf.addOptionEnabled("ID_LEAF_C", DependencySpec.True, "$ID_MIDDLE==%ID_MIDDLE_C2");
		specLeaf.addOptionEnabled("ID_LEAF_C", DependencySpec.False, DependencySpec.Else);
		
		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(specMiddle);
		specHolder.addSpec(specLeaf);
		
		DependencySpecRebuilder alternative = new DependencySpecRebuilder(specHolder, store);
		DependencySpecHolder newHolder = alternative.getNewHolder();
		
		List<Expression> newMid = newHolder.getSpec("ID_MIDDLE").getExpression(DependencySpec.Value);
		Expression exp1 = newMid.get(5);
		assertEquals("ID_MIDDLE_C", exp1.getValue());
		assertEquals("$ID_LEAF==%ID_LEAF_C", exp1.getTrigger());
		assertEquals("($ID_MIDDLE!=%ID_MIDDLE_C2)", exp1.getCondition());
		
		Expression exp2 = newMid.get(6);
		
		DependencyEngine engine = new DependencyEngine(store) {
			@Override
			protected DependencySpecHolder getSpecHolder() {
				return newHolder;
			}
		};
		try {
			store.getProperty("ID_MIDDLE").setCurrentValue("ID_MIDDLE_C");
			engine.requestChange("ID_LEAF", "ID_LEAF_C");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			
			assertEquals("ID_MIDDLE_C", cached.getProperty("ID_MIDDLE").getCurrentValue());		
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}
}

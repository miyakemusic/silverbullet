package jp.silverbullet.dependency2.design;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jp.silverbullet.dependency2.DependencySpec;
import jp.silverbullet.dependency2.DependencySpecHolder;
import jp.silverbullet.dependency2.Expression;
import jp.silverbullet.dependency2.PropertyStoreForTest;
import jp.silverbullet.dependency2.design.RestrictionMatrix.AxisType;
import jp.silverbullet.property2.PropertyDef2;
import jp.silverbullet.property2.PropertyFactory;
import jp.silverbullet.property2.PropertyHolder2;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.property2.RuntimePropertyStore;

public class RestrictionMatrixTest {

	@Test
	public void test() throws Exception {
		PropertyHolder2 holder = new PropertyHolder2();
		PropertyFactory factory = new PropertyFactory();
		RuntimePropertyStore store = new RuntimePropertyStore(holder);

		holder.addProperty(factory.createNumeric("ID_TRIGGER").option("ID_TRIGGER_A", "A", "").option("ID_TRIGGER_B", "B", "")
				.option("ID_TRIGGER_C", "C", "").option("ID_TRIGGER_D", "D", ""));
		holder.addProperty(factory.createNumeric("ID_TARGET").option("ID_TARGET_A", "A", "").option("ID_TARGET_B", "B", "")
				.option("ID_TARGET_C", "C", "").option("ID_TARGET_D", "D", ""));

		DependencySpecHolder depSpecHolder = new DependencySpecHolder();
		
		RestrictionMatrix matrix = new RestrictionMatrix() {
			@Override
			protected DependencySpecHolder getDependencySpecHolder() {
				return depSpecHolder;
			}

			@Override
			protected void resetMask() {

			}

			@Override
			protected RuntimeProperty getRuntimeProperty(String id, int index) {
				return store.get(id, index);
			}

			@Override
			protected RuntimeProperty getRuntimeProperty(String id) {
				return store.get(id);
			}

			@Override
			protected PropertyDef2 getPropertyDef(String id) {
				return holder.get(id);
			}

			@Override
			protected List<PropertyDef2> getAllPropertieDefs() {
				return new ArrayList<PropertyDef2>(holder.getProperties());
			}
			
		};
		
		matrix.add("ID_TRIGGER", AxisType.COLUMN);
		matrix.add("ID_TARGET", AxisType.ROW);

		matrix.updateEnabled(0, 0, true);
		matrix.updateEnabled(1, 0, true);
		matrix.updateEnabled(1, 1, true);
		matrix.updateEnabled(2, 1, true);
		matrix.updateEnabled(2, 2, true);
		matrix.updateEnabled(3, 2, true);
		matrix.updateEnabled(3, 3, true);
		
		// In case, Trigger is strong
		matrix.setPriority("ID_TRIGGER", 10); 
		matrix.setPriority("ID_TARGET", 0);
		matrix.build();
	
		DependencySpec spec = depSpecHolder.getSpec("ID_TARGET");
		assertEquals(2, spec.getExpression("ID_TARGET_A").size());
		assertEquals("$ID_TRIGGER==%ID_TRIGGER_A", spec.getExpression("ID_TARGET_A").get(0).getTrigger());
		assertEquals("", spec.getExpression("ID_TARGET_A").get(0).getCondition());
		assertEquals(DependencySpec.Else, spec.getExpression("ID_TARGET_A").get(1).getTrigger());
		
		assertEquals(3, spec.getExpression("ID_TARGET_B").size());
		assertEquals("$ID_TRIGGER==%ID_TRIGGER_A", spec.getExpression("ID_TARGET_B").get(0).getTrigger());
		assertEquals("", spec.getExpression("ID_TARGET_B").get(0).getCondition());
		assertEquals("$ID_TRIGGER==%ID_TRIGGER_B", spec.getExpression("ID_TARGET_B").get(1).getTrigger());
		assertEquals("", spec.getExpression("ID_TARGET_B").get(1).getCondition());
		assertEquals(DependencySpec.Else, spec.getExpression("ID_TARGET_B").get(2).getTrigger());
		
		assertEquals(3, spec.getExpression("ID_TARGET_C").size());
		assertEquals("$ID_TRIGGER==%ID_TRIGGER_B", spec.getExpression("ID_TARGET_C").get(0).getTrigger());
		assertEquals("", spec.getExpression("ID_TARGET_C").get(0).getCondition());
		assertEquals("$ID_TRIGGER==%ID_TRIGGER_C", spec.getExpression("ID_TARGET_C").get(1).getTrigger());
		assertEquals("", spec.getExpression("ID_TARGET_C").get(1).getCondition());
		assertEquals(DependencySpec.Else, spec.getExpression("ID_TARGET_C").get(2).getTrigger());
		
		assertEquals(3, spec.getExpression("ID_TARGET_D").size());
		assertEquals("$ID_TRIGGER==%ID_TRIGGER_C", spec.getExpression("ID_TARGET_D").get(0).getTrigger());
		assertEquals("", spec.getExpression("ID_TARGET_D").get(0).getCondition());
		assertEquals("$ID_TRIGGER==%ID_TRIGGER_D", spec.getExpression("ID_TARGET_D").get(1).getTrigger());
		assertEquals("", spec.getExpression("ID_TARGET_D").get(1).getCondition());
		assertEquals(DependencySpec.Else, spec.getExpression("ID_TARGET_D").get(2).getTrigger());
		
		assertEquals(0, depSpecHolder.getSpec("ID_TRIGGER").getDependencySpecDetail().getExpressions().getExpressions().size());
		
		// In case, Target is strong
		matrix.setPriority("ID_TRIGGER", 10); 
		matrix.setPriority("ID_TARGET", 20);
		matrix.build();
		
		spec = depSpecHolder.getSpec("ID_TRIGGER");
		assertEquals(3, spec.getExpression("ID_TRIGGER_A").size());
		assertEquals("$ID_TARGET==%ID_TARGET_A", spec.getExpression("ID_TRIGGER_A").get(0).getTrigger());
		assertEquals("", spec.getExpression("ID_TRIGGER_A").get(0).getCondition());
		assertEquals("$ID_TARGET==%ID_TARGET_B", spec.getExpression("ID_TRIGGER_A").get(1).getTrigger());
		assertEquals("", spec.getExpression("ID_TRIGGER_A").get(1).getCondition());
		assertEquals(DependencySpec.Else, spec.getExpression("ID_TRIGGER_A").get(2).getTrigger());
		
		assertEquals(3, spec.getExpression("ID_TRIGGER_B").size());
		assertEquals("$ID_TARGET==%ID_TARGET_B", spec.getExpression("ID_TRIGGER_B").get(0).getTrigger());
		assertEquals("", spec.getExpression("ID_TRIGGER_B").get(0).getCondition());
		assertEquals("$ID_TARGET==%ID_TARGET_C", spec.getExpression("ID_TRIGGER_B").get(1).getTrigger());
		assertEquals("", spec.getExpression("ID_TRIGGER_B").get(1).getCondition());
		assertEquals(DependencySpec.Else, spec.getExpression("ID_TRIGGER_B").get(2).getTrigger());
		
		assertEquals(3, spec.getExpression("ID_TRIGGER_C").size());
		assertEquals("$ID_TARGET==%ID_TARGET_C", spec.getExpression("ID_TRIGGER_C").get(0).getTrigger());
		assertEquals("", spec.getExpression("ID_TRIGGER_C").get(0).getCondition());
		assertEquals("$ID_TARGET==%ID_TARGET_D", spec.getExpression("ID_TRIGGER_C").get(1).getTrigger());
		assertEquals("", spec.getExpression("ID_TRIGGER_C").get(1).getCondition());
		assertEquals(DependencySpec.Else, spec.getExpression("ID_TRIGGER_C").get(2).getTrigger());
		
		assertEquals(2, spec.getExpression("ID_TRIGGER_D").size());
		assertEquals("$ID_TARGET==%ID_TARGET_D", spec.getExpression("ID_TRIGGER_D").get(0).getTrigger());
		assertEquals("", spec.getExpression("ID_TRIGGER_D").get(0).getCondition());
		assertEquals(DependencySpec.Else, spec.getExpression("ID_TRIGGER_D").get(1).getTrigger());
		
		assertEquals(0, depSpecHolder.getSpec("ID_TARGET").getDependencySpecDetail().getExpressions().getExpressions().size());
	
		
		// In case, Target and Trigger are the same
		matrix.setPriority("ID_TRIGGER", 10); 
		matrix.setPriority("ID_TARGET", 10);
		matrix.build();
		
		spec = depSpecHolder.getSpec("ID_TRIGGER");
		assertEquals(7, spec.getExpression(DependencySpec.Value).size());
		{
			Expression exp = spec.getExpression(DependencySpec.Value).get(0);
			assertEquals("ID_TRIGGER_A", exp.getValue());
			assertEquals("$ID_TARGET==%ID_TARGET_B", exp.getTrigger());
			assertEquals("($ID_TRIGGER!=%ID_TRIGGER_B)", exp.getCondition());
		}
		{
			Expression exp = spec.getExpression(DependencySpec.Value).get(1);
			assertEquals("ID_TRIGGER_B", exp.getValue());
			assertEquals("$ID_TARGET==%ID_TARGET_B", exp.getTrigger());
			assertEquals("($ID_TRIGGER!=%ID_TRIGGER_A)", exp.getCondition());
		}
		{
			Expression exp = spec.getExpression(DependencySpec.Value).get(2);
			assertEquals("ID_TRIGGER_A", exp.getValue());
			assertEquals("$ID_TARGET==%ID_TARGET_A", exp.getTrigger());
			assertEquals("", exp.getCondition());
		}
		{
			Expression exp = spec.getExpression(DependencySpec.Value).get(3);
			assertEquals("ID_TRIGGER_C", exp.getValue());
			assertEquals("$ID_TARGET==%ID_TARGET_D", exp.getTrigger());
			assertEquals("($ID_TRIGGER!=%ID_TRIGGER_D)", exp.getCondition());
		}
		{
			Expression exp = spec.getExpression(DependencySpec.Value).get(4);
			assertEquals("ID_TRIGGER_D", exp.getValue());
			assertEquals("$ID_TARGET==%ID_TARGET_D", exp.getTrigger());
			assertEquals("($ID_TRIGGER!=%ID_TRIGGER_C)", exp.getCondition());
		}
		{
			Expression exp = spec.getExpression(DependencySpec.Value).get(5);
			assertEquals("ID_TRIGGER_B", exp.getValue());
			assertEquals("$ID_TARGET==%ID_TARGET_C", exp.getTrigger());
			assertEquals("($ID_TRIGGER!=%ID_TRIGGER_C)", exp.getCondition());
		}
		{
			Expression exp = spec.getExpression(DependencySpec.Value).get(6);
			assertEquals("ID_TRIGGER_C", exp.getValue());
			assertEquals("$ID_TARGET==%ID_TARGET_C", exp.getTrigger());
			assertEquals("($ID_TRIGGER!=%ID_TRIGGER_B)", exp.getCondition());
		}
	}

}

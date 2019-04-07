package jp.silverbullet.dependency2.design;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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

		holder.addProperty(factory.createList("ID_TRIGGER").option("ID_TRIGGER_A", "A", "").option("ID_TRIGGER_B", "B", "")
				.option("ID_TRIGGER_C", "C", "").option("ID_TRIGGER_D", "D", ""));
		holder.addProperty(factory.createList("ID_TARGET").option("ID_TARGET_A", "A", "").option("ID_TARGET_B", "B", "")
				.option("ID_TARGET_C", "C", "").option("ID_TARGET_D", "D", ""));
		holder.addProperty(factory.createList("ID_DUMMY").option("ID_DUMMY_A", "A", "").option("ID_DUMMY_B", "B", ""));
		holder.addProperty(factory.createList("ID_DUMMY2").option("ID_DUMMY2_A", "A", "").option("ID_DUMMY2_B", "B", ""));
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
		
		matrix.add("ID_TRIGGER", AxisType.X);
		matrix.add("ID_DUMMY", AxisType.X);
		matrix.add("ID_DUMMY2", AxisType.X);
		matrix.add("ID_TARGET", AxisType.Y);
		matrix.add("ID_DUMMY", AxisType.Y);
		matrix.add("ID_DUMMY2", AxisType.Y);
		/*
		|       |Trigger A|Trigger B|Trigger C|Trigger D|
		Target A|    x    |         |         |         |
		Target B|    x    |    x    |         |         |
		Target C|         |    x    |    x    |         |
		Target D|         |         |    x    |    x    |
		 */
		matrix.updateEnabled(matrix.yTitle.indexOf("ID_TARGET_A"), matrix.xTitle.indexOf("ID_TRIGGER_A"), true);
		matrix.updateEnabled(matrix.yTitle.indexOf("ID_TARGET_B"), matrix.xTitle.indexOf("ID_TRIGGER_A"), true);
		matrix.updateEnabled(matrix.yTitle.indexOf("ID_TARGET_B"), matrix.xTitle.indexOf("ID_TRIGGER_B"), true);
		matrix.updateEnabled(matrix.yTitle.indexOf("ID_TARGET_C"), matrix.xTitle.indexOf("ID_TRIGGER_B"), true);
		matrix.updateEnabled(matrix.yTitle.indexOf("ID_TARGET_C"), matrix.xTitle.indexOf("ID_TRIGGER_C"), true);
		matrix.updateEnabled(matrix.yTitle.indexOf("ID_TARGET_D"), matrix.xTitle.indexOf("ID_TRIGGER_C"), true);
		matrix.updateEnabled(matrix.yTitle.indexOf("ID_TARGET_D"), matrix.xTitle.indexOf("ID_TRIGGER_D"), true);
		
		matrix.updateEnabled(matrix.yTitle.indexOf("ID_DUMMY_A"), matrix.xTitle.indexOf("ID_DUMMY2_A"), true);
		
		
		// In case, TRIGGER is stronger than TARGET
		matrix.setPriority("ID_TRIGGER", 10); 
		matrix.setPriority("ID_TARGET", 0);
		matrix.build();
	
		DependencySpec spec = depSpecHolder.getSpec("ID_TARGET");
		assertEquals(0, depSpecHolder.getSpec("ID_TRIGGER").getDependencySpecDetail().getExpressions().getExpressions().size());
//		assertEquals(0, spec.getExpression(DependencySpec.Enable).size());
		{
			assertEquals(2, spec.getExpression("ID_TARGET_A").size());
			List<Expression> expected = Arrays.asList(
					new Expression(DependencySpec.True, "$ID_TRIGGER==%ID_TRIGGER_A", ""),
					new Expression(DependencySpec.False, DependencySpec.Else, ""));
	
			testSpec(spec, expected, "ID_TARGET_A");
		}
		
		
		{				
			assertEquals(3, spec.getExpression("ID_TARGET_B").size());
			List<Expression> expected = Arrays.asList(
					new Expression(DependencySpec.True, "$ID_TRIGGER==%ID_TRIGGER_A", ""),
					new Expression(DependencySpec.True, "$ID_TRIGGER==%ID_TRIGGER_B", ""),
					new Expression(DependencySpec.False, DependencySpec.Else, ""));
	
			testSpec(spec, expected, "ID_TARGET_B");
		}
			
		
		{
			assertEquals(3, spec.getExpression("ID_TARGET_C").size());
			List<Expression> expected = Arrays.asList(
					new Expression(DependencySpec.True, "$ID_TRIGGER==%ID_TRIGGER_B", ""),
					new Expression(DependencySpec.True, "$ID_TRIGGER==%ID_TRIGGER_C", ""),
					new Expression(DependencySpec.False, DependencySpec.Else, ""));
	
			testSpec(spec, expected, "ID_TARGET_C");
		}
		
		{
			assertEquals(3, spec.getExpression("ID_TARGET_D").size());
			List<Expression> expected = Arrays.asList(
					new Expression(DependencySpec.True, "$ID_TRIGGER==%ID_TRIGGER_C", ""),
					new Expression(DependencySpec.True, "$ID_TRIGGER==%ID_TRIGGER_D", ""),
					new Expression(DependencySpec.False, DependencySpec.Else, ""));
	
			testSpec(spec, expected, "ID_TARGET_D");			
		}
		
		assertEquals(0, depSpecHolder.getSpec("ID_TRIGGER").getDependencySpecDetail().getExpressions().getExpressions().size());
		
		// In case, Target is strong
		matrix.setPriority("ID_TRIGGER", 10); 
		matrix.setPriority("ID_TARGET", 20);
		matrix.build();
		
		assertEquals(0, depSpecHolder.getSpec("ID_TARGET").getDependencySpecDetail().getExpressions().getExpressions().size());
		spec = depSpecHolder.getSpec("ID_TRIGGER");
		{
			assertEquals(3, spec.getExpression("ID_TRIGGER_A").size());
			List<Expression> expected = Arrays.asList(
					new Expression(DependencySpec.True, "$ID_TARGET==%ID_TARGET_A", ""),
					new Expression(DependencySpec.True, "$ID_TARGET==%ID_TARGET_B", ""),
					new Expression(DependencySpec.False, DependencySpec.Else, ""));
			testSpec(spec, expected, "ID_TRIGGER_A");
		}
		{
			assertEquals(3, spec.getExpression("ID_TRIGGER_B").size());
			List<Expression> expected = Arrays.asList(
					new Expression(DependencySpec.True, "$ID_TARGET==%ID_TARGET_B", ""),
					new Expression(DependencySpec.True, "$ID_TARGET==%ID_TARGET_C", ""),
					new Expression(DependencySpec.False, DependencySpec.Else, ""));
			testSpec(spec, expected, "ID_TRIGGER_B");
		}
		{
			assertEquals(3, spec.getExpression("ID_TRIGGER_C").size());
			List<Expression> expected = Arrays.asList(
					new Expression(DependencySpec.True, "$ID_TARGET==%ID_TARGET_C", ""),
					new Expression(DependencySpec.True, "$ID_TARGET==%ID_TARGET_D", ""),
					new Expression(DependencySpec.False, DependencySpec.Else, ""));
			testSpec(spec, expected, "ID_TRIGGER_C");			
		}
		{
			assertEquals(2, spec.getExpression("ID_TRIGGER_D").size());
			List<Expression> expected = Arrays.asList(
					new Expression(DependencySpec.True, "$ID_TARGET==%ID_TARGET_D", ""),
					new Expression(DependencySpec.False, DependencySpec.Else, ""));
			testSpec(spec, expected, "ID_TRIGGER_D");				
		}
		
		assertEquals(0, depSpecHolder.getSpec("ID_TARGET").getDependencySpecDetail().getExpressions().getTriggerIds().size());
		
		// In case, Target and Trigger are the same
		matrix.setPriority("ID_TRIGGER", 10); 
		matrix.setPriority("ID_TARGET", 10);
		matrix.build();
		
		spec = depSpecHolder.getSpec("ID_TRIGGER");
		
		{
			assertEquals(7, spec.getExpression(DependencySpec.Value).size());		
			assertEquals(0, spec.getExpression(DependencySpec.Enable).size());	
			assertEquals(0, spec.getExpression(DependencySpec.OptionEnable).size());		
			List<Expression> expected = Arrays.asList(
					new Expression("ID_TRIGGER_A", "$ID_TARGET==%ID_TARGET_A", ""),
					new Expression("ID_TRIGGER_A", "$ID_TARGET==%ID_TARGET_B", "($ID_TRIGGER!=%ID_TRIGGER_B)"),
					new Expression("ID_TRIGGER_B", "$ID_TARGET==%ID_TARGET_B", "($ID_TRIGGER!=%ID_TRIGGER_A)"),
					new Expression("ID_TRIGGER_B", "$ID_TARGET==%ID_TARGET_C", "($ID_TRIGGER!=%ID_TRIGGER_C)"),
					new Expression("ID_TRIGGER_C", "$ID_TARGET==%ID_TARGET_D", "($ID_TRIGGER!=%ID_TRIGGER_D)"),
					new Expression("ID_TRIGGER_C", "$ID_TARGET==%ID_TARGET_C", "($ID_TRIGGER!=%ID_TRIGGER_B)"),
					new Expression("ID_TRIGGER_D", "$ID_TARGET==%ID_TARGET_D", "($ID_TRIGGER!=%ID_TRIGGER_C)")
					);
//			testSpec(spec, expected, DependencySpec.Value);
		}
		
		// Trigger is strong. Mode and Trigger affect Target
		holder.addProperty(factory.createNumeric("ID_MODE").option("ID_MODE_A", "A", "").option("ID_MODE_B", "B", ""));
		matrix.add("ID_MODE", AxisType.X);
		matrix.setPriority("ID_TRIGGER", 10); 
		matrix.setPriority("ID_TARGET", 0);
		matrix.setPriority("ID_MODE", 100);
		

		/*
		|       |Trigger A|Trigger B|Trigger C|Trigger D| Mode A | Mode B |
		Target A|    x    |         |         |         |    x   |    x   |
		Target B|    x    |    x    |         |         |    x   |    x   |
		Target C|         |    x    |    x    |         |    x   |    x   |
		Target D|         |         |    x    |    x    |        |    x   |
		 */
		matrix.updateEnabled(matrix.yTitle.indexOf("ID_TARGET_A"), matrix.xTitle.indexOf("ID_MODE_A"), true);
		matrix.updateEnabled(matrix.yTitle.indexOf("ID_TARGET_B"), matrix.xTitle.indexOf("ID_MODE_A"), true);
		matrix.updateEnabled(matrix.yTitle.indexOf("ID_TARGET_C"), matrix.xTitle.indexOf("ID_MODE_A"), true);
		
		matrix.updateEnabled(matrix.yTitle.indexOf("ID_TARGET_A"), matrix.xTitle.indexOf("ID_MODE_B"), true);
		matrix.updateEnabled(matrix.yTitle.indexOf("ID_TARGET_B"), matrix.xTitle.indexOf("ID_MODE_B"), true);
		matrix.updateEnabled(matrix.yTitle.indexOf("ID_TARGET_C"), matrix.xTitle.indexOf("ID_MODE_B"), true);
		matrix.updateEnabled(matrix.yTitle.indexOf("ID_TARGET_D"), matrix.xTitle.indexOf("ID_MODE_B"), true);
		matrix.build();		
		
		spec = depSpecHolder.getSpec("ID_TARGET");
		{
			assertEquals(4, spec.getExpression("ID_TARGET_A").size());
			List<Expression> expected = Arrays.asList(
					new Expression(DependencySpec.True, "$ID_TRIGGER==%ID_TRIGGER_A", "($ID_MODE==%ID_MODE_A)||($ID_MODE==%ID_MODE_B)"),
					new Expression(DependencySpec.True, "$ID_MODE==%ID_MODE_A", ""),
					new Expression(DependencySpec.True, "$ID_MODE==%ID_MODE_B", ""),
					new Expression(DependencySpec.False, DependencySpec.Else, "")

					);
			testSpec(spec, expected, "ID_TARGET_A");
		}
		{
			assertEquals(5, spec.getExpression("ID_TARGET_B").size());
			List<Expression> expected = Arrays.asList(
					new Expression(DependencySpec.True, "$ID_TRIGGER==%ID_TRIGGER_A", "($ID_MODE==%ID_MODE_A)||($ID_MODE==%ID_MODE_B)"),
					new Expression(DependencySpec.True, "$ID_TRIGGER==%ID_TRIGGER_B", "($ID_MODE==%ID_MODE_A)||($ID_MODE==%ID_MODE_B)"),
					new Expression(DependencySpec.True, "$ID_MODE==%ID_MODE_A", ""),
					new Expression(DependencySpec.True, "$ID_MODE==%ID_MODE_B", ""),
					new Expression(DependencySpec.False, DependencySpec.Else, "")
					);
			testSpec(spec, expected, "ID_TARGET_B");
		}	
		{
			assertEquals(5, spec.getExpression("ID_TARGET_C").size());
			List<Expression> expected = Arrays.asList(
					new Expression(DependencySpec.True, "$ID_TRIGGER==%ID_TRIGGER_B", "($ID_MODE==%ID_MODE_A)||($ID_MODE==%ID_MODE_B)"),
					new Expression(DependencySpec.True, "$ID_TRIGGER==%ID_TRIGGER_C", "($ID_MODE==%ID_MODE_A)||($ID_MODE==%ID_MODE_B)"),
					new Expression(DependencySpec.True, "$ID_MODE==%ID_MODE_A", ""),
					new Expression(DependencySpec.True, "$ID_MODE==%ID_MODE_B", ""),
					new Expression(DependencySpec.False, DependencySpec.Else, "")
					);
			testSpec(spec, expected, "ID_TARGET_C");
		}	
		{
			assertEquals(4, spec.getExpression("ID_TARGET_D").size());
			List<Expression> expected = Arrays.asList(
					new Expression(DependencySpec.True, "$ID_TRIGGER==%ID_TRIGGER_C", "($ID_MODE==%ID_MODE_B)"),
					new Expression(DependencySpec.True, "$ID_TRIGGER==%ID_TRIGGER_D", "($ID_MODE==%ID_MODE_B)"),
					new Expression(DependencySpec.True, "$ID_MODE==%ID_MODE_B", ""),
					new Expression(DependencySpec.False, DependencySpec.Else, "")
					);
			testSpec(spec, expected, "ID_TARGET_D");
		}		
		
		// Mode is strongest, Trigget and Target are the same
		matrix.setPriority("ID_MODE", 100);
		matrix.setPriority("ID_TRIGGER", 10);
		matrix.setPriority("ID_TARGET", 10);
		{
			assertEquals(4, spec.getExpression("ID_TARGET_D").size());
			List<Expression> expected = Arrays.asList(
					new Expression(DependencySpec.True, "$ID_TRIGGER==%ID_TRIGGER_C", "($ID_MODE==%ID_MODE_B)"),
					new Expression(DependencySpec.True, "$ID_TRIGGER==%ID_TRIGGER_D", "($ID_MODE==%ID_MODE_B)"),
					new Expression(DependencySpec.True, "$ID_MODE==%ID_MODE_B", ""),
					new Expression(DependencySpec.False, DependencySpec.Else, "")
					);
			testSpec(spec, expected, "ID_TARGET_D");
		}	
		
		// Not Options but it's enabled
		holder.addProperty(factory.createNumeric("ID_NUMERIC").defaultValue(0).unit("Hz").min(-100).max(100));
		matrix.add("ID_NUMERIC", AxisType.Y);
		/*
		 * 
		|       |Trigger A|Trigger B|Trigger C|Trigger D| 
		NUMERIC |    x    |         |     x   |         | 
		
	 */
		matrix.updateEnabled(matrix.yTitle.indexOf("ID_NUMERIC"), matrix.xTitle.indexOf("ID_TRIGGER_A"), true);
		matrix.updateEnabled(matrix.yTitle.indexOf("ID_NUMERIC"), matrix.xTitle.indexOf("ID_TRIGGER_C"), true);
		matrix.build();
		spec = depSpecHolder.getSpec("ID_NUMERIC");
		{
			List<Expression> expected = Arrays.asList(
					new Expression(DependencySpec.True, "$ID_TRIGGER==%ID_TRIGGER_A", ""),
					new Expression(DependencySpec.True, "$ID_TRIGGER==%ID_TRIGGER_C", ""),
					new Expression(DependencySpec.False, DependencySpec.Else, "")
					);
			testSpec(spec, expected, DependencySpec.Enable);	
		}
	}

	private void testSpec(DependencySpec spec, List<Expression> expected, String target) {
		List<Expression> exp2 = new ArrayList<Expression>(expected);
		Iterator<Expression> it = exp2.iterator();
		while(it.hasNext()) {
			if (spec.getExpression(target).contains(it.next())) {
				it.remove();
			}
		}
		assertEquals(0, exp2.size());
	}

//	@Test
//	public void testSamePriority() throws Exception {
//		PropertyHolder2 holder = new PropertyHolder2();
//		PropertyFactory factory = new PropertyFactory();
//		RuntimePropertyStore store = new RuntimePropertyStore(holder);
//		DependencySpecHolder depSpecHolder = new DependencySpecHolder();
//		
//		holder.addProperty(factory.createList("ID_MODE").option("ID_MODE_A", "Mode A", "").option("ID_MODE_B", "Mode B", ""));
//		holder.addProperty(factory.createList("ID_PULSE").option("ID_PULSE_A", "Pulse A", "").option("ID_PULSE_B", "Pulse B", ""));
//		holder.addProperty(factory.createList("ID_DISTANCE").option("ID_DISTANCE_A", "Distance A", "").option("ID_DISTANCE_B", "Distance B", ""));
//
//		RestrictionMatrix matrix = new RestrictionMatrix() {
//			@Override
//			protected DependencySpecHolder getDependencySpecHolder() {
//				return depSpecHolder;
//			}
//
//			@Override
//			protected void resetMask() {
//
//			}
//
//			@Override
//			protected RuntimeProperty getRuntimeProperty(String id, int index) {
//				return store.get(id, index);
//			}
//
//			@Override
//			protected RuntimeProperty getRuntimeProperty(String id) {
//				return store.get(id);
//			}
//
//			@Override
//			protected PropertyDef2 getPropertyDef(String id) {
//				return holder.get(id);
//			}
//
//			@Override
//			protected List<PropertyDef2> getAllPropertieDefs() {
//				return new ArrayList<PropertyDef2>(holder.getProperties());
//			}
//			
//		};
//		
//		matrix.add("ID_DISTANCE", AxisType.X);
//		matrix.add("ID_PULSE", AxisType.Y);
//		matrix.setPriority("ID_MODE", 100);
//		matrix.setPriority("ID_PULSE", 10);
//		matrix.setPriority("ID_DISTANCE", 10);
//	}
}

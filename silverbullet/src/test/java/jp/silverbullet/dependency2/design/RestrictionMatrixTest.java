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
import jp.silverbullet.property2.PropertyDef2;
import jp.silverbullet.property2.PropertyFactory;
import jp.silverbullet.property2.PropertyHolder2;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.property2.RuntimePropertyStore;

public class RestrictionMatrixTest {
	
	abstract class MatrixBuilder {
		private PropertyHolder2 holder = new PropertyHolder2();
		private PropertyFactory factory = new PropertyFactory();
		private RuntimePropertyStore store = null;
		private DependencySpecHolder depSpecHolder = new DependencySpecHolder();
		private DependencyDesigner designer = null;
		
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
		
		abstract List<PropertyDef2> registerProperties(PropertyFactory factory);

		public MatrixBuilder() {
			for (PropertyDef2 propertyDef : registerProperties(factory)) {
				this.holder.addProperty(propertyDef);
			}
			this.store = new RuntimePropertyStore(holder);	
			
			designer = new DependencyDesigner(holder) {
				@Override
				protected RuntimeProperty getRuntimeProperty(String id) {
					return store.get(id);
				}

				@Override
				protected List<PropertyDef2> getAllPropertieDefs() {
					return new ArrayList<PropertyDef2>(holder.getProperties());
				}

				@Override
				protected PropertyDef2 getPropertyDef(String id) {
					return holder.get(id);
				}

				@Override
				protected RuntimeProperty getRuntimeProperty(String id, int index) {
					return store.get(id, index);
				}

				@Override
				protected DependencySpecHolder getDependencySpecHolder() {
					return depSpecHolder;
				}

				@Override
				protected void resetMask() {

				}
				
			};
			designer.init();
		}

		public RestrictionMatrix getMatrix() {
			return this.matrix;
		}

		public DependencyDesigner getDesigner() {
			return designer;
		}
		
		public void addProperty(AddProperty addProperty) {
			for (PropertyDef2 propertyDef : addProperty.registerProperties(factory)) {
				this.holder.addProperty(propertyDef);
			}
		}
	}
	interface AddProperty {
		List<PropertyDef2> registerProperties(PropertyFactory factory);
	}
	
	@Test
	public void test() throws Exception {
		MatrixBuilder builder = new MatrixBuilder() {
			@Override
			List<PropertyDef2> registerProperties(PropertyFactory factory) {
				try {
					return Arrays.asList(
							factory.createList("ID_TRIGGER").option("ID_TRIGGER_A", "A", "").option("ID_TRIGGER_B", "B", "")
									.option("ID_TRIGGER_C", "C", "").option("ID_TRIGGER_D", "D", ""),
							factory.createList("ID_TARGET").option("ID_TARGET_A", "A", "").option("ID_TARGET_B", "B", "")
									.option("ID_TARGET_C", "C", "").option("ID_TARGET_D", "D", ""),
							factory.createList("ID_DUMMY").option("ID_DUMMY_A", "A", "").option("ID_DUMMY_B", "B", ""),
							factory.createList("ID_DUMMY2").option("ID_DUMMY2_A", "A", "").option("ID_DUMMY2_B", "B", "")
						);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		};
		
		DependencyDesigner designer = builder.getDesigner();
		/*
		|       |Trigger A|Trigger B|Trigger C|Trigger D| Dummy2_A
		Target A|    x    |         |         |         |
		Target B|    x    |    x    |         |         |
		Target C|         |    x    |    x    |         |
		Target D|         |         |    x    |    x    |
		Dummy A |         |         |         |         |    x
		 */
		
		designer.setSpecEnabled("ID_TRIGGER_A", "ID_TARGET_A", true);
		designer.setSpecEnabled("ID_TRIGGER_A", "ID_TARGET_B", true);
		designer.setSpecEnabled("ID_TRIGGER_B", "ID_TARGET_B", true);
		designer.setSpecEnabled("ID_TRIGGER_B", "ID_TARGET_C", true);
		designer.setSpecEnabled("ID_TRIGGER_C", "ID_TARGET_C", true);
		designer.setSpecEnabled("ID_TRIGGER_C", "ID_TARGET_D", true);
		designer.setSpecEnabled("ID_TRIGGER_D", "ID_TARGET_D", true);
		designer.setSpecEnabled("ID_DUMMY2_A", "ID_DUMMY_A", true);
		
		// In case, TRIGGER is stronger than TARGET
		designer.setPriority("ID_TRIGGER", 10); 
		designer.setPriority("ID_TARGET", 0);
		designer.buildSpec();
	
		DependencySpecHolder depSpecHolder = designer.getDependencySpecHolder();
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
		designer.setPriority("ID_TRIGGER", 10); 
		designer.setPriority("ID_TARGET", 20);
		designer.buildSpec();
		
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
		designer.setPriority("ID_TRIGGER", 10); 
		designer.setPriority("ID_TARGET", 10);
		designer.buildSpec();
		
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
		builder.addProperty(new AddProperty() {
			@Override
			public List<PropertyDef2> registerProperties(PropertyFactory factory) {
				try {
					return Arrays.asList(factory.createList("ID_MODE").option("ID_MODE_A", "A", "").option("ID_MODE_B", "B", ""));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					assertEquals(false, true);
				}
				return null;
			}
		});

		designer.setPriority("ID_TRIGGER", 10); 
		designer.setPriority("ID_TARGET", 0);
		designer.setPriority("ID_MODE", 100);
		

		/*
		|       |Trigger A|Trigger B|Trigger C|Trigger D| Mode A | Mode B |
		Target A|    x    |         |         |         |    x   |    x   |
		Target B|    x    |    x    |         |         |    x   |    x   |
		Target C|         |    x    |    x    |         |    x   |    x   |
		Target D|         |         |    x    |    x    |        |    x   |
		 */
		
		designer.setSpecEnabled("ID_MODE_A", "ID_TARGET_A", true);
		designer.setSpecEnabled("ID_MODE_A", "ID_TARGET_B", true);
		designer.setSpecEnabled("ID_MODE_A", "ID_TARGET_C", true);

		designer.setSpecEnabled("ID_MODE_B", "ID_TARGET_A", true);
		designer.setSpecEnabled("ID_MODE_B", "ID_TARGET_B", true);
		designer.setSpecEnabled("ID_MODE_B", "ID_TARGET_C", true);
		designer.setSpecEnabled("ID_MODE_B", "ID_TARGET_D", true);

		designer.buildSpec();		
		
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
		designer.setPriority("ID_MODE", 100);
		designer.setPriority("ID_TRIGGER", 10);
		designer.setPriority("ID_TARGET", 10);
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
		builder.addProperty(new AddProperty() {
			@Override
			public List<PropertyDef2> registerProperties(PropertyFactory factory) {
				return Arrays.asList(factory.createNumeric("ID_NUMERIC").defaultValue(0).unit("Hz").min(-100).max(100));
			}
		});

		/*
		 * 
		|       |Trigger A|Trigger B|Trigger C|Trigger D| 
		NUMERIC |    x    |         |     x   |         | 
		
	 */
		
		designer.setSpecEnabled("ID_TRIGGER_A", "ID_NUMERIC", true);
		designer.setSpecEnabled("ID_TRIGGER_C", "ID_NUMERIC", true);
		designer.buildSpec();
		
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
	
	@Test
	public void testValue() throws Exception {
		MatrixBuilder builder = new MatrixBuilder() {
			@Override
			List<PropertyDef2> registerProperties(PropertyFactory factory) {
				try {
					return Arrays.asList(
							factory.createList("ID_BAND").option("ID_BAND_A", "A", "").option("ID_BAND_B", "B", "")
									.option("ID_BAND_C", "C", "").option("ID_BAND_D", "D", ""),
							factory.createList("ID_OPTION").option("ID_OPTION_A", "A", "").option("ID_OPTION_B", "B", ""),
							factory.createNumeric("ID_NUMERIC"),
							factory.createNumeric("ID_NUMERIC2")
						);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		};
		
		DependencyDesigner designer = builder.getDesigner();
		
		/*
		 * 
		|         | BAND  | BAND A | BAND B | BAND C | BAND D | NUMERIC | NUMERIC2 | 
		| NUMERIC |       |    0   |   100  |        |        |         |     <    |
		| BAND    |       |        |        |        |        |BAND_C:* |          |
		| BAND A  |       |        |        |        |        |         |          |
		| BAND B  |       |        |        |        |        |         |          |
		| BAND C  |       |        |        |        |        |         |          |
		| BAND D  |       |        |        |        |        |         |          |
		| NUMERIC2|       |        |        |        |        |         |          |
		
	 */
		designer.setSpecValue("ID_BAND_A", "ID_NUMERIC", "0");
		designer.setSpecValue("ID_BAND_B", "ID_NUMERIC", "100");
		designer.setSpecValue("ID_NUMERIC2", "ID_NUMERIC", "<");
		designer.setSpecValue("ID_NUMERIC", "ID_BAND", "ID_BAND_C:*");
		designer.setSpecValue("ID_BAND_A", "ID_OPTION", "ID_OPTION_A");	
		designer.buildSpec();	
		
		DependencySpecHolder depSpecHolder = designer.getDependencySpecHolder();
		{
			DependencySpec spec = depSpecHolder.getSpec("ID_NUMERIC");
			List<Expression> expected = Arrays.asList(
					new Expression("0", "$ID_BAND==%ID_BAND_A", ""),
					new Expression("100", "$ID_BAND==%ID_BAND_B", ""),
					new Expression("$ID_NUMERIC2", "$ID_NUMERIC>$ID_NUMERIC2", "")
					);
			testSpec(spec, expected, DependencySpec.Value);	
		}
		{
			DependencySpec spec = depSpecHolder.getSpec("ID_NUMERIC2");
			List<Expression> expected = Arrays.asList(
					new Expression("$ID_NUMERIC", "$ID_NUMERIC2<$ID_NUMERIC", "")
					);
			testSpec(spec, expected, DependencySpec.Value);	
		}
		{
			DependencySpec spec = depSpecHolder.getSpec("ID_BAND");
			List<Expression> expected = Arrays.asList(
					new Expression("ID_BAND_C", "$ID_NUMERIC==$ID_NUMERIC", "")
					);
			testSpec(spec, expected, DependencySpec.Value);	
		}
		{
			DependencySpec spec = depSpecHolder.getSpec("ID_OPTION");
			List<Expression> expected = Arrays.asList(
					new Expression("ID_OPTION_A", "$ID_BAND==%ID_BAND_A", "")
					);
			testSpec(spec, expected, DependencySpec.Value);	
		}
	}

	@Test 
	void testMixMax() {
		MatrixBuilder builder = new MatrixBuilder() {
			@Override
			List<PropertyDef2> registerProperties(PropertyFactory factory) {
				return Arrays.asList(
						factory.createNumeric("ID_LOWER_LIMIT"),
						factory.createNumeric("ID_UPPER_LIMIT"),
						factory.createNumeric("ID_CURSOR_LEFT"),
						factory.createNumeric("ID_CURSOR_RIGHT")
					);
			}
		};
		
		DependencyDesigner designer = builder.getDesigner();
		
		/*
		 * 
		|             | CURSOR_LEFT  | CURSOR_RIGHT | LOWER_LIMIT | UPPER_LIMIT | 
		| CURSOR_LEFT |              |      <       |      >      |             |  
		| CURSOR_RIGHT|      >       |              |             |      <      |      
		| LOWER_LIMIT |      <       |              |             |             |      
		| UPPER_LIMIT |              |      >       |             |             |      		
	 */
		
		designer.setSpecValue("ID_CURSOR_LEFT", "ID_CURSOR_RIGHT", ">");
		designer.setSpecValue("ID_CURSOR_LEFT", "ID_LOWER_LIMIT", "<");
		designer.setSpecValue("ID_CURSOR_RIGHT", "ID_UPPER_LIMIT", ">");
		
		designer.setPriority("ID_CURSOR_LEFT", 0);
		designer.setPriority("ID_CURSOR_RIGHT", 0);
		designer.setPriority("ID_LOWER_LIMIT", 1);
		designer.setPriority("ID_UPPER_LIMIT", 1);
		
		RestrictionMatrix matrix = designer.getMatrix(
				"ID_CURSOR_LEFT,ID_CURSOR_RIGHT,ID_LOWER_LIMIT,ID_UPPER_LIMIT", 
				"ID_CURSOR_LEFT,ID_CURSOR_RIGHT,ID_LOWER_LIMIT,ID_UPPER_LIMIT");
				
		assertEquals(">", matrix.valueMatrix[matrix.yTitle.indexOf("ID_CURSOR_RIGHT")][matrix.xTitle.indexOf("ID_CURSOR_LEFT")]);
		assertEquals("<", matrix.valueMatrix[matrix.yTitle.indexOf("ID_LOWER_LIMIT")][matrix.xTitle.indexOf("ID_CURSOR_LEFT")]);
		assertEquals("<", matrix.valueMatrix[matrix.yTitle.indexOf("ID_CURSOR_LEFT")][matrix.xTitle.indexOf("ID_CURSOR_RIGHT")]);
		assertEquals(">", matrix.valueMatrix[matrix.yTitle.indexOf("ID_UPPER_LIMIT")][matrix.xTitle.indexOf("ID_CURSOR_RIGHT")]);
		assertEquals("<", matrix.valueMatrix[matrix.yTitle.indexOf("ID_CURSOR_RIGHT")][matrix.xTitle.indexOf("ID_UPPER_LIMIT")]);
		assertEquals(">", matrix.valueMatrix[matrix.yTitle.indexOf("ID_CURSOR_LEFT")][matrix.xTitle.indexOf("ID_LOWER_LIMIT")]);

		
		designer.buildSpec();
		
		DependencySpecHolder depSpecHolder = designer.getDependencySpecHolder();
		{
			DependencySpec spec = depSpecHolder.getSpec("ID_CURSOR_LEFT");
			List<Expression> extected = Arrays.asList(
					new Expression("$ID_CURSOR_RIGHT", "$ID_CURSOR_LEFT>$ID_CURSOR_RIGHT", ""),
					new Expression("$ID_LOWER_LIMIT", "$ID_CURSOR_LEFT<$ID_LOWER_LIMIT", "")
					);
			testSpec(spec, extected, DependencySpec.Value);	
			
			extected = Arrays.asList(
					new Expression("$ID_LOWER_LIMIT", "$ID_LOWER_LIMIT==$ID_LOWER_LIMIT", "")
					);			
			testSpec(spec, extected, DependencySpec.Min);	
		}
		{
			DependencySpec spec = depSpecHolder.getSpec("ID_CURSOR_RIGHT");
			List<Expression> expected = Arrays.asList(
					new Expression("$ID_CURSOR_LEFT", "$ID_CURSOR_RIGHT<$ID_CURSOR_LEFT", ""),
					new Expression("$ID_UPPER_LIMIT", "$ID_CURSOR_RIGHT>$ID_UPPER_LIMIT", "")
					);
			testSpec(spec, expected, DependencySpec.Value);	
			
			expected = Arrays.asList(
					new Expression("$ID_UPPER_LIMIT", "$ID_UPPER_LIMIT==$ID_UPPER_LIMIT", "")
					);			
			testSpec(spec, expected, DependencySpec.Max);
		}
	}

}

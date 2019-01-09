package jp.silverbullet.property2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import jp.silverbullet.XmlPersistent;
import jp.silverbullet.property2.PropertyDef2;
import jp.silverbullet.property2.PropertyFactory;
import jp.silverbullet.property2.PropertyHolder2;
import jp.silverbullet.property2.PropertyType2;
import jp.silverbullet.property2.WebTableConverter;
import jp.silverbullet.web.JsonTable;
import obsolute.property.ArgumentDefInterface;
import obsolute.property.PropertyDef;
import obsolute.property.PropertyHolder;

public class PropertyHolderTest {
	private PropertyType types = new PropertyType();
	private ArgumentDefInterface argDef = new ArgumentDefInterface() {

		@Override
		public int indexOf(String type, String key) {
			return types.getArguments(type).indexOf(key);
		}

		@Override
		public List<String> get(String type) {
			return types.getArguments(type);
		}
		
	};
	private <T> T load(Class<T> clazz, String filename) {
		XmlPersistent<T> propertyPersister = new XmlPersistent<>();
		try {
			return propertyPersister.load(filename, clazz);
		} catch (Exception e) {
			try {
				return clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}
	@Test
	public void convert() {
		PropertyHolder propertiesHolder = load(PropertyHolder.class, "C:\\Users\\çNçG\\git3\\openti\\openti\\sv_tmp\\id_def.xml");
		PropertyHolder2 holder = new PropertyHolder2();
		PropertyFactory factory = new PropertyFactory();
		
		for (PropertyDef original : propertiesHolder.getProperties()) {
			original.setArgumentDef(argDef);
			
			PropertyDef2 def2 = new PropertyDef2();
			def2.setId(original.getId());
			def2.setType(getType(original.getType()));
			def2.setArraySize(original.getSize());
			def2.setComment(original.getComment());
			def2.setTitle(original.getTitle());
			def2.setGroup(original.getGroup());
			if (def2.getType().equals(PropertyType2.Numeric)) {
				def2.setUnit(original.getOthers().get(0));
				def2.setDecimals(Integer.valueOf(getInt(original.getOthers().get(1))));
				def2.setMin(Double.valueOf(original.getOthers().get(2)));
				def2.setMax(Double.valueOf(original.getOthers().get(3)));
//				def2.setDefaultValue(original.getOthers().get(4));
			}
			else if (def2.getType().equals(PropertyType2.List)) {
				def2.setAllOptions(original.getListDetail());
				def2.setDefaultId(original.getOthers().get(2));
			}
			holder.addProperty(def2);
			//def2.setDefaultValue(original.getArgumentValue("defaultValue"));
		}
		
		holder.save("C:\\Users\\çNçG\\git3\\openti\\openti\\newid2.json");
//		holder.load("C:\\Users\\a1199022\\git3\\openti\\openti\\newid2.json");
		
		WebTableConverter converter = new WebTableConverter(holder);
		JsonTable table = converter.createIdTable(PropertyType2.NotSpecified);
		
//		new JsonPersistent().saveJson(holder, "C:\\Users\\a1199022\\git3\\openti\\openti\\newid.json");
	}
	
	private int getInt(String string) {
		try {
			return Integer.valueOf(string);
		}
		catch (Exception e) {
			return 0;
		}
	}
	private PropertyType2 getType(String type) {
		if (type.equals("DoubleProperty")) {
			return PropertyType2.Numeric;
		}
		else if (type.equals("LongProperty")) {
			return PropertyType2.Numeric;
		}
		return PropertyType2.valueOf(type.replace("Property", ""));
	}
	
	@Test
	public void test() {
		PropertyHolder2 holder = new PropertyHolder2();
		PropertyFactory factory = new PropertyFactory();
		RuntimePropertyStore store = new RuntimePropertyStore(holder);
		
		try {
			holder.addProperty(factory.createList("ID_LIST").option("ID_LIST_A", "A", "").option("ID_LIST_D", "D", "").defaultId("ID_LIST_A"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		holder.addProperty(factory.createNumeric("ID_NUMERIC").min(-10).max(10).unit("Hz").decimals(2).defaultValue(10.0));
		holder.addProperty(factory.createBoolean("ID_BOOL").defaultValue(PropertyDef2.True));
		holder.addProperty(factory.createText("ID_TEXT").defaultValue("Default Text").maxLength(100));
		holder.addProperty(factory.createChart("ID_CHART"));
		holder.addProperty(factory.createTable("ID_TABLE"));
		
		
		{
			// test options
			PropertyDef2 def = holder.get("ID_LIST");
			assertEquals("ID_LIST", def.getId());
			assertEquals(2, def.getOptionValues().size());
			assertEquals("ID_LIST_A", def.getOption(0).getId());
			assertEquals("ID_LIST_D", def.getOption(1).getId());
			assertEquals("ID_LIST_A", def.getDefaultId());
			
			// test default value of runtime property
			assertEquals("ID_LIST_A", store.get("ID_LIST").getCurrentValue());
			assertEquals("ID_LIST_A", store.get("ID_LIST#0").getCurrentValue());		
			assertEquals("10.00", store.get("ID_NUMERIC").getCurrentValue());
			assertEquals("Default Text", store.get("ID_TEXT").getCurrentValue());
			
			// Items should be sorted when added
			try {
				def.option("ID_LIST_B", "B", "");
			} catch (Exception e) {
				e.printStackTrace();
			}
			assertEquals(3, def.getOptionValues().size());
			assertEquals("ID_LIST_A", def.getOption(0).getId());
			assertEquals("ID_LIST_B", def.getOption(1).getId());
			assertEquals("ID_LIST_D", def.getOption(2).getId());	
			
			def.removeOption("ID_LIST_B");
			assertEquals(2, def.getOptionValues().size());
			assertEquals("ID_LIST_A", def.getOption(0).getId());
			assertEquals("ID_LIST_D", def.getOption(1).getId());	
			
			// option ID is changed
			def.changeOptionId("ID_LIST_A", "ID_LIST_Z");
			assertEquals(2, def.getOptionValues().size());
			assertEquals("ID_LIST_D", def.getOption(0).getId());
			assertEquals("ID_LIST_Z", def.getOption(1).getId());	
			//assertEquals("ID_LIST_Z", store.get("ID_LIST").getCurrentValue()); don't need 
			
			// When main ID is changed, option ID's should be changed 
			def.setId("ID_NEWLIST");
			assertEquals(2, def.getOptionValues().size());
			assertEquals("ID_NEWLIST_D", def.getOption(0).getId());
			assertEquals("ID_NEWLIST_Z", def.getOption(1).getId());	
			
			PropertyDef2 def2 = holder.get("ID_NEWLIST");
			assertEquals("ID_NEWLIST", def2.getId());
		}
		{
			PropertyDef2 def = holder.get("ID_NUMERIC");
			assertEquals("ID_NUMERIC", def.getId());
			assertEquals("Hz", def.getUnit());
			assertEquals(2, def.getDecimals());
			assertTrue(-10.0 == def.getMin());
			assertTrue(10.0 == def.getMax());
			assertTrue(10.0 == Double.valueOf(def.getDefaultValue()));
		}
		
		{
			//// delete
			assertEquals(6, holder.getProperties().size());
			holder.remove("ID_NEWLIST");
			assertEquals(5, holder.getProperties().size());

			assertEquals(null, store.get("ID_NEWLIST"));
			
			// runtime properties should be removed if def property is removed
			holder.addProperty(factory.createNumeric("ID_SIZE_5").arraySize(5));
			assertEquals("ID_SIZE_5", store.get("ID_SIZE_5#0").getId());
			assertEquals("ID_SIZE_5", store.get("ID_SIZE_5#1").getId());
			assertEquals("ID_SIZE_5", store.get("ID_SIZE_5#2").getId());
			assertEquals("ID_SIZE_5", store.get("ID_SIZE_5#4").getId());
			holder.remove("ID_SIZE_5"); // Now removes!!
			assertEquals(null, store.get("ID_SIZE_5#0"));
			assertEquals(null, store.get("ID_SIZE_5#1"));
			assertEquals(null, store.get("ID_SIZE_5#2"));
			assertEquals(null, store.get("ID_SIZE_5#4"));
		}
		
		{
			// copy
			try {
				holder.addProperty(factory.createList("ID_LIST").option("ID_LIST_A", "A", "").option("ID_LIST_D", "D", "").defaultId("ID_LIST_A"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			assertEquals(6, holder.getProperties().size());
			holder.createClone("ID_LIST", "ID_NEWLIST2");
			assertEquals(7, holder.getProperties().size());
			PropertyDef2 def = holder.get("ID_NEWLIST2");
			assertEquals("ID_NEWLIST2", def.getId());
			assertEquals(2, def.getOptionValues().size());
			assertEquals("ID_NEWLIST2_A", def.getOption(0).getId());
			assertEquals("ID_NEWLIST2_D", def.getOption(1).getId());
			assertEquals("ID_NEWLIST2_A", def.getDefaultId());
			
			def = holder.get("ID_LIST");
			assertEquals("ID_LIST", def.getId());
			assertEquals(2, def.getOptionValues().size());
			assertEquals("ID_LIST_A", def.getOption(0).getId());
			assertEquals("ID_LIST_D", def.getOption(1).getId());
			assertEquals("ID_LIST_A", def.getDefaultId());
			
			RuntimeProperty runtime = store.get("ID_NEWLIST2");
			assertEquals("ID_NEWLIST2", runtime.getId());
			assertEquals("ID_NEWLIST2_A", runtime.getCurrentValue());
		}
		
//		System.out.println(holder.getTypes().toString());
		assertEquals("[NotSpecified, List, Numeric, Text, Boolean, Chart, Table, Image, Action]", holder.getTypes().toString());
	}

	@Test
	public void testWebTable() {
		PropertyHolder2 holder = new PropertyHolder2();
		PropertyFactory factory = new PropertyFactory();
		
		try {
			holder.addProperty(factory.createList("ID_LIST").option("ID_LIST_A", "A", "").option("ID_LIST_D", "D", "").defaultValue("ID_LIST_A"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		holder.addProperty(factory.createNumeric("ID_NUMERIC").min(-10).max(10).unit("Hz").decimals(2).defaultValue(0.0));
		holder.addProperty(factory.createBoolean("ID_BOOL").defaultValue(PropertyDef2.True));
		holder.addProperty(factory.createText("ID_TEXT").defaultValue("Default Text").maxLength(100));
		holder.addProperty(factory.createChart("ID_CHART"));
		holder.addProperty(factory.createTable("ID_TABLE"));
		
		JsonTable jsonTable = new WebTableConverter(holder).createIdTable(PropertyType2.NotSpecified);
	}
	
}

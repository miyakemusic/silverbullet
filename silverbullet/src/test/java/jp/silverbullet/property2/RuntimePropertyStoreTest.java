package jp.silverbullet.property2;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jp.silverbullet.dependency2.CachedPropertyStore;
import jp.silverbullet.web.ui.PropertyGetter;

public class RuntimePropertyStoreTest {

	@Test
	public void test() {
		PropertyFactory factory = new PropertyFactory();
		PropertyHolder2 def = new PropertyHolder2();
		def.addProperty(factory.createBoolean("ID_BOOLEAN").title("Boolean2").defaultValue(PropertyDef2.True));
		def.addProperty(factory.createNumeric("ID_NUMERIC").defaultValue(200).decimals(2));
		try {
			def.addProperty(factory.createList("ID_LIST").option("ID_LIST_A", "A", "").
					option("ID_LIST_B", "B", "").option("ID_LIST_C", "C", "").defaultId("ID_LIST_B").arraySize(3));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		RuntimePropertyStore store = new RuntimePropertyStore(def);
		
		// test get all
		assertEquals("ID_BOOLEAN", store.getAllProperties().get(0).getId());
		assertEquals("ID_NUMERIC", store.getAllProperties().get(1).getId());
		
		assertEquals("ID_BOOLEAN", store.getAllProperties(PropertyType2.Boolean).get(0).getId());
		assertEquals("ID_NUMERIC", store.getAllProperties().get(1).getId());
		assertEquals(5, store.getAllProperties(PropertyType2.NotSpecified).size()); //5 = boolean + numeric + list*3
		
		assertEquals(5, store.getIds(PropertyType2.NotSpecified).size());
		assertEquals(1, store.getIds(PropertyType2.Boolean).size());
		assertEquals(1, store.getIds(PropertyType2.Numeric).size());
		assertEquals(3, store.getIds(PropertyType2.List).size());
		
		
		assertEquals(PropertyDef2.True, store.get("ID_BOOLEAN").getCurrentValue());
		assertEquals("ID_LIST_B", store.get("ID_LIST", 0).getCurrentValue());
		assertEquals("ID_LIST_B", store.get("ID_LIST", 1).getCurrentValue());
		assertEquals("ID_LIST_B", store.get("ID_LIST", 2).getCurrentValue());
		assertEquals("200.00", store.get("ID_NUMERIC").getCurrentValue());
		
		def.remove("ID_BOOLEAN");
		assertEquals(null, store.get("ID_BOOLEAN"));
		assertEquals("ID_LIST", store.get("ID_LIST").getId());
		
		def.addProperty(factory.createBoolean("ID_BOOLEAN").title("Boolean"));
		store.get("ID_BOOLEAN").setCurrentValue(PropertyDef2.False);
		store.get("ID_BOOLEAN").setTitle("NewTitle");
		store.get("ID_NUMERIC").setCurrentValue("123.0");
		store.get("ID_LIST").setCurrentValue("ID_LIST_B");
		
		store.save("unittest.json");
		store.get("ID_BOOLEAN").setCurrentValue(PropertyDef2.True);
		store.get("ID_BOOLEAN").setTitle("1111");
		store.get("ID_NUMERIC").setCurrentValue("-123.0");
		store.get("ID_LIST").setCurrentValue("ID_LIST_A");
		
		// reset mask
		store.get("ID_LIST").enableOption("ID_LIST_A", false);
		assertEquals(true, store.get("ID_LIST").isOptionDisabled("ID_LIST_A"));
		store.resetMask();
		assertEquals(false, store.get("ID_LIST").isOptionDisabled("ID_LIST_A"));
		
		// load
		IdValues ret = store.load("unittest.json");
		
		assertEquals(PropertyDef2.False,  ret.getValue("ID_BOOLEAN#0"));

		assertEquals("123.00", ret.getValue("ID_NUMERIC#0"));

		assertEquals("ID_LIST_B", ret.getValue("ID_LIST#0"));

	}

	class ForTest {
		PropertyFactory factory = new PropertyFactory();
		PropertyHolder2 def = new PropertyHolder2();
		RuntimePropertyStore store = new RuntimePropertyStore(def);
		CachedPropertyStore cache = new CachedPropertyStore(new PropertyGetter() {
			@Override
			public RuntimeProperty getProperty(String id) {
				return store.get(id);
			}

			@Override
			public RuntimeProperty getProperty(String id, int index) {
				return store.get(id, index);
			}
		});
		public PropertyHolder2 getDef() {
			return def;
		}
		public RuntimePropertyStore getStore() {
			return store;
		}
		public CachedPropertyStore getCache() {
			return cache;
		}
		public PropertyFactory getFactory() {
			return factory;
		}
		
	}
	@Test
	public void testBoolean() {
		ForTest forTest = new ForTest();
		PropertyHolder2 def = forTest.getDef();
		CachedPropertyStore cache = forTest.getCache();
		RuntimePropertyStore store = forTest.getStore();
		
		def.addProperty(forTest.getFactory().createBoolean("ID_BOOLEAN").title("Boolean").defaultValue(PropertyDef2.True));
		
		assertEquals(PropertyDef2.True, cache.getProperty("ID_BOOLEAN").getCurrentValue());
		
		RuntimeProperty property = cache.getProperty("ID_BOOLEAN");
		// Does not change
		property.setCurrentValue(PropertyDef2.True);
		cache.commit();
		assertEquals(PropertyDef2.True, store.get("ID_BOOLEAN").getCurrentValue());
		
		// value changed
		property.setCurrentValue(PropertyDef2.False);

		// enable changed
		property.setEnabled(false);
		cache.commit();
		assertEquals(false, store.get("ID_BOOLEAN").isEnabled()); // disabled
		
		property.setEnabled(true);
		cache.commit();
		assertEquals(true, store.get("ID_BOOLEAN").isEnabled()); // disabled
		
		// title changed
		assertEquals("Boolean", property.getTitle());
		property.setTitle("NewBoolean");
		cache.commit();
		assertEquals("NewBoolean", property.getTitle());
	}
	
	@Test
	public void testNumeric() {
		ForTest forTest = new ForTest();
		PropertyHolder2 def = forTest.getDef();
		CachedPropertyStore cache = forTest.getCache();
		RuntimePropertyStore store = forTest.getStore();
		PropertyFactory factory = forTest.getFactory();
		
		def.addProperty(factory.createNumeric("ID_NUMERIC").decimals(3).defaultValue(20).min(-10).max(10).unit("Hz").arraySize(2));
			
		RuntimeProperty property = cache.getProperty("ID_NUMERIC");
		
		// change value
		property.setCurrentValue("30");
		cache.commit();
		assertEquals("30.000", store.get("ID_NUMERIC").getCurrentValue());
		assertEquals("20.000", store.get("ID_NUMERIC", 1).getCurrentValue()); // Another one does not change
		
		// change min
		property.setMin("-100");
		cache.commit();
		assertEquals("-100.000", store.get("ID_NUMERIC").getMin());
		assertEquals("-100.000", store.get("ID_NUMERIC", 1).getMin());
		
		// change max
		property.setMax("100");
		cache.commit();
		assertEquals("100.000", store.get("ID_NUMERIC").getMax());
		assertEquals("100.000", store.get("ID_NUMERIC", 1).getMax());
		
		// change unit
		property.setUnit("MHz");
		cache.commit();
		assertEquals("MHz", store.get("ID_NUMERIC").getUnit());
		assertEquals("MHz", store.get("ID_NUMERIC", 1).getUnit());
		
		// change array size
		property.setSize(3);
		cache.commit();
		assertEquals("30.000", store.get("ID_NUMERIC").getCurrentValue());
		assertEquals("20.000", store.get("ID_NUMERIC", 1).getCurrentValue());
		assertEquals("20.000", store.get("ID_NUMERIC", 2).getCurrentValue());
		
		property.setSize(2);
		cache.commit();
		assertEquals("30.000", store.get("ID_NUMERIC").getCurrentValue());
		assertEquals("20.000", store.get("ID_NUMERIC", 1).getCurrentValue());
		assertEquals(null, store.get("ID_NUMERIC", 2));
	}
	
	@Test
	public void testList() throws Exception {
		ForTest forTest = new ForTest();
		PropertyHolder2 def = forTest.getDef();
		CachedPropertyStore cache = forTest.getCache();
		RuntimePropertyStore store = forTest.getStore();
		
		def.addProperty(forTest.getFactory().createBoolean("ID_LIST").title("LIST").
				option("ID_LIST_A", "A", "A").option("ID_LIST_B", "B", "").
				defaultId("ID_LIST_A"));
		
		RuntimeProperty property = cache.getProperty("ID_LIST");
		
		// disable
		property.enableOption("ID_LIST_A", false);
		cache.commit();
		assertEquals(1, store.get("ID_LIST").getAvailableListDetail().size());
		assertEquals("ID_LIST_B", store.get("ID_LIST").getAvailableListDetail().get(0).getId());
		assertEquals(true, store.get("ID_LIST").isOptionDisabled(0));
		assertEquals(false, store.get("ID_LIST").isOptionDisabled(1));
		
		// enable
		property.enableOption("ID_LIST_A", true);
		cache.commit();
		assertEquals(2, store.get("ID_LIST").getAvailableListDetail().size());
		assertEquals("ID_LIST_A", store.get("ID_LIST").getAvailableListDetail().get(0).getId());
		assertEquals("ID_LIST_B", store.get("ID_LIST").getAvailableListDetail().get(1).getId());
		
		// value
		property.setCurrentValue("ID_LIST_B");
		cache.commit();
		assertEquals("B", store.get("ID_LIST").getSelectedListTitle());
		
	}
	
	@Test
	public void testIdChanged() throws Exception {
		ForTest forTest = new ForTest();
		PropertyHolder2 def = forTest.getDef();
		CachedPropertyStore cache = forTest.getCache();
		RuntimePropertyStore store = forTest.getStore();
		
		def.addProperty(forTest.getFactory().createBoolean("ID_LIST").title("LIST").
				option("ID_LIST_A", "A", "A").option("ID_LIST_B", "B", "").
				defaultId("ID_LIST_A"));
		
		def.get("ID_LIST").setId("ID_NEWLIST");
		assertEquals("ID_NEWLIST", store.get("ID_NEWLIST").getId());
		assertEquals(null, store.get("ID_LIST"));
	}
}

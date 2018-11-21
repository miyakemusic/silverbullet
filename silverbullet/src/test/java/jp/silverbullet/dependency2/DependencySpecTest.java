package jp.silverbullet.dependency2;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.CachedPropertyStore;
import jp.silverbullet.dependency.DepPropertyStore;
import jp.silverbullet.dependency.RequestRejectedException;
import jp.silverbullet.dependency.speceditor3.SvPropertyFactory;

class DependencySpecTest {
	private DepPropertyStore createPropertyStore() {
		DepPropertyStore store = new DepPropertyStore() {
			private Map<String, SvProperty> props = new HashMap<>();
			@Override
			public SvProperty getProperty(String id) {
				return props.get(id);
			}
			@Override
			public void add(SvProperty property) {
				props.put(property.getId(), property);
			}			
		};
		return store;
	}
	
	private SvProperty createDoubleProperty(String id, double defaultValue, String unit, double min, double max, int decimal) {
		return new SvPropertyFactory().getDoubleProperty(id, defaultValue, unit, min, max, decimal);

	}

	private SvProperty createListProperty(String id, List<String> asList, String defaultId) {
		return new SvPropertyFactory().getListProperty(id, asList, defaultId);
	}
	
	@Test
	void testOptionEnabled() {
		DepPropertyStore store = createPropertyStore();
		store.add(createListProperty("ID_MIDDLE", Arrays.asList("ID_MIDDLE_A1", "ID_MIDDLE_A2", "ID_MIDDLE_B1", "ID_MIDDLE_B2"), "ID_MIDDLE_A1"));
		store.add(createListProperty("ID_ROOT", Arrays.asList("ID_ROOTA", "ID_ROOTA2", "ID_ROOTB"), "ID_ROOTA"));
		
		DependencySpec spec = new DependencySpec("ID_MIDDLE");
		spec.addOptionEnabled("ID_MIDDLE_A1", DependencySpec.True, "$ID_ROOT==%ID_ROOTA");
		spec.addOptionEnabled("ID_MIDDLE_A1", DependencySpec.True, "$ID_ROOT==%ID_ROOTA2");
		spec.addOptionEnabled("ID_MIDDLE_A2", DependencySpec.True, "$ID_ROOT==%ID_ROOTA");
		spec.addOptionEnabled("ID_MIDDLE_B1", DependencySpec.True, "$ID_ROOT==%ID_ROOTB");
		spec.addOptionEnabled("ID_MIDDLE_B2", DependencySpec.True, "$ID_ROOT==%ID_ROOTB");

		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(spec);
		
		DependencyEngine engine = new DependencyEngine(specHolder, store);
		try {
			engine.requestChange("ID_ROOT", "ID_ROOTA");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			
			assertEquals(true, cached.getProperty("ID_MIDDLE_A1").isEnabled());	
			assertEquals(false, cached.getProperty("ID_MIDDLE_A2").isEnabled());	
			assertEquals(false, cached.getProperty("ID_MIDDLE_B1").isEnabled());	
			assertEquals(false, cached.getProperty("ID_MIDDLE_B2").isEnabled());	
			assertEquals(false, cached.getProperty("ID_MIDDLE_A1").isEnabled());	
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		try {
			engine.requestChange("ID_ROOT", "ID_ROOTA2");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			
			assertEquals(true, cached.getProperty("ID_MIDDLE_A1").isEnabled());			
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}		
		
	}

	@Test
	void testEnabled() {
		DependencySpec spec = new DependencySpec("ID_MIDDLE");
		spec.setEnabeld(DependencySpec.True, "$ID_ROOT==%ID_ROOTA");
		spec.setEnabeld(DependencySpec.False, DependencySpec.Else);
		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(spec);
		
		DepPropertyStore store = createPropertyStore();
		store.add(createListProperty("ID_MIDDLE", Arrays.asList("ID_MIDDLE_A1", "ID_MIDDLE_A2", "ID_MIDDLE_B1", "ID_MIDDLE_B2"), "ID_MIDDLE_A1"));
		store.add(createListProperty("ID_ROOT", Arrays.asList("ID_ROOTA", "ID_ROOTA2", "ID_ROOTB"), "ID_ROOTA"));

		DependencyEngine engine = new DependencyEngine(specHolder, store);
		try {
			store.getProperty("ID_MIDDLE").setEnabled(false);
			engine.requestChange("ID_ROOT", "ID_ROOTA");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals(true, cached.getProperty("ID_MIDDLE").isEnabled());	
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		
		try {
			store.getProperty("ID_MIDDLE").setEnabled(true);
			engine.requestChange("ID_ROOT", "ID_ROOTB");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals(false, cached.getProperty("ID_MIDDLE").isEnabled());	
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testOptionSelect() {
		DepPropertyStore store = createPropertyStore();
		store.add(createListProperty("ID_LEAF", Arrays.asList("LEAF_A1_1", "LEAF_A1_2", "LEAF_A2"), "LEAF_A1_1"));
		store.add(createListProperty("ID_MIDDLE", Arrays.asList("ID_MIDDLE_A1", "ID_MIDDLE_A2", "ID_MIDDLE_B1", "ID_MIDDLE_B2"), "ID_MIDDLE_A1"));
		store.add(createListProperty("ID_ROOT", Arrays.asList("ID_ROOTA", "ID_ROOTA2", "ID_ROOTB"), "ID_ROOTA"));

		DependencySpec spec = new DependencySpec("ID_MIDDLE");
		spec.addOptionSelect("ID_MIDDLE_A1", "$ID_ROOT==%ID_ROOTA", "$ID_MIDDLE!=%ID_MIDDLE_A2");
		spec.addOptionSelect("ID_MIDDLE_A1", "$ID_LEAF==%LEAF_A1_1");
		spec.addOptionSelect("ID_MIDDLE_A1", "$ID_LEAF==%LEAF_A1_2");
		
		spec.addOptionSelect("ID_MIDDLE_A2", "$ID_ROOT==%ID_ROOTA", "$ID_MIDDLE!=%ID_MIDDLE_A1");
		spec.addOptionSelect("ID_MIDDLE_A2", "$ID_LEAF==%LEAF_A2");
		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(spec);
		
		DependencyEngine engine = new DependencyEngine(specHolder, store);
		try {
			engine.requestChange("ID_LEAF", "LEAF_A1_1");
		} catch (RequestRejectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	void testCalculation() {
		DependencySpec spec = new DependencySpec("ID_TOTAL");
		spec.addCalculation("$ID_LEFT.Value + $ID_RIGHT.VALUE");
		
		spec.addValue("0", "$ID_LEFT.Value >= 1");
		spec.addValue("1", "$ID_LEFT.Value < 1");
	}
}

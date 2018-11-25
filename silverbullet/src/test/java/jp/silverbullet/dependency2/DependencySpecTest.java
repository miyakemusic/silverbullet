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
import jp.silverbullet.dependency2.LinkGenerator.LinkLevel;

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
		spec.addOptionEnabled("ID_MIDDLE_A1", DependencySpec.False, DependencySpec.Else);
		
		spec.addOptionEnabled("ID_MIDDLE_A2", DependencySpec.True, "$ID_ROOT==%ID_ROOTA");
		spec.addOptionEnabled("ID_MIDDLE_A2", DependencySpec.False, DependencySpec.Else);
		
		spec.addOptionEnabled("ID_MIDDLE_B1", DependencySpec.True, "$ID_ROOT==%ID_ROOTB");
		spec.addOptionEnabled("ID_MIDDLE_B1", DependencySpec.False, DependencySpec.Else);
		
		spec.addOptionEnabled("ID_MIDDLE_B2", DependencySpec.True, "$ID_ROOT==%ID_ROOTB");
		spec.addOptionEnabled("ID_MIDDLE_B2", DependencySpec.False, DependencySpec.Else);

		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(spec);
		
		DependencyEngine engine = new DependencyEngine(specHolder, store);
		try {
			engine.requestChange("ID_ROOT", "ID_ROOTA");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			
			assertEquals(false, cached.getProperty("ID_MIDDLE").isListElementMasked("ID_MIDDLE_A1"));	
			assertEquals(false, cached.getProperty("ID_MIDDLE").isListElementMasked("ID_MIDDLE_A2"));	
			assertEquals(true, cached.getProperty("ID_MIDDLE").isListElementMasked("ID_MIDDLE_B1"));	
			assertEquals(true, cached.getProperty("ID_MIDDLE").isListElementMasked("ID_MIDDLE_B2"));		
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		try {
			engine.requestChange("ID_ROOT", "ID_ROOTB");
			CachedPropertyStore cached = engine.getCachedPropertyStore();

			assertEquals(true, cached.getProperty("ID_MIDDLE").isListElementMasked("ID_MIDDLE_A1"));	
			assertEquals(true, cached.getProperty("ID_MIDDLE").isListElementMasked("ID_MIDDLE_A2"));	
			assertEquals(false, cached.getProperty("ID_MIDDLE").isListElementMasked("ID_MIDDLE_B1"));	
			assertEquals(false, cached.getProperty("ID_MIDDLE").isListElementMasked("ID_MIDDLE_B2"));			
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}		
		try {
			engine.requestChange("ID_ROOT", "ID_ROOTA2");
			CachedPropertyStore cached = engine.getCachedPropertyStore();

			assertEquals(false, cached.getProperty("ID_MIDDLE").isListElementMasked("ID_MIDDLE_A1"));	
			assertEquals(true, cached.getProperty("ID_MIDDLE").isListElementMasked("ID_MIDDLE_A2"));	
			assertEquals(true, cached.getProperty("ID_MIDDLE").isListElementMasked("ID_MIDDLE_B1"));	
			assertEquals(true, cached.getProperty("ID_MIDDLE").isListElementMasked("ID_MIDDLE_B2"));			
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}			
	}

	@Test
	void testEnabled() {
		DependencySpec spec = new DependencySpec("ID_MIDDLE");
		spec.addEnable(DependencySpec.True, "$ID_ROOT==%ID_ROOTA");
		spec.addEnable(DependencySpec.False, DependencySpec.Else);
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
		store.add(createListProperty("ID_LEAF", Arrays.asList("ID_LEAF_A1_1", "ID_LEAF_A1_2", "ID_LEAF_A2"), "ID_LEAF_A1_1"));
		store.add(createListProperty("ID_MIDDLE", Arrays.asList("ID_MIDDLE_A1", "ID_MIDDLE_A2", "ID_MIDDLE_B1", "ID_MIDDLE_B2"), "ID_MIDDLE_A1"));
		store.add(createListProperty("ID_ROOT", Arrays.asList("ID_ROOTA", "ID_ROOTA2", "ID_ROOTB"), "ID_ROOTA"));

		DependencySpec spec = new DependencySpec("ID_MIDDLE");
		spec.addOptionSelect("ID_MIDDLE_A1", "$ID_ROOT==%ID_ROOTA");
		spec.addOptionSelect("ID_MIDDLE_A1", "$ID_LEAF==%ID_LEAF_A1_1");
		spec.addOptionSelect("ID_MIDDLE_A1", "$ID_LEAF==%ID_LEAF_A1_2");
		
		spec.addOptionSelect("ID_MIDDLE_A2", "$ID_ROOT==%ID_ROOTB");
		spec.addOptionSelect("ID_MIDDLE_A2", "$ID_LEAF==%ID_LEAF_A2");
		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(spec);
		
		DependencyEngine engine = new DependencyEngine(specHolder, store);
		try {
			engine.requestChange("ID_ROOT", "ID_ROOTA");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("ID_MIDDLE_A1", cached.getProperty("ID_MIDDLE").getCurrentValue());	
			
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		
		try {
			engine.requestChange("ID_LEAF", "ID_LEAF_A1_1");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("ID_MIDDLE_A1", cached.getProperty("ID_MIDDLE").getCurrentValue());	
			
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		
		try {
			engine.requestChange("ID_LEAF", "ID_LEAF_A1_2");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("ID_MIDDLE_A1", cached.getProperty("ID_MIDDLE").getCurrentValue());	
			
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		
		try {
			engine.requestChange("ID_ROOT", "ID_ROOTB");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("ID_MIDDLE_A2", cached.getProperty("ID_MIDDLE").getCurrentValue());	
			
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		
		try {
			engine.requestChange("ID_LEAF", "ID_LEAF_A2");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("ID_MIDDLE_A2", cached.getProperty("ID_MIDDLE").getCurrentValue());	
			
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testNumericValue() {
		DepPropertyStore store = createPropertyStore();
		store.add(createDoubleProperty("ID_LEFT", 0, "unit", 0, 100, 0));
		store.add(createDoubleProperty("ID_TOTAL", 0, "unit", 0, 100, 0));
		
		DependencySpec spec = new DependencySpec("ID_TOTAL");		
		spec.addValue("1", "$ID_LEFT >= 10");
		spec.addValue("0", "$ID_LEFT < 10");
		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(spec);
		
		DependencyEngine engine = new DependencyEngine(specHolder, store);
		try {
			engine.requestChange("ID_LEFT", "0");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("0", cached.getProperty("ID_LEFT").getCurrentValue());	
			
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		
		try {
			engine.requestChange("ID_LEFT", "11");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("1", cached.getProperty("ID_TOTAL").getCurrentValue());	
			
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testCalculation() {
		DepPropertyStore store = createPropertyStore();
		store.add(createDoubleProperty("ID_LEFT", 0, "unit", 0, 100, 0));
		store.add(createDoubleProperty("ID_RIGHT", 0, "unit", 0, 100, 0));
		store.add(createDoubleProperty("ID_TOTAL", 0, "unit", 0, 100, 0));
		
		DependencySpec spec = new DependencySpec("ID_TOTAL");		
		spec.addCalculation("$ID_LEFT + $ID_RIGHT");

		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(spec);
		
		DependencyEngine engine = new DependencyEngine(specHolder, store);
		try {
			store.getProperty("ID_RIGHT").setCurrentValue("20");
			engine.requestChange("ID_LEFT", "10");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("30", cached.getProperty("ID_TOTAL").getCurrentValue());	
			
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testMinMax() {
		DepPropertyStore store = createPropertyStore();
		store.add(createDoubleProperty("ID_LEFT", 0, "unit", -1000, 1000, 0));
		store.add(createDoubleProperty("ID_RIGHT", 0, "unit", -1000, 1000, 0));
		store.add(createDoubleProperty("ID_TOTAL", 0, "unit", -1000, 1000, 0));
		store.add(createListProperty("ID_MODE", Arrays.asList("ID_MODE_WIDE", "ID_MODE_NARROW"), "ID_MODE_WIDE"));
	
		DependencySpec spec = new DependencySpec("ID_LEFT");		
		spec.addMin(-100, "$ID_MODE==%ID_MODE_WIDE");
		spec.addMax( 100, "$ID_MODE==%ID_MODE_WIDE");
		
		spec.addMin(-50, "$ID_MODE==%ID_MODE_NARROW");
		spec.addMax( 50, "$ID_MODE==%ID_MODE_NARROW");

		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(spec);
		
		DependencyEngine engine = new DependencyEngine(specHolder, store);
		try {
			engine.requestChange("ID_MODE", "ID_MODE_WIDE");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("-100.0", cached.getProperty("ID_LEFT").getMin());	
			assertEquals("100.0", cached.getProperty("ID_LEFT").getMax());	
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		
		try {
			engine.requestChange("ID_MODE", "ID_MODE_NARROW");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("-50.0", cached.getProperty("ID_LEFT").getMin());	
			assertEquals("50.0", cached.getProperty("ID_LEFT").getMax());	
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void test3Layers() {
		DepPropertyStore store = createPropertyStore();
		store.add(createListProperty("ID_ROOT", Arrays.asList("ID_ROOT_A", "ID_ROOT_B", "ID_ROOT_C"), "ID_ROOT_A"));
		store.add(createListProperty("ID_MIDDLE", Arrays.asList("ID_MIDDLE_A", "ID_MIDDLE_B", "ID_MIDDLE_C"), "ID_MIDDLE_A"));
		store.add(createListProperty("ID_LEAF", Arrays.asList("ID_LEAF_A", "ID_LEAF_B", "ID_LEAF_C"), "ID_LEAF_A"));
		
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
		
		DependencyEngine engine = new DependencyEngine(specHolder, store);
		try {
			engine.requestChange("ID_ROOT", "ID_ROOT_B");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals(true, cached.getProperty("ID_LEAF").getListMask().get("ID_LEAF_A"));	
			assertEquals(false, cached.getProperty("ID_LEAF").getListMask().get("ID_LEAF_B"));	
			assertEquals(true, cached.getProperty("ID_LEAF").getListMask().get("ID_LEAF_C"));	
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testLoop() {
		DepPropertyStore store = createPropertyStore();
		store.add(createListProperty("ID_BAND", Arrays.asList("ID_BAND_AUTO", "ID_BAND_MANUAL"), "ID_BAND_AUTO"));
		store.add(createDoubleProperty("ID_VALUE", 0, "unit", -1000, 1000, 0));
		
		DependencySpec specBand = new DependencySpec("ID_BAND");
		specBand.addOptionSelect("ID_BAND_MANUAL", "$ID_VALUE==$ID_VALUE");

		DependencySpec specValue = new DependencySpec("ID_VALUE");
		specValue.addValue("1234", "$ID_BAND==%ID_BAND_AUTO");
		
		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(specBand);
		specHolder.addSpec(specValue);
		
		DependencyEngine engine = new DependencyEngine(specHolder, store);
		try {
			engine.requestChange("ID_VALUE", "10");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("ID_BAND_MANUAL", cached.getProperty("ID_BAND").getCurrentValue());	
			assertEquals("10", cached.getProperty("ID_VALUE").getCurrentValue());	
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		
		try {
			engine.requestChange("ID_BAND", "ID_BAND_AUTO");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("1234", cached.getProperty("ID_VALUE").getCurrentValue());	
			assertEquals("ID_BAND_AUTO", cached.getProperty("ID_BAND").getCurrentValue());	

		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		
		DependencySpecAnalyzer analyzer = new DependencySpecAnalyzer(specHolder);
		List<GenericLink> links = analyzer.getLinkGenerator().generateLinks(LinkLevel.Detail);
		for (GenericLink link : links) {
			System.out.println(link.getFrom() + " " + link.getTo() + " " + link.getType());
		}
		
		for (Path path : analyzer.getLinkGenerator().getWarningPaths()) {
			System.out.println(path.getText());
		}
	}
	
	@Test
	void testOptionSelectWithCondition() {
		DepPropertyStore store = createPropertyStore();
		store.add(createListProperty("ID_MODE", Arrays.asList("ID_MODE_A", "ID_MODE_B"), "ID_MODE_A"));
		store.add(createListProperty("ID_LEAF", Arrays.asList("LEAF_A1_1", "LEAF_A1_2", "LEAF_A2"), "LEAF_A1_1"));
		store.add(createListProperty("ID_MIDDLE", Arrays.asList("ID_MIDDLE_A1", "ID_MIDDLE_A2", "ID_MIDDLE_B1", "ID_MIDDLE_B2"), "ID_MIDDLE_A1"));
		store.add(createListProperty("ID_ROOT", Arrays.asList("ID_ROOTA", "ID_ROOTA2", "ID_ROOTB"), "ID_ROOTA"));

		DependencySpec spec = new DependencySpec("ID_MIDDLE");
		spec.addOptionSelect("ID_MIDDLE_A1", "$ID_ROOT==%ID_ROOTA", "$ID_MODE==%ID_MODE_A");
		spec.addOptionSelect("ID_MIDDLE_A1", "$ID_LEAF==%LEAF_A1_1");
		spec.addOptionSelect("ID_MIDDLE_A1", "$ID_LEAF==%LEAF_A1_2");
		
		spec.addOptionSelect("ID_MIDDLE_A2", "$ID_ROOT==%ID_ROOTA", "$ID_MODE==%ID_MODE_B");
		spec.addOptionSelect("ID_MIDDLE_A2", "$ID_LEAF==%LEAF_A2");
		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(spec);
		
		DependencyEngine engine = new DependencyEngine(specHolder, store);
		try {
			store.getProperty("ID_MIDDLE").setCurrentValue("ID_MIDDLE_A2");
			store.getProperty("ID_MODE").setCurrentValue("ID_MODE_A");
			engine.requestChange("ID_ROOT", "ID_ROOTA");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("ID_MIDDLE_A1", cached.getProperty("ID_MIDDLE").getCurrentValue());	

		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		
		try {
			store.getProperty("ID_MIDDLE").setCurrentValue("ID_MIDDLE_A2");
			store.getProperty("ID_MODE").setCurrentValue("ID_MODE_B");
			engine.requestChange("ID_ROOT", "ID_ROOTA");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("ID_MIDDLE_A2", cached.getProperty("ID_MIDDLE").getCurrentValue());	

		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}

}

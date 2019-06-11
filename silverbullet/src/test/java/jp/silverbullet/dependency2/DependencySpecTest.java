package jp.silverbullet.dependency2;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.dependency2.CommitListener.Reply;
import jp.silverbullet.dependency2.LinkGenerator.LinkLevel;

public class DependencySpecTest {
		
	@Test
	public void testStrongWeakMinMax() {
		PropertyStoreForTest store = new PropertyStoreForTest();
		store.addDoubleProperty("ID_VALUE", 0, "unit", -1000, 1000, 0);
		store.addDoubleProperty("ID_VALUE_STRONG", 0, "unit", -1000, 1000, 0);
		store.addListProperty("ID_STRONG", Arrays.asList("ID_STRONG_A", "ID_STRONG_B"), "ID_STRONG_A");
		
		DependencySpec specValue = new DependencySpec("ID_VALUE");
		specValue.addMin(-10, "$ID_STRONG==%ID_STRONG_A").addMax(10, "$ID_STRONG==%ID_STRONG_A");

		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(specValue);
		
		DependencyEngine engine = createDependencyEngine(store, specHolder);
		
		// ID_STRONG is stronger than ID_VALUE
		specHolder.setPriority("ID_VALUE", 10);
		specHolder.setPriority("ID_STRONG", 11);
		try {
			store.getProperty("ID_VALUE").setCurrentValue("-100");
			engine.requestChange("ID_STRONG", "ID_STRONG_A");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("-10", cached.getProperty("ID_VALUE").getCurrentValue());
		} catch (RequestRejectedException e) {
		}
		try {
			store.getProperty("ID_VALUE").setCurrentValue("100");
			engine.requestChange("ID_STRONG", "ID_STRONG_A");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("10", cached.getProperty("ID_VALUE").getCurrentValue());
		} catch (RequestRejectedException e) {
		}
		
		// ID_VALUE is stronger than ID_STRING
		specHolder.setPriority("ID_VALUE", 13);
		specHolder.setPriority("ID_STRONG", 11);
		{
			boolean rejected = false;
			try {				
				store.getProperty("ID_VALUE").setCurrentValue("-100");
				engine.requestChange("ID_STRONG", "ID_STRONG_A");
			} catch (RequestRejectedException e) {
				rejected = true;
			}
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals(true, rejected);
			assertEquals("-100", cached.getProperty("ID_VALUE").getCurrentValue());
		}
		{
			boolean rejected = false;
			try {				
				store.getProperty("ID_VALUE").setCurrentValue("100");
				engine.requestChange("ID_STRONG", "ID_STRONG_A");
			} catch (RequestRejectedException e) {
				rejected = true;
			}
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals(true, rejected);
			assertEquals("100", cached.getProperty("ID_VALUE").getCurrentValue());
		}
	}

	public DependencyEngine createDependencyEngine(PropertyStoreForTest store, DependencySpecHolder specHolder) {
		DependencyEngine engine = new DependencyEngine(store) {
			@Override
			protected DependencySpecHolder getSpecHolder() {
				return specHolder;
			}
		};
		return engine;
	}
	
	@Test
	public void testOptionEnabled() {
		PropertyStoreForTest store = new PropertyStoreForTest();
		store.addListProperty("ID_MIDDLE", Arrays.asList("ID_MIDDLE_A1", "ID_MIDDLE_A2", "ID_MIDDLE_B1", "ID_MIDDLE_B2"), "ID_MIDDLE_A1");
		store.addListProperty("ID_ROOT", Arrays.asList("ID_ROOTA", "ID_ROOTA2", "ID_ROOTB"), "ID_ROOTA");
		
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
		
		DependencyEngine engine = createDependencyEngine(store, specHolder);
		try {
			engine.requestChange("ID_ROOT", "ID_ROOTA");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			
			assertEquals(false, cached.getProperty("ID_MIDDLE").isOptionDisabled("ID_MIDDLE_A1"));	
			assertEquals(false, cached.getProperty("ID_MIDDLE").isOptionDisabled("ID_MIDDLE_A2"));	
			assertEquals(true, cached.getProperty("ID_MIDDLE").isOptionDisabled("ID_MIDDLE_B1"));	
			assertEquals(true, cached.getProperty("ID_MIDDLE").isOptionDisabled("ID_MIDDLE_B2"));		
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		try {
			engine.requestChange("ID_ROOT", "ID_ROOTB");
			CachedPropertyStore cached = engine.getCachedPropertyStore();

			assertEquals(true, cached.getProperty("ID_MIDDLE").isOptionDisabled("ID_MIDDLE_A1"));	
			assertEquals(true, cached.getProperty("ID_MIDDLE").isOptionDisabled("ID_MIDDLE_A2"));	
			assertEquals(false, cached.getProperty("ID_MIDDLE").isOptionDisabled("ID_MIDDLE_B1"));	
			assertEquals(false, cached.getProperty("ID_MIDDLE").isOptionDisabled("ID_MIDDLE_B2"));	
			
			// ID_MIDDLE_A1 can no more be selected
			assertEquals("ID_MIDDLE_B1", cached.getProperty("ID_MIDDLE").getCurrentValue());
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}		
		try {
			engine.requestChange("ID_ROOT", "ID_ROOTA2");
			CachedPropertyStore cached = engine.getCachedPropertyStore();

			assertEquals(false, cached.getProperty("ID_MIDDLE").isOptionDisabled("ID_MIDDLE_A1"));	
			assertEquals(true, cached.getProperty("ID_MIDDLE").isOptionDisabled("ID_MIDDLE_A2"));	
			assertEquals(true, cached.getProperty("ID_MIDDLE").isOptionDisabled("ID_MIDDLE_B1"));	
			assertEquals(true, cached.getProperty("ID_MIDDLE").isOptionDisabled("ID_MIDDLE_B2"));			
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}		
			
	}

	@Test
	public void testEnabled() {
		DependencySpec spec = new DependencySpec("ID_MIDDLE");
		spec.addEnable(DependencySpec.True, "$ID_ROOT==%ID_ROOTA");
		spec.addEnable(DependencySpec.False, DependencySpec.Else);
		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(spec);
		
		PropertyStoreForTest store = new PropertyStoreForTest();
		store.addListProperty("ID_MIDDLE", Arrays.asList("ID_MIDDLE_A1", "ID_MIDDLE_A2", "ID_MIDDLE_B1", "ID_MIDDLE_B2"), "ID_MIDDLE_A1");
		store.addListProperty("ID_ROOT", Arrays.asList("ID_ROOTA", "ID_ROOTA2", "ID_ROOTB"), "ID_ROOTA");

				DependencyEngine engine = createDependencyEngine(store, specHolder);
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
	public void testOptionSelect() {
		PropertyStoreForTest store = new PropertyStoreForTest();
		store.addListProperty("ID_LEAF", Arrays.asList("ID_LEAF_A1_1", "ID_LEAF_A1_2", "ID_LEAF_A2"), "ID_LEAF_A1_1");
		store.addListProperty("ID_MIDDLE", Arrays.asList("ID_MIDDLE_A1", "ID_MIDDLE_A2", "ID_MIDDLE_B1", "ID_MIDDLE_B2"), "ID_MIDDLE_A1");
		store.addListProperty("ID_ROOT", Arrays.asList("ID_ROOTA", "ID_ROOTA2", "ID_ROOTB"), "ID_ROOTA");

		DependencySpec spec = new DependencySpec("ID_MIDDLE");
		spec.addOptionSelect("ID_MIDDLE_A1", "$ID_ROOT==%ID_ROOTA");
		spec.addOptionSelect("ID_MIDDLE_A1", "$ID_LEAF==%ID_LEAF_A1_1");
		spec.addOptionSelect("ID_MIDDLE_A1", "$ID_LEAF==%ID_LEAF_A1_2");
		
		spec.addOptionSelect("ID_MIDDLE_A2", "$ID_ROOT==%ID_ROOTB");
		spec.addOptionSelect("ID_MIDDLE_A2", "$ID_LEAF==%ID_LEAF_A2");
		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(spec);
		
				DependencyEngine engine = createDependencyEngine(store, specHolder);
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
	public void testNumericValue() {
		PropertyStoreForTest store = new PropertyStoreForTest();
		store.addDoubleProperty("ID_LEFT", 0, "unit", 0, 100, 0);
		store.addDoubleProperty("ID_TOTAL", 0, "unit", 0, 100, 0);
		
		DependencySpec spec = new DependencySpec("ID_TOTAL");		
		spec.addValue("1", "$ID_LEFT >= 10");
		spec.addValue("0", "$ID_LEFT < 10");
		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(spec);
		
				DependencyEngine engine = createDependencyEngine(store, specHolder);
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
	public void testCalculation() {
		PropertyStoreForTest store = new PropertyStoreForTest();
		store.addDoubleProperty("ID_LEFT", 0, "unit", 0, 100, 0);
		store.addDoubleProperty("ID_RIGHT", 0, "unit", 0, 100, 0);
		store.addDoubleProperty("ID_TOTAL", 0, "unit", 0, 100, 0);
		
		DependencySpec spec = new DependencySpec("ID_TOTAL");		
		spec.addCalculation("$ID_LEFT + $ID_RIGHT");

		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(spec);
		
				DependencyEngine engine = createDependencyEngine(store, specHolder);
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
	public void testCalculationComplex() {
		PropertyStoreForTest store = new PropertyStoreForTest();
		store.addDoubleProperty("ID_MIN", 0, "unit", -1000, 1000, 0);
		store.addDoubleProperty("ID_MAX", 100, "unit", -1000, 1000, 0);
		store.addDoubleProperty("ID_AVERAGE", 50, "unit", -1000, 1000, 0);
		store.addDoubleProperty("ID_RANGE", 100, "unit", -1000, 1000, 0);
		
		DependencySpec specMin = new DependencySpec("ID_MIN");		
		specMin.addCalculation("$ID_AVERAGE - $ID_RANGE/2");

		DependencySpec specMax = new DependencySpec("ID_MAX");		
		specMax.addCalculation("$ID_AVERAGE + $ID_RANGE/2");

		DependencySpec specAvg = new DependencySpec("ID_AVERAGE");		
		specAvg.addCalculation("($ID_MAX + $ID_MIN)/2");
		
		DependencySpec specRange = new DependencySpec("ID_RANGE");		
		specRange.addCalculation("$ID_MAX - $ID_MIN");
		
		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(specMin);
		specHolder.addSpec(specMax);
		specHolder.addSpec(specAvg);
		specHolder.addSpec(specRange);
		
				DependencyEngine engine = createDependencyEngine(store, specHolder);
		try {
			engine.requestChange("ID_AVERAGE", "100");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("50", cached.getProperty("ID_MIN").getCurrentValue());	
			assertEquals("150", cached.getProperty("ID_MAX").getCurrentValue());	
			assertEquals("100", cached.getProperty("ID_AVERAGE").getCurrentValue());	
			assertEquals("100", cached.getProperty("ID_RANGE").getCurrentValue());	
			
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testMinMax() {
		PropertyStoreForTest store = new PropertyStoreForTest();
		store.addDoubleProperty("ID_LEFT", 0, "unit", -1000, 1000, 1);
		store.addDoubleProperty("ID_RIGHT", 0, "unit", -1000, 1000, 1);
		store.addDoubleProperty("ID_TOTAL", 0, "unit", -1000, 1000, 1);
		store.addListProperty("ID_MODE", Arrays.asList("ID_MODE_WIDE", "ID_MODE_NARROW"), "ID_MODE_WIDE");
	
		DependencySpec spec = new DependencySpec("ID_LEFT");		
		spec.addMin(-100, "$ID_MODE==%ID_MODE_WIDE");
		spec.addMax( 100, "$ID_MODE==%ID_MODE_WIDE");
		
		spec.addMin(-50, "$ID_MODE==%ID_MODE_NARROW");
		spec.addMax( 50, "$ID_MODE==%ID_MODE_NARROW");

		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(spec);
		
				DependencyEngine engine = createDependencyEngine(store, specHolder);
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
		
		// input value over max
		{
			boolean rejected = false;
			try {
				store.getProperty("ID_LEFT").setCurrentValue("10");
				engine.requestChanges(Arrays.asList(new IdValue("ID_MODE", "ID_MODE_NARROW"), new IdValue("ID_LEFT", "200")));
			} catch (RequestRejectedException e) {
				rejected = true;
			}
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("10.0", cached.getProperty("ID_LEFT").getCurrentValue());	
			assertEquals(true, rejected);
		}
		
		// input value under min
		{
			boolean rejected = false;
			try {
				store.getProperty("ID_LEFT").setCurrentValue("10");
				engine.requestChanges(Arrays.asList(new IdValue("ID_MODE", "ID_MODE_NARROW"), new IdValue("ID_LEFT", "-200")));
			} catch (RequestRejectedException e) {
				rejected = true;
			}
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("10.0", cached.getProperty("ID_LEFT").getCurrentValue());	
			assertEquals(true, rejected);
		}
		
		// Current value became lager than max value becase max is changed.
		try {
			engine.requestChanges(Arrays.asList(new IdValue("ID_MODE", "ID_MODE_WIDE"), new IdValue("ID_LEFT", "70"), new IdValue("ID_MODE", "ID_MODE_NARROW")));
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("50.0", cached.getProperty("ID_LEFT").getCurrentValue());	
		} catch (RequestRejectedException e) {
		//	e.printStackTrace();
		}
	}
	
	@Test
	public void test3Layers() {
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
		
				DependencyEngine engine = createDependencyEngine(store, specHolder);
		try {
			engine.requestChange("ID_ROOT", "ID_ROOT_B");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals(true, cached.getProperty("ID_LEAF").isOptionDisabled("ID_LEAF_A"));	
			assertEquals(false, cached.getProperty("ID_LEAF").isOptionDisabled("ID_LEAF_B"));	
			assertEquals(true, cached.getProperty("ID_LEAF").isOptionDisabled("ID_LEAF_C"));	
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		
		{
			ObjectMapper mapper = new ObjectMapper();
			try {
				String s = mapper.writeValueAsString(specHolder);
				Files.write(Paths.get("dependency.json"), Arrays.asList(s));
				DependencySpecHolder obj = mapper.readValue(new File("dependency.json"), DependencySpecHolder.class);
				
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void testLoop() {
		PropertyStoreForTest store = new PropertyStoreForTest();
		store.addListProperty("ID_BAND", Arrays.asList("ID_BAND_AUTO", "ID_BAND_MANUAL"), "ID_BAND_AUTO");
		store.addDoubleProperty("ID_VALUE", 0, "unit", -1000, 1000, 0);
		
		DependencySpec specBand = new DependencySpec("ID_BAND");
		specBand.addOptionSelect("ID_BAND_MANUAL", "$ID_VALUE==$ID_VALUE");

		DependencySpec specValue = new DependencySpec("ID_VALUE");
		specValue.addValue("123", "$ID_BAND==%ID_BAND_AUTO");
		
		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(specBand);
		specHolder.addSpec(specValue);
		
				DependencyEngine engine = createDependencyEngine(store, specHolder);
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
			assertEquals("123", cached.getProperty("ID_VALUE").getCurrentValue());	
			assertEquals("ID_BAND_AUTO", cached.getProperty("ID_BAND").getCurrentValue());	

		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		
		DependencySpecAnalyzer analyzer = new DependencySpecAnalyzer(specHolder);
		List<GenericLink> links = analyzer.getLinkGenerator().generateLinks(LinkLevel.Detail).getLinks();
//		for (GenericLink link : links) {
//			System.out.println(link.getFrom() + " " + link.getTo() + " " + link.getType());
//		}
//		
//		for (Path path : analyzer.getLinkGenerator().getWarningPaths()) {
//			System.out.println(path.getText());
//		}
	}
	
	@Test
	public void testOptionSelectWithCondition() {
		PropertyStoreForTest store = new PropertyStoreForTest();
		store.addListProperty("ID_MODE", Arrays.asList("ID_MODE_A", "ID_MODE_B"), "ID_MODE_A");
		store.addListProperty("ID_LEAF", Arrays.asList("ID_LEAF_A1_1", "ID_LEAF_A1_2", "ID_LEAF_A2"), "ID_LEAF_A1_1");
		store.addListProperty("ID_MIDDLE", Arrays.asList("ID_MIDDLE_A1", "ID_MIDDLE_A2", "ID_MIDDLE_B1", "ID_MIDDLE_B2"), "ID_MIDDLE_A1");
		store.addListProperty("ID_ROOT", Arrays.asList("ID_ROOTA", "ID_ROOTA2", "ID_ROOTB"), "ID_ROOTA");

		DependencySpec spec = new DependencySpec("ID_MIDDLE");
		spec.addOptionSelect("ID_MIDDLE_A1", "$ID_ROOT==%ID_ROOTA", "$ID_MODE==%ID_MODE_A");
		spec.addOptionSelect("ID_MIDDLE_A1", "$ID_LEAF==%ID_LEAF_A1_1");
		spec.addOptionSelect("ID_MIDDLE_A1", "$ID_LEAF==%ID_LEAF_A1_2");
		
		spec.addOptionSelect("ID_MIDDLE_A2", "$ID_ROOT==%ID_ROOTA", "$ID_MODE==%ID_MODE_B");
		spec.addOptionSelect("ID_MIDDLE_A2", "$ID_LEAF==%ID_LEAF_A2");
		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(spec);
		
				DependencyEngine engine = createDependencyEngine(store, specHolder);
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
	
	@Test
	public void testStrongWeakList() {
		PropertyStoreForTest store = new PropertyStoreForTest();
		store.addListProperty("ID_STRONG", Arrays.asList("ID_STRONG_A", "ID_STRONG_B"), "ID_STRONG_A");
		store.addListProperty("ID_WEAK", Arrays.asList("ID_WEAK_A", "ID_WEAK_B"), "ID_WEAK_A");

		DependencySpec specStrong = new DependencySpec("ID_STRONG");
		specStrong.addOptionEnabled("ID_STRONG_A", DependencySpec.True, "$ID_WEAK==%ID_WEAK_A");
		specStrong.addOptionEnabled("ID_STRONG_A", DependencySpec.False, DependencySpec.Else);
		
		specStrong.addOptionEnabled("ID_STRONG_B", DependencySpec.True, "$ID_WEAK==%ID_WEAK_B");
		specStrong.addOptionEnabled("ID_STRONG_B", DependencySpec.False, DependencySpec.Else);
		
		DependencySpec specWeak = new DependencySpec("ID_WEAK");
		specWeak.addOptionEnabled("ID_WEAK_A", DependencySpec.True, "$ID_STRONG==%ID_STRONG_A");
		specWeak.addOptionEnabled("ID_WEAK_A", DependencySpec.False, DependencySpec.Else);
		specWeak.addOptionEnabled("ID_WEAK_B", DependencySpec.True, "$ID_STRONG==%ID_STRONG_B");
		specWeak.addOptionEnabled("ID_WEAK_B", DependencySpec.False, DependencySpec.Else);
		
		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(specStrong);
		specHolder.addSpec(specWeak);
		
		specHolder.setPriority("ID_WEAK", 99);
		specHolder.setPriority("ID_STRONG", 100);
		
				DependencyEngine engine = createDependencyEngine(store, specHolder);
		try {
			store.getProperty("ID_WEAK").setCurrentValue("ID_WEAK_A");
			engine.requestChange("ID_STRONG", "ID_STRONG_B");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			//assertEquals(true, cached.getProperty("ID_WEAK").isListElementMasked("ID_WEAK_A"));	
			assertEquals("ID_WEAK_B", cached.getProperty("ID_WEAK").getCurrentValue());
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		
		{
			boolean rejected = false;
			try {
				store.getProperty("ID_STRONG").setCurrentValue("ID_STRONG_A");
				engine.requestChange("ID_WEAK", "ID_WEAK_B");
			} catch (RequestRejectedException e) {
				rejected = true;
			}
			assertEquals(true, rejected);
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("ID_STRONG_A", cached.getProperty("ID_STRONG").getCurrentValue());
			
		}
	}
	
	@Test
	public void testCreateFile() {
		PropertyStoreForTest store = new PropertyStoreForTest();
		store.addDoubleProperty("ID_VALUE", 0, "unit", -1000, 1000, 0);
		store.addDoubleProperty("ID_MIN", 0, "unit", -1000, 1000, 0);
		store.addDoubleProperty("ID_MAX", 0, "unit", -1000, 1000, 0);
		store.addListProperty("ID_STRONG", Arrays.asList("ID_STRONG_A", "ID_STRONG_B"), "ID_STRONG_A");
		store.addListProperty("ID_MODE", Arrays.asList("ID_MODE_A", "ID_MODE_B"), "ID_MODE_A");
		
		DependencySpec specValue = new DependencySpec("ID_VALUE");
		specValue.addMin(-10, "$ID_STRONG==%ID_STRONG_A", "$ID_MODE==%ID_MODE_A");
		specValue.addMax(10, "$ID_STRONG==%ID_STRONG_B");
		specValue.addEnable(DependencySpec.True, "$ID_STRONG==%ID_STRONG_A");
		specValue.addEnable(DependencySpec.False, DependencySpec.Else);
		specValue.addCalculation("$ID_MIN + $ID_MAX");
		DependencySpec specStrong = new DependencySpec("ID_STRONG");
		specStrong.addEnable(DependencySpec.True, "$ID_MODE==%ID_MODE_A");
		specStrong.addEnable(DependencySpec.False, DependencySpec.Else);
		specStrong.addValue("%ID_STRONG_B", "$ID_MIN > 10");
		specStrong.addOptionEnabled("ID_STRONG_A", DependencySpec.True, "$ID_MODE==%ID_MODE_A");
		specStrong.addOptionEnabled("ID_STRONG_A", DependencySpec.False, DependencySpec.Else);
		
		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(specValue);
		specHolder.addSpec(specStrong);
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			String s = mapper.writeValueAsString(specHolder);
			Files.write(Paths.get("sample.json"), Arrays.asList(s));			
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void complexTest() {
		PropertyStoreForTest store = new PropertyStoreForTest();
		store.addListProperty("ID_A", Arrays.asList("ID_A_1", "ID_A_2", "ID_A_3", "ID_A_4", "ID_A_5"), "ID_A_1");
		store.addListProperty("ID_MODE", Arrays.asList("ID_MODE_A", "ID_MODE_B"), "ID_MODE_B");
		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.newSpec("ID_A")
			.addValue("ID_A_1", "$ID_MODE==%ID_MODE_A")
			.addValue("ID_A_5", "$ID_MODE==%ID_MODE_A");
//			.addOptionEnabled("ID_STRONG_A", DependencySpec.True, "$ID_MODE==%ID_MODE_A")
//			.addOptionEnabled("ID_STRONG_A", DependencySpec.True, "$ID_MODE==%ID_MODE_A")
//			.addOptionEnabled("ID_STRONG_B", DependencySpec.False, DependencySpec.Else);

				DependencyEngine engine = createDependencyEngine(store, specHolder);

		// if two candidates exist, a close value to current value will be selected.
		
		// if current is ID_A_2 ID_A_1 should be selected. (should not be selected ID_A_5
		try {
			store.getProperty("ID_A").setCurrentValue("ID_A_2");
			engine.requestChange("ID_MODE", "ID_MODE_A");
	//		CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("ID_A_1", store.getProperty("ID_A").getCurrentValue());	
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		// if current is ID_A_4 ID_A_1 should be selected. (should not be selected ID_A_1
		try {
			store.getProperty("ID_A").setCurrentValue("ID_A_4");
			engine.requestChange("ID_MODE", "ID_MODE_A");
//			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("ID_A_5", store.getProperty("ID_A").getCurrentValue());	
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		
		// Commit is accepted
		engine.setCommitListener(new CommitListener() {
			@Override
			public Reply confirm(String message) {
				return Reply.Accept;
			}
		});
		try {
			engine.requestChange("ID_A", "ID_A_2");
			assertEquals("ID_A_2", store.getProperty("ID_A").getCurrentValue());			
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		
		// Commit is rejected
		engine.setCommitListener(new CommitListener() {
			@Override
			public Reply confirm(String message) {
				return Reply.Reject;
			}
		});
		try {
			store.getProperty("ID_A").setCurrentValue("ID_A_2");
			engine.requestChange("ID_A", "ID_A_5");
			//CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("ID_A_2", store.getProperty("ID_A").getCurrentValue());			
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		
		// Commit is pended
		engine.setCommitListener(new CommitListener() {
			@Override
			public Reply confirm(String message) {
				return Reply.Pend;
			}
		});
		try {
			store.getProperty("ID_A").setCurrentValue("ID_A_2");
			engine.requestChange("ID_A", "ID_A_5");
			//CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("ID_A_2", store.getProperty("ID_A").getCurrentValue());	
			
			engine.setPendedReply(Reply.Accept);
			assertEquals("ID_A_5", store.getProperty("ID_A").getCurrentValue());
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test() {
		PropertyStoreForTest store = new PropertyStoreForTest();
		store.addListProperty("ID_DIST", Arrays.asList("ID_DIST_1", "ID_DIST_2", "ID_DIST_3", "ID_DIST_4", "ID_DIST_5", "ID_DIST_6"), "ID_DIST_1");
		store.addListProperty("ID_PULSE", Arrays.asList("ID_PULSE_1", "ID_PULSE_2", "ID_PULSE_3", "ID_PULSE_4", "ID_PULSE_5", "ID_PULSE_6"), "ID_PULSE_1");
		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.newSpec("ID_PULSE")
			.addOptionEnabled("ID_PULSE_1", DependencySpec.True, "$ID_DIST==%ID_DIST_1")
			.addOptionEnabled("ID_PULSE_1", DependencySpec.False, DependencySpec.Else)
			.addOptionEnabled("ID_PULSE_2", DependencySpec.True, "$ID_DIST==%ID_DIST_1")
			.addOptionEnabled("ID_PULSE_2", DependencySpec.True, "$ID_DIST==%ID_DIST_2")
			.addOptionEnabled("ID_PULSE_2", DependencySpec.False, DependencySpec.Else)
			.addOptionEnabled("ID_PULSE_3", DependencySpec.True, "$ID_DIST==%ID_DIST_1")
			.addOptionEnabled("ID_PULSE_3", DependencySpec.True, "$ID_DIST==%ID_DIST_2")
			.addOptionEnabled("ID_PULSE_3", DependencySpec.True, "$ID_DIST==%ID_DIST_3")
			.addOptionEnabled("ID_PULSE_3", DependencySpec.False, DependencySpec.Else)	
			.addOptionEnabled("ID_PULSE_4", DependencySpec.True, "$ID_DIST==%ID_DIST_2")
			.addOptionEnabled("ID_PULSE_4", DependencySpec.True, "$ID_DIST==%ID_DIST_3")
			.addOptionEnabled("ID_PULSE_4", DependencySpec.True, "$ID_DIST==%ID_DIST_4")
			.addOptionEnabled("ID_PULSE_4", DependencySpec.False, DependencySpec.Else)	
			.addOptionEnabled("ID_PULSE_5", DependencySpec.True, "$ID_DIST==%ID_DIST_3")
			.addOptionEnabled("ID_PULSE_5", DependencySpec.True, "$ID_DIST==%ID_DIST_4")
			.addOptionEnabled("ID_PULSE_5", DependencySpec.True, "$ID_DIST==%ID_DIST_5")
			.addOptionEnabled("ID_PULSE_5", DependencySpec.False, DependencySpec.Else)
			.addOptionEnabled("ID_PULSE_6", DependencySpec.True, "$ID_DIST==%ID_DIST_4")
			.addOptionEnabled("ID_PULSE_6", DependencySpec.True, "$ID_DIST==%ID_DIST_5")
			.addOptionEnabled("ID_PULSE_6", DependencySpec.True, "$ID_DIST==%ID_DIST_6")
			.addOptionEnabled("ID_PULSE_6", DependencySpec.False, DependencySpec.Else);		
		DependencyEngine engine = createDependencyEngine(store, specHolder);
		
		try {
			engine.requestChange("ID_DIST", "ID_DIST_1");
			assertEquals("ID_PULSE_1", store.getProperty("ID_PULSE").getCurrentValue());
			
			engine.requestChange("ID_DIST", "ID_DIST_2");
			assertEquals("ID_PULSE_2", store.getProperty("ID_PULSE").getCurrentValue());
			
			engine.requestChange("ID_DIST", "ID_DIST_3");
			assertEquals("ID_PULSE_3", store.getProperty("ID_PULSE").getCurrentValue());
			
			engine.requestChange("ID_DIST", "ID_DIST_4");
			assertEquals("ID_PULSE_4", store.getProperty("ID_PULSE").getCurrentValue());
			
			engine.requestChange("ID_DIST", "ID_DIST_5");
			assertEquals("ID_PULSE_5", store.getProperty("ID_PULSE").getCurrentValue());
			
			engine.requestChange("ID_DIST", "ID_DIST_6");
			assertEquals("ID_PULSE_6", store.getProperty("ID_PULSE").getCurrentValue());
			
			engine.requestChange("ID_DIST", "ID_DIST_5");
			assertEquals("ID_PULSE_6", store.getProperty("ID_PULSE").getCurrentValue());
			
			engine.requestChange("ID_DIST", "ID_DIST_4");
			assertEquals("ID_PULSE_6", store.getProperty("ID_PULSE").getCurrentValue());

			engine.requestChange("ID_DIST", "ID_DIST_3");
			assertEquals("ID_PULSE_5", store.getProperty("ID_PULSE").getCurrentValue());

			engine.requestChange("ID_DIST", "ID_DIST_2");
			assertEquals("ID_PULSE_4", store.getProperty("ID_PULSE").getCurrentValue());

			engine.requestChange("ID_DIST", "ID_DIST_1");
			assertEquals("ID_PULSE_3", store.getProperty("ID_PULSE").getCurrentValue());

		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testOptionEnabledByOtherCondition() {
		PropertyStoreForTest store = new PropertyStoreForTest();
		store.addListProperty("ID_CHOICE", Arrays.asList("ID_CHOICE_A", "ID_CHOICE_B", "ID_CHOICE", "ID_CHOICE_D"), "ID_CHOICE_A");
		store.addBooleanProperty("ID_OPTION1", false);
		store.addBooleanProperty("ID_OPTION2", false);
		
		DependencySpec spec = new DependencySpec("ID_CHOICE");
		spec.addOptionEnabled("ID_CHOICE_C", DependencySpec.True, "$ID_OPTION1==" + DependencySpec.True);
		spec.addOptionEnabled("ID_CHOICE_C", DependencySpec.False, "$ID_OPTION1==" + DependencySpec.False);
		
		spec.addOptionEnabled("ID_CHOICE_D", DependencySpec.True, "$ID_OPTION2==" + DependencySpec.True);
		spec.addOptionEnabled("ID_CHOICE_D", DependencySpec.False, "$ID_OPTION2==" + DependencySpec.False);

		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(spec);
		
		DependencyEngine engine = createDependencyEngine(store, specHolder);
		try {
			engine.requestChanges(Arrays.asList(
					new IdValue("ID_OPTION1", DependencySpec.True), 
					new IdValue("ID_OPTION2", DependencySpec.False)));
			
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			cached.commit();
			assertEquals(false, store.getProperty("ID_CHOICE").isOptionDisabled("ID_CHOICE_C"));
			assertEquals(true, store.getProperty("ID_CHOICE").isOptionDisabled("ID_CHOICE_D"));	
			
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		
		try {
			engine.requestChanges(Arrays.asList(
					new IdValue("ID_OPTION1", DependencySpec.False), 
					new IdValue("ID_OPTION2", DependencySpec.True)));
			
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			cached.commit();
			assertEquals(false, store.getProperty("ID_CHOICE").isOptionDisabled("ID_CHOICE_D"));	
			assertEquals(true, store.getProperty("ID_CHOICE").isOptionDisabled("ID_CHOICE_C"));
			
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		
		try {
			store.getProperty("ID_CHOICE").enableOption("ID_CHOICE_C", false);
			store.getProperty("ID_CHOICE").enableOption("ID_CHOICE_D", true);
			assertEquals(false, store.getProperty("ID_CHOICE").isOptionDisabled("ID_CHOICE_D"));	
			assertEquals(true, store.getProperty("ID_CHOICE").isOptionDisabled("ID_CHOICE_C"));
			
			
			engine.requestChanges(Arrays.asList(
					new IdValue("ID_OPTION2", DependencySpec.False)));
			
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			cached.commit();
			assertEquals(true, store.getProperty("ID_CHOICE").isOptionDisabled("ID_CHOICE_D"));	
			assertEquals(true, store.getProperty("ID_CHOICE").isOptionDisabled("ID_CHOICE_C"));
			
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}
}

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

import jp.silverbullet.dependency2.LinkGenerator.LinkLevel;

public class DependencySpecTest {
		
	@Test
	public void testStrongWeakMinMax() {
		PropertyStoreForTest store = new PropertyStoreForTest();
		store.addDoubleProperty("ID_VALUE", 0, "unit", -1000, 1000, 0);
		store.addDoubleProperty("ID_VALUE_STRONG", 0, "unit", -1000, 1000, 0);
		store.addListProperty("ID_STRONG", Arrays.asList("ID_STRONG_A", "ID_STRONG_B"), "ID_STRONG_A");
		
		DependencySpec specValue = new DependencySpec("ID_VALUE");
		specValue.addMin(-10, "$ID_STRONG==%ID_STRONG_A");

		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(specValue);
		
		DependencyEngine engine = new DependencyEngine(specHolder, store);
		try {
			specHolder.setPriority("ID_VALUE", 10);
			specHolder.setPriority("ID_STRONG", 11);
			
			store.getProperty("ID_VALUE").setCurrentValue("-100");
			engine.requestChange("ID_STRONG", "ID_STRONG_A");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("-10", cached.getProperty("ID_VALUE").getCurrentValue());
		} catch (RequestRejectedException e) {
		}
		
		{
			boolean rejected = false;
			try {
				specHolder.setPriority("ID_VALUE", 13);
				specHolder.setPriority("ID_STRONG", 11);
				
				store.getProperty("ID_VALUE").setCurrentValue("-100");
				engine.requestChange("ID_STRONG", "ID_STRONG_A");
			} catch (RequestRejectedException e) {
				rejected = true;
			}
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals(true, rejected);
			assertEquals("-100", cached.getProperty("ID_VALUE").getCurrentValue());
		}
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
			
			// ID_MIDDLE_A1 can no more be selected
			assertEquals("ID_MIDDLE_B1", cached.getProperty("ID_MIDDLE").getCurrentValue());
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
	public void testEnabled() {
		DependencySpec spec = new DependencySpec("ID_MIDDLE");
		spec.addEnable(DependencySpec.True, "$ID_ROOT==%ID_ROOTA");
		spec.addEnable(DependencySpec.False, DependencySpec.Else);
		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(spec);
		
		PropertyStoreForTest store = new PropertyStoreForTest();
		store.addListProperty("ID_MIDDLE", Arrays.asList("ID_MIDDLE_A1", "ID_MIDDLE_A2", "ID_MIDDLE_B1", "ID_MIDDLE_B2"), "ID_MIDDLE_A1");
		store.addListProperty("ID_ROOT", Arrays.asList("ID_ROOTA", "ID_ROOTA2", "ID_ROOTB"), "ID_ROOTA");

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
	public void testNumericValue() {
		PropertyStoreForTest store = new PropertyStoreForTest();
		store.addDoubleProperty("ID_LEFT", 0, "unit", 0, 100, 0);
		store.addDoubleProperty("ID_TOTAL", 0, "unit", 0, 100, 0);
		
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
	public void testCalculation() {
		PropertyStoreForTest store = new PropertyStoreForTest();
		store.addDoubleProperty("ID_LEFT", 0, "unit", 0, 100, 0);
		store.addDoubleProperty("ID_RIGHT", 0, "unit", 0, 100, 0);
		store.addDoubleProperty("ID_TOTAL", 0, "unit", 0, 100, 0);
		
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
		
		DependencyEngine engine = new DependencyEngine(specHolder, store);
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
		store.addDoubleProperty("ID_LEFT", 0, "unit", -1000, 1000, 0);
		store.addDoubleProperty("ID_RIGHT", 0, "unit", -1000, 1000, 0);
		store.addDoubleProperty("ID_TOTAL", 0, "unit", -1000, 1000, 0);
		store.addListProperty("ID_MODE", Arrays.asList("ID_MODE_WIDE", "ID_MODE_NARROW"), "ID_MODE_WIDE");
	
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
		
		// input value over max
		{
			boolean rejected = false;
			try {
				store.getProperty("ID_LEFT").setCurrentValue("10");
				engine.requestChanges(Arrays.asList(new IdValue("ID_MODE", "ID_MODE_NARROW"), new IdValue("ID_LEFT", "200")));
			} catch (RequestRejectedException e) {
			//	e.printStackTrace();
				rejected = true;
			}
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("10", cached.getProperty("ID_LEFT").getCurrentValue());	
			assertEquals(true, rejected);
		}
		
		// input value under min
		{
			boolean rejected = false;
			try {
				store.getProperty("ID_LEFT").setCurrentValue("10");
				engine.requestChanges(Arrays.asList(new IdValue("ID_MODE", "ID_MODE_NARROW"), new IdValue("ID_LEFT", "-200")));
			} catch (RequestRejectedException e) {
			//	e.printStackTrace();
				rejected = true;
			}
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("10", cached.getProperty("ID_LEFT").getCurrentValue());	
			assertEquals(true, rejected);
		}
		
		// Current value became lager than max value becase max is changed.
		try {
			engine.requestChanges(Arrays.asList(new IdValue("ID_MODE", "ID_MODE_WIDE"), new IdValue("ID_LEFT", "70"), new IdValue("ID_MODE", "ID_MODE_NARROW")));
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("50", cached.getProperty("ID_LEFT").getCurrentValue());	
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
		store.addListProperty("ID_LEAF", Arrays.asList("LEAF_A1_1", "LEAF_A1_2", "LEAF_A2"), "LEAF_A1_1");
		store.addListProperty("ID_MIDDLE", Arrays.asList("ID_MIDDLE_A1", "ID_MIDDLE_A2", "ID_MIDDLE_B1", "ID_MIDDLE_B2"), "ID_MIDDLE_A1");
		store.addListProperty("ID_ROOT", Arrays.asList("ID_ROOTA", "ID_ROOTA2", "ID_ROOTB"), "ID_ROOTA");

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
		
		DependencyEngine engine = new DependencyEngine(specHolder, store);
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
}

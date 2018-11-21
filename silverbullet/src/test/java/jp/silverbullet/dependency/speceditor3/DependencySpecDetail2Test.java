package jp.silverbullet.dependency.speceditor3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.Test;

import jp.silverbullet.SvProperty;
import jp.silverbullet.XmlPersistent;
import jp.silverbullet.dependency.CachedPropertyStore;
import jp.silverbullet.dependency.ChangedItemValue;
import jp.silverbullet.dependency.DepPropertyStore;
import jp.silverbullet.dependency.DependencyBuilder;
import jp.silverbullet.dependency.DependencyEngine;
import jp.silverbullet.dependency.DependencyExpression;
import jp.silverbullet.dependency.DependencyExpressionHolder;
import jp.silverbullet.dependency.DependencyProperty;
import jp.silverbullet.dependency.DependencySpec;
import jp.silverbullet.dependency.DependencySpecHolder;
import jp.silverbullet.dependency.DependencyTargetElement;
import jp.silverbullet.dependency.RequestRejectedException;
import jp.silverbullet.dependency.DependencyExpressionHolder.SettingDisabledBehavior;
import jp.silverbullet.dependency.alternative.AlternativeDependencyGenerator;
import jp.silverbullet.web.ui.PropertyGetter;

class DependencySpecDetail2Test {

	@Test
	void testEnable() {
		String idFirst = "ID_FIRST";
		String idSecond = "ID_SECOND";	
		String idEnableChanger = "ID_ENABLED";
		DepPropertyStore store = createPropertyStore();
		store.add(createDoubleProperty(idFirst, 100, "nm", 0, 9999, 3));
		store.add(createDoubleProperty(idSecond, 200, "nm", 0, 9999, 3));
		store.add(createDoubleProperty(idEnableChanger, 100, "nm", 0, 9999, 3));
		
		DependencySpecHolder holder = new DependencySpecHolder();
		
		{
			DependencySpec spec = new DependencySpec(idSecond);
			holder.add(spec);
			DependencyExpressionHolder value = new DependencyExpressionHolder(DependencyTargetElement.Value);
			value.addExpression().resultExpression("$"+idFirst + ".Value * 2");
			spec.add(value);	
		}
		{
			DependencySpec spec = new DependencySpec(idFirst);
			holder.add(spec);
			DependencyExpressionHolder enabled = new DependencyExpressionHolder(DependencyTargetElement.Enabled);
			enabled.addExpression().resultExpression(DependencyExpression.False).conditionExpression("$" + idEnableChanger + ".Value > 100");
			spec.add(enabled);	
		}
		
		DependencyEngine engine = createEngine(holder, store);
		
		try {
			engine.requestChange(idFirst, "200");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("400.000", cached.getProperty(idSecond).getCurrentValue());
			cached.commit();
			
			store.getProperty(idSecond).setCurrentValue("200");
			
			engine.requestChange(idEnableChanger, "300");
			cached = engine.getCachedPropertyStore();
			
			assertEquals(false, cached.getProperty(idFirst).isEnabled());
			assertEquals("200.000", cached.getProperty(idSecond).getCurrentValue());
			cached.commit();
		}
		catch (Exception e) {
			
		}
	}
	
	@Test
	void test() {	
		String idBand = "ID_OSA_BAND";
		String idBandC = "ID_OSA_BAND_C";
		String idBandManual = "ID_OSA_BAND_MANUAL";
		
		String idStartWavelength = "ID_OSA_START_WAVELENGTH";
		String idStopWavelength = "ID_OSA_STOP_WAVELENGTH";
		String idCenterWavelength = "ID_OSA_CENTER_WAVELENGTH";
		String idTestMode = "ID_OSA_TESTMODE";
		String idTestModeAuto = "ID_OSA_TESTMODE_AUTO";
		String idTestModeManual = "ID_OSA_TESTMODE_MANUAL";
		
		String idModel = "ID_OSA_MODEL";
		String idModel20A = "ID_OSA_MODEL_20A";
		String idModel21A = "ID_OSA_MODEL_21A";
		String idApplication = "ID_OSA_APPLICATION";
		String idApplicationWdm = "ID_OSA_APPLICATION_WDM";
		String idApplicationDrift = "ID_OSA_APPLICATION_DRIFT";
		String idApplicationInBand = "ID_OSA_APPLICATION_INBAND";
		
		DependencySpecHolder holder = new DependencySpecHolder();
		
		//////////BAND///////////
		DependencySpec specBand = new DependencySpec(idBand);
		holder.add(specBand);
		//// Enabled ///
		{
			DependencyExpressionHolder specDetail = new DependencyExpressionHolder(DependencyTargetElement.Enabled);
			specDetail.addExpression().resultExpression(DependencyExpression.True).conditionIdValue(idTestMode).equals().conditionSelectionId(idTestModeManual);		
			specBand.add(specDetail);
		}
	    //// Value ///
		{
			DependencyExpressionHolder specDetailBand = new DependencyExpressionHolder(DependencyTargetElement.Value);
			specDetailBand.addExpression().resultExpression("%" + idBandManual).conditionIdValue(idStartWavelength).equals().anyValue().or().conditionIdValue(idStopWavelength).equals().anyValue();
//			specDetailBand.addExpression().resultExpression("%" + idBandManual).conditionIdValue(idStopWavelength).equals().anyValue();
			specBand.add(specDetailBand);
		}
		
		////////// Start Wavelength///////////
		//// Value ////
		{
			DependencySpec spec = new DependencySpec(idStartWavelength);
			holder.add(spec);
			DependencyExpressionHolder specDetailStartW = new DependencyExpressionHolder(DependencyTargetElement.Value);
			specDetailStartW.addExpression().resultExpression("1530").conditionIdValue(idBand).equals().conditionSelectionId(idBandC);	
			specDetailStartW.addExpression().resultExpression("$" + idStopWavelength + ".Value").conditionExpression("$" + idStartWavelength + ".Value > " + "$" + idStopWavelength + ".Value");
			spec.add(specDetailStartW);
			
		}
		////////// Stop Wavelength///////////
		//// Value ////
		{
			DependencySpec spec = new DependencySpec(idStopWavelength);
			holder.add(spec);
			DependencyExpressionHolder specDetailStopW = new DependencyExpressionHolder(DependencyTargetElement.Value);
			specDetailStopW.addExpression().resultExpression("1565").conditionIdValue(idBand).equals().conditionSelectionId(idBandC);
			specDetailStopW.addExpression().resultExpression("$" + idStartWavelength + ".Value").conditionExpression("$" + idStopWavelength + ".Value < " + "$" + idStartWavelength + ".Value");
			spec.add(specDetailStopW);
		}
		
		{

			
		}
		
		//// ListItemVisibel ////
		{
			DependencySpec spec = new DependencySpec(idApplicationInBand);
			holder.add(spec);
			DependencyExpressionHolder specDetail = new DependencyExpressionHolder(DependencyTargetElement.ListItemVisible);
			specDetail.addExpression().resultExpression(DependencyExpression.True).conditionIdValue(idModel).equals().conditionSelectionId(idModel21A);
//			specDetail.addExpression().resultValue(DependencyExpression.False).conditionOther();
			spec.add(idApplicationInBand, specDetail);
			
		}

		DepPropertyStore store = createPropertyStore();
		
		store.add(createListProperty(idBand, Arrays.asList(idBandC, idBandManual), idBandC));
		store.add(createDoubleProperty(idStartWavelength, 1250, "nm", 0, 9999, 3));
		store.add(createDoubleProperty(idStopWavelength, 1650, "nm", 0, 9999, 3));
		store.add(createListProperty(idTestMode, Arrays.asList(idTestModeAuto, idTestModeManual), idTestModeAuto));
		store.add(createListProperty(idModel, Arrays.asList(idModel20A, idModel21A), idModel20A));
		store.add(createListProperty(idModel, Arrays.asList(idApplication, idApplicationWdm, idApplicationDrift, idApplicationInBand), idApplicationWdm));
		
		DependencyEngine engine = createEngine(holder, store);
		DependencyBuilder builder2 = new DependencyBuilder(idStartWavelength, holder);
		List<DependencyProperty> spec2 = builder2.getSpecs(0);
		assertTrue(builder2.getLayerCount() == 3);
		
		assertTrue(spec2.get(0).getId().equals(idBand));
		assertTrue(spec2.get(0).getElement().equals(DependencyTargetElement.Value));
		assertTrue(spec2.get(0).getCondition().equals("$ID_OSA_START_WAVELENGTH.Value==*any || $ID_OSA_STOP_WAVELENGTH.Value==*any"));
//		assertTrue(spec2.get(0).getValue().equals(idBandManual));
	
		assertTrue(spec2.get(1).getId().equals(idStopWavelength));
		assertTrue(spec2.get(1).getElement().equals(DependencyTargetElement.Value));
		assertTrue(spec2.get(1).getCondition().equals("$ID_OSA_STOP_WAVELENGTH.Value < $ID_OSA_START_WAVELENGTH.Value"));
		assertTrue(spec2.get(1).getValue().equals("$ID_OSA_START_WAVELENGTH.Value"));
				
		List<DependencyProperty> spec3 = builder2.getSpecs(1);
		
		assertTrue(spec3.get(0).getId().equals(idStopWavelength));
		assertTrue(spec3.get(0).getElement().equals(DependencyTargetElement.Value));
		assertTrue(spec3.get(0).getCondition().equals("$ID_OSA_BAND.Value==%ID_OSA_BAND_C"));
//		assertTrue(spec3.get(0).getValue().equals("1565"));
	
		assertTrue(spec3.get(1).getId().equals(idBand));
		assertTrue(spec3.get(1).getElement().equals(DependencyTargetElement.Value));
		assertTrue(spec3.get(1).getCondition().equals("$ID_OSA_START_WAVELENGTH.Value==*any || $ID_OSA_STOP_WAVELENGTH.Value==*any"));
//		assertTrue(spec3.get(1).getValue().equals("ID_OSA_BAND_MANUAL"));	
		
		assertTrue(store.getProperty(idBand).getCurrentValue().equals(idBandC));
		try {
			engine.requestChange(idStartWavelength, "1400");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertTrue(cached.getProperty(idBand).getCurrentValue().equals(idBandManual));
			assertTrue(cached.getProperty(idStartWavelength).getCurrentValue().equals("1400.000"));
			
			{
				List<ChangedItemValue> changed = cached.getChanged(idStartWavelength);
				assertEquals(DependencyTargetElement.Value, changed.get(0).getElement());
				assertEquals("1400.000", changed.get(0).getValue());
			}
			{
				List<ChangedItemValue> changed = cached.getChanged(idBand);
				assertEquals(DependencyTargetElement.Value, changed.get(0).getElement());
				assertEquals(idBandManual, changed.get(0).getValue());
			}	
			
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}

		try {
			//store.clearHistory();
			store.getProperty(idBand).setCurrentValue(idBandManual);
			engine.requestChange(idBand, idBandC);
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("1530.000", cached.getProperty(idStartWavelength).getCurrentValue());
			assertEquals("1565.000", cached.getProperty(idStopWavelength).getCurrentValue());
			assertEquals(idBandC, cached.getProperty(idBand).getCurrentValue());
		
			{
				List<ChangedItemValue> changed = cached.getChanged(idBand);
				assertEquals(DependencyTargetElement.Value, changed.get(0).getElement());
				assertEquals(idBandC, changed.get(0).getValue());
			}
			{
				List<ChangedItemValue> changed = cached.getChanged(idStartWavelength);
				assertEquals(DependencyTargetElement.Value, changed.get(0).getElement());
				assertEquals("1530.000", changed.get(0).getValue());
			}
			{
				List<ChangedItemValue> changed = cached.getChanged(idStopWavelength);
				assertEquals(DependencyTargetElement.Value, changed.get(0).getElement());
				assertEquals("1565.000", changed.get(0).getValue());
			}

		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		
//		DependencyBuilder3 builder = new DependencyBuilder3(idBand, holder);
//		assertTrue(builder.getTree().getDependencyProperty().getId().equals(idBand));
		
		XmlPersistent<DependencySpecHolder> propertyPersister = new XmlPersistent<>();
		try {
			propertyPersister.save(holder, "C:\\Projects\\dep.xml", DependencySpecHolder.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	@Test
	void testResultEquation() {
		String idStartWavelength = "ID_OSA_START_WAVELENGTH";
		String idStopWavelength = "ID_OSA_STOP_WAVELENGTH";
		String idCenterWavelength = "ID_OSA_CENTER_WAVELENGTH";
		String idSpanWavelength = "ID_OSA_SPAN_WAVELENGTH";
		
		DepPropertyStore store = createPropertyStore();
		store.add(createDoubleProperty(idStartWavelength, 1250, "nm", 0, 9999, 3));
		store.add(createDoubleProperty(idStopWavelength, 1650, "nm", 0, 9999, 3));
		store.add(createDoubleProperty(idCenterWavelength, 1450, "nm", 0, 9999, 3));
		store.add(createDoubleProperty(idSpanWavelength, 400, "nm", 0, 9999, 3));
		
		DependencySpecHolder holder = new DependencySpecHolder();
				
		////////// Start Wavelength///////////
		{
			DependencySpec spec = new DependencySpec(idStartWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("$"+idCenterWavelength+".Value" + "-" + "$"+idSpanWavelength+".Value/2");
			spec.add(detail);
		}
		////////// Stop Wavelength///////////
		{
			DependencySpec spec = new DependencySpec(idStopWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("$"+idCenterWavelength+".Value" + "+" + "$"+idSpanWavelength+".Value/2");
			spec.add(detail);
		}
		////////// Center Wavelength///////////
		{
			DependencySpec spec = new DependencySpec(idCenterWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("($"+idStartWavelength+".Value" + "+" + "$"+idStopWavelength+".Value)/2");
			spec.add(detail);
		}
		////////// Span Wavelength///////////
		{
			DependencySpec spec = new DependencySpec(idSpanWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("$"+idStopWavelength +".Value - " + "$"+idStartWavelength + ".Value");
			spec.add(detail);
		}
		DependencyEngine engine = createEngine(holder, store);
		try {
			engine.requestChange(idCenterWavelength, "1550");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertTrue(cached.getProperty(idStartWavelength).getCurrentValue().equals("1350.000"));
			assertTrue(cached.getProperty(idStopWavelength).getCurrentValue().equals("1750.000"));
			assertTrue(cached.getProperty(idSpanWavelength).getCurrentValue().equals("400.000"));
			assertTrue(cached.getProperty(idCenterWavelength).getCurrentValue().equals("1550.000"));
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testOverLimit() {
		String idStartWavelength = "ID_OSA_START_WAVELENGTH";
		String idStopWavelength = "ID_OSA_STOP_WAVELENGTH";
		
		DepPropertyStore store = createPropertyStore();
		store.add(createDoubleProperty(idStartWavelength, 1500, "nm", 1250, 1650, 3));
		store.add(createDoubleProperty(idStopWavelength, 1550, "nm", 1250, 1650, 3));
		
		DependencySpecHolder holder = new DependencySpecHolder();
		
		////////// Start Wavelength///////////
		{
			DependencySpec spec = new DependencySpec(idStartWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("$" + idStopWavelength +".Value").conditionIdValue(idStartWavelength).largerThan().conditionIdValue(idStopWavelength);
			spec.add(detail);
		}
		////////// Stop Wavelength///////////
		{
			DependencySpec spec = new DependencySpec(idStopWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("$" +idStartWavelength +".Value").conditionIdValue(idStartWavelength).largerThan().conditionIdValue(idStopWavelength);
			spec.add(detail);
		}
		
		DependencyEngine engine = createEngine(holder, store);
		try {
			assertTrue(store.getProperty(idStartWavelength).getCurrentValue().equals("1500.000"));
			assertTrue(store.getProperty(idStopWavelength).getCurrentValue().equals("1550.000"));
			engine.requestChange(idStartWavelength, "1600");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("1600.000", cached.getProperty(idStartWavelength).getCurrentValue());
			assertEquals("1600.000", cached.getProperty(idStopWavelength).getCurrentValue());
			
			engine.requestChange(idStopWavelength, "1500");
			cached = engine.getCachedPropertyStore();
			assertEquals("1500.000", cached.getProperty(idStartWavelength).getCurrentValue());
			assertEquals("1500.000", cached.getProperty(idStopWavelength).getCurrentValue());
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}
	@Test
	void testListMask() {
		String idModel = "ID_OSA_MODEL";
		String idModel20A = "ID_OSA_MODEL_20A";
		String idModel21A = "ID_OSA_MODEL_21A";
		
		String idApplication = "ID_OSA_APPLICATION";
		String idApplicationWdm = "ID_OSA_APPLICATION_WDM";
		String idApplicationDrift = "ID_OSA_APPLICATION_DRIFT";
		String idApplicationInBand = "ID_OSA_APPLICATION_INBAND";
		
		DependencySpecHolder holder = new DependencySpecHolder();
		
		CachedPropertyStore store = new CachedPropertyStore(createPropertyStore());
		store.add(createListProperty(idModel, Arrays.asList(idModel20A, idModel21A), idModel20A));
		store.add(createListProperty(idApplication, Arrays.asList(idApplicationWdm, idApplicationDrift, idApplicationInBand), idApplicationInBand));

		{
			DependencySpec spec = new DependencySpec(idApplication);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.ListItemVisible);
			detail.addExpression().resultExpression(DependencyExpression.True).conditionIdValue(idModel).equals().conditionSelectionId(idModel21A);
			detail.addExpression().resultExpression(DependencyExpression.False).conditionIdValue(idModel).equals().conditionSelectionId(idModel20A);
			spec.add(idApplicationInBand, detail);
		}
		
		DependencyEngine engine = createEngine(holder, store);
		try {
			engine.requestChange(idModel, idModel20A);
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			
			assertTrue(cached.getProperty(idApplication).getAvailableListDetail().size() == 2);
			assertTrue(cached.getProperty(idApplication).getAvailableListDetail().get(0).getId().equals(idApplicationWdm));
			assertTrue(cached.getProperty(idApplication).getAvailableListDetail().get(1).getId().equals(idApplicationDrift));
			assertTrue(cached.getProperty(idApplication).getCurrentValue().equals(idApplicationWdm));
			
			{
				List<ChangedItemValue> changed = cached.getChanged(idApplication);
				assertEquals(DependencyTargetElement.ListItemEnabled, changed.get(0).getElement());
				assertEquals(idApplicationInBand + ",true", changed.get(0).getValue());

				assertEquals(DependencyTargetElement.Value, changed.get(1).getElement());
				assertEquals(idApplicationWdm, changed.get(1).getValue());
			}
			
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testElse() {
		String idModel = "ID_OSA_MODEL";
		String idModel20A = "ID_OSA_MODEL_20A";
		String idModel21A = "ID_OSA_MODEL_21A";
		String idModel22A = "ID_OSA_MODEL_22A";
		String idModel23A = "ID_OSA_MODEL_23A";
		
		String idApplication = "ID_OSA_APPLICATION";
		String idApplicationWdm = "ID_OSA_APPLICATION_WDM";
		String idApplicationDrift = "ID_OSA_APPLICATION_DRIFT";
		String idApplicationInBand = "ID_OSA_APPLICATION_INBAND";
		
		DependencySpecHolder holder = new DependencySpecHolder();
		
		DepPropertyStore store = createPropertyStore();
		store.add(createListProperty(idModel, Arrays.asList(idModel20A, idModel21A, idModel22A, idModel23A), idModel20A));
		store.add(createListProperty(idApplication, Arrays.asList(idApplicationWdm, idApplicationDrift, idApplicationInBand), idApplicationInBand));

		{
			DependencySpec spec = new DependencySpec(idApplication);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.ListItemVisible);
			detail.addExpression().resultExpression(DependencyExpression.True).conditionIdValue(idModel).equals().conditionSelectionId(idModel21A).or().conditionIdValue(idModel).equals().conditionSelectionId(idModel23A);
//			detail.addExpression().resultExpression(DependencyExpression.True).conditionIdValue(idModel).equals().conditionSelectionId(idModel23A);
			detail.addExpression().resultExpression(DependencyExpression.False).conditionElse();
			spec.add(idApplicationInBand, detail);
		}
		DependencyEngine engine = createEngine(holder, store);
		try {
			// 20A
			engine.requestChange(idModel, idModel20A);
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			
			assertEquals(2, cached.getProperty(idApplication).getAvailableListDetail().size());
			assertTrue(cached.getProperty(idApplication).getAvailableListDetail().get(0).getId().equals(idApplicationWdm));
			assertTrue(cached.getProperty(idApplication).getAvailableListDetail().get(1).getId().equals(idApplicationDrift));
			assertTrue(cached.getProperty(idApplication).getCurrentValue().equals(idApplicationWdm));
			
			{
				List<ChangedItemValue> changed = cached.getChanged(idApplication);
				assertEquals(DependencyTargetElement.ListItemEnabled, changed.get(0).getElement());
				assertEquals(idApplicationInBand + ",true", changed.get(0).getValue());
			}
			{
				List<ChangedItemValue> changed = cached.getChanged(idApplication);
				assertEquals(DependencyTargetElement.Value, changed.get(1).getElement());
				assertEquals(idApplicationWdm, changed.get(1).getValue());
			}

			// 22A
			engine.requestChange(idModel, idModel22A);
			cached = engine.getCachedPropertyStore();
			assertTrue(cached.getProperty(idApplication).getAvailableListDetail().size() == 2);
			assertTrue(cached.getProperty(idApplication).getAvailableListDetail().get(0).getId().equals(idApplicationWdm));
			assertTrue(cached.getProperty(idApplication).getAvailableListDetail().get(1).getId().equals(idApplicationDrift));
			assertTrue(cached.getProperty(idApplication).getCurrentValue().equals(idApplicationWdm));
			
			// 21A
			engine.requestChange(idModel, idModel21A);
			cached = engine.getCachedPropertyStore();
			assertTrue(cached.getProperty(idApplication).getAvailableListDetail().size() ==3);
			assertTrue(cached.getProperty(idApplication).getAvailableListDetail().get(0).getId().equals(idApplicationWdm));
			assertTrue(cached.getProperty(idApplication).getAvailableListDetail().get(1).getId().equals(idApplicationDrift));
			assertTrue(cached.getProperty(idApplication).getAvailableListDetail().get(2).getId().equals(idApplicationInBand));
			
			// 23A
			engine.requestChange(idModel, idModel23A);
			cached = engine.getCachedPropertyStore();
			assertTrue(cached.getProperty(idApplication).getAvailableListDetail().size() ==3);
			assertTrue(cached.getProperty(idApplication).getAvailableListDetail().get(0).getId().equals(idApplicationWdm));
			assertTrue(cached.getProperty(idApplication).getAvailableListDetail().get(1).getId().equals(idApplicationDrift));
			assertTrue(cached.getProperty(idApplication).getAvailableListDetail().get(2).getId().equals(idApplicationInBand));
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testScript() {
		String idStartWavelength = "ID_OSA_START_WAVELENGTH";
		String idStopWavelength = "ID_OSA_STOP_WAVELENGTH";
		String idCenterWavelength = "ID_OSA_CENTER_WAVELENGTH";
		String idSpanWavelength = "ID_OSA_SPAN_WAVELENGTH";
		String idModel = "ID_OSA_MODEL";
		String idModel20A = "ID_OSA_MODEL_20A";
		String idModel21A = "ID_OSA_MODEL_21A";
		String idModel22A = "ID_OSA_MODEL_22A";
		String idModel23A = "ID_OSA_MODEL_23A";
		
		DepPropertyStore store = createPropertyStore();
		store.add(createDoubleProperty(idStartWavelength, 1250, "nm", 0, 9999, 3));
		store.add(createDoubleProperty(idStopWavelength, 1650, "nm", 0, 9999, 3));
		store.add(createDoubleProperty(idCenterWavelength, 1450, "nm", 0, 9999, 3));
		store.add(createDoubleProperty(idSpanWavelength, 400, "nm", 0, 9999, 3));
		store.add(createListProperty(idModel, Arrays.asList(idModel20A, idModel21A, idModel22A, idModel23A), idModel20A));

		DependencySpecHolder holder = new DependencySpecHolder();
				
		////////// Start Wavelength///////////
		{
			DependencySpec spec = new DependencySpec(idStartWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultScript("if ($ID_OSA_MODEL.Value == %ID_OSA_MODEL_20A) {ret=$ID_OSA_STOP_WAVELENGTH.Value-$ID_OSA_START_WAVELENGTH.Value;} else if ($ID_OSA_MODEL.Value == %ID_OSA_MODEL_23A) {ret=$ID_OSA_STOP_WAVELENGTH.Value+$ID_OSA_START_WAVELENGTH.Value;} else {ret=100;}");
			spec.add(detail);
		}
		
		DependencyEngine engine = createEngine(holder, store);
			
		// 20A
		try {
			engine.requestChange(idModel, idModel20A);
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			
			assertEquals("400.000", cached.getProperty(idStartWavelength).getCurrentValue());
			cached.commit();
			
			engine.requestChange(idModel, idModel23A);
			cached = engine.getCachedPropertyStore();
			assertEquals("2050.000", cached.getProperty(idStartWavelength).getCurrentValue());
			cached.commit();
			
			engine.requestChange(idModel, idModel21A);
			cached = engine.getCachedPropertyStore();
			assertEquals("100.000", cached.getProperty(idStartWavelength).getCurrentValue());
			
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	void testLimitOverReject() {
		String idStartWavelength = "ID_OSA_START_WAVELENGTH";
		String idStopWavelength = "ID_OSA_STOP_WAVELENGTH";
		
		DepPropertyStore store = createPropertyStore();
		store.add(createDoubleProperty(idStartWavelength, 1300, "nm", 1250, 1650, 3));
		store.add(createDoubleProperty(idStopWavelength, 1600, "nm", 1250, 1650, 3));
		
		DependencySpecHolder holder = new DependencySpecHolder();
		////////// Stop Wavelength///////////
		{
			DependencySpec spec = new DependencySpec(idStopWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("$" + idStartWavelength + ".Value" + "+100");
			detail.setSettingDisabledBehavior(SettingDisabledBehavior.Reject);
			spec.add(detail);
		}
		////////// Start Wavelength///////////
		{
			DependencySpec spec = new DependencySpec(idStartWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("$" + idStopWavelength + ".Value" + "-100");
			detail.setSettingDisabledBehavior(SettingDisabledBehavior.Reject);
			spec.add(detail);
		}
		DependencyEngine engine = createEngine(holder, store);
		
		boolean exception = false;
		CachedPropertyStore cached = null;
		try {
			engine.requestChange(idStartWavelength, "1600");
			cached = engine.getCachedPropertyStore();
			assertTrue(cached.getProperty(idStopWavelength).getCurrentValue().equals("1650.000"));
			
		} catch (RequestRejectedException e) {
			exception = true;
		}
		assertEquals("1600.000", store.getProperty(idStopWavelength).getCurrentValue());
		assertTrue(exception);
		
		try {
			exception = false;
			store.getProperty(idStartWavelength).setCurrentValue("1300.0");
			engine.requestChange(idStopWavelength, "1300");
			assertTrue(store.getProperty(idStartWavelength).getCurrentValue().equals("1250.000"));
			assertTrue(store.getProperty(idStartWavelength).getCurrentValue().equals("1300.000"));
			
			
		} catch (RequestRejectedException e) {
			exception = true;
		}
		assertTrue(exception);
	}
	
	@Test
	void testResultExpression() {
		String idStartWavelength = "ID_OSA_START_WAVELENGTH";
		String idStopWavelength = "ID_OSA_STOP_WAVELENGTH";
		String idBand = "ID_OSA_BAND";
		String idBandC = "ID_OSA_BAND_C";
		String idBandManual = "ID_OSA_BAND_MANUAL";
		
		DepPropertyStore store = createPropertyStore();
		store.add(createListProperty(idBand, Arrays.asList(idBandC, idBandManual), idBandC));
		store.add(createDoubleProperty(idStartWavelength, 1250, "nm", 0, 9999, 3));
		store.add(createDoubleProperty(idStopWavelength, 1650, "nm", 0, 9999, 3));
		DependencySpecHolder holder = new DependencySpecHolder();
		////////// Band ///////////
		{
			DependencySpec spec = new DependencySpec(idBand);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("%" + idBandManual).conditionExpression("$ID_OSA_START_WAVELENGTH.Value > 2000");
			detail.setSettingDisabledBehavior(SettingDisabledBehavior.Reject);
			spec.add(detail);
		}

		DependencyEngine engine = createEngine(holder, store);
		
		try {
			engine.requestChange(idStartWavelength, "2100");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertTrue(cached.getProperty(idBand).getCurrentValue().equals(idBandManual));
			
		} catch (RequestRejectedException e) {

		}
	}
	
	@Test
	void testConditionOnly() {
		String idStartWavelength = "ID_OSA_START_WAVELENGTH";
		String idStopWavelength = "ID_OSA_STOP_WAVELENGTH";
		
		DepPropertyStore store = createPropertyStore();
		store.add(createDoubleProperty(idStartWavelength, 1500, "nm", 1250, 1650, 3));
		store.add(createDoubleProperty(idStopWavelength, 1550, "nm", 1250, 1650, 3));
		
		DependencySpecHolder holder = new DependencySpecHolder();
		
		////////// Start Wavelength///////////
		{
			DependencySpec spec = new DependencySpec(idStartWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("$" + idStopWavelength +".Value + 100");
			spec.add(detail);
		}
		
		DependencyEngine engine = createEngine(holder, store);
		try {			
			engine.requestChange(idStopWavelength, "1500");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertTrue(cached.getProperty(idStartWavelength).getCurrentValue().equals("1600.000"));
			assertTrue(cached.getProperty(idStopWavelength).getCurrentValue().equals("1500.000"));
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testStartStopCenterSpan() {
		String idStartWavelength = "ID_OSA_START_WAVELENGTH";
		String idStopWavelength = "ID_OSA_STOP_WAVELENGTH";
		String idCenterWavelength = "ID_OSA_CENTER_WAVELENGTH";
		String idSpanWavelength = "ID_OSA_SPAN_WAVELENGTH";
		String idBand = "ID_OSA_BAND";
		String idBandC = "ID_OSA_BAND_C";
		String idBandManual = "ID_OSA_BAND_MANUAL";
		
		DepPropertyStore store = createPropertyStore();
		store.add(createDoubleProperty(idStartWavelength, 1250, "nm", 0, 9999, 3));
		store.add(createDoubleProperty(idStopWavelength, 1650, "nm", 0, 9999, 3));
		store.add(createDoubleProperty(idCenterWavelength, 1450, "nm", 0, 9999, 3));
		store.add(createDoubleProperty(idSpanWavelength, 400, "nm", 0, 9999, 3));
		store.add(createListProperty(idBand, Arrays.asList(idBandC, idBandManual), idBandManual));

		DependencySpecHolder holder = new DependencySpecHolder();
				
		////////// Start Wavelength///////////
		{
			DependencySpec spec = new DependencySpec(idStartWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("$"+idCenterWavelength + ".Value - " + "$"+idSpanWavelength +".Value/2");
			detail.addExpression().resultExpression("1530").conditionIdValue(idBand).equals().conditionSelectionId(idBandC);
			spec.add(detail);
		}
		////////// Stop Wavelength///////////
		{
			DependencySpec spec = new DependencySpec(idStopWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("$"+idCenterWavelength + ".Value + " + "$"+idSpanWavelength +".Value/2");
			detail.addExpression().resultExpression("1565").conditionIdValue(idBand).equals().conditionSelectionId(idBandC);
			spec.add(detail);
		}
		////////// Center Wavelength///////////
		{
			DependencySpec spec = new DependencySpec(idCenterWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("("+"$"+idStartWavelength + ".Value + " + "$"+idStopWavelength +".Value)/2");
			spec.add(detail);
			
			// Enabledの変更なのにStart/Stopの値を変えようとするバグがある
			DependencyExpressionHolder enabled = new DependencyExpressionHolder(DependencyTargetElement.Enabled);
			enabled.addExpression().resultExpression(DependencyExpression.True).conditionIdValue(idBand).equals().conditionSelectionId(idBandManual);
			enabled.addExpression().resultExpression(DependencyExpression.False).conditionElse();
			spec.add(enabled);
		}
		////////// Span Wavelength///////////
		{
			DependencySpec spec = new DependencySpec(idSpanWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("$"+idStopWavelength + ".Value - " + "$"+idStartWavelength +".Value");
			spec.add(detail);
		}

		{
			DependencyBuilder builder = new DependencyBuilder(idStartWavelength, holder);
			assertEquals(4, builder.getLayerCount());
			
			List<DependencyProperty> layer0 = builder.getSpecs(0);
			assertEquals(2, layer0.size());
			assertEquals(idCenterWavelength, layer0.get(0).getId());
			assertEquals(idSpanWavelength, layer0.get(1).getId());
		}
		{
			DependencyBuilder builder = new DependencyBuilder(idStopWavelength, holder);
			assertEquals(4, builder.getLayerCount());
		}
		{
			DependencyBuilder builder = new DependencyBuilder(idCenterWavelength, holder);
			assertEquals(4, builder.getLayerCount());
		}
		{
			DependencyBuilder builder = new DependencyBuilder(idSpanWavelength, holder);
			assertEquals(4, builder.getLayerCount());
		}

		DependencyEngine engine = createEngine(holder, store);
			
		try {
			engine.requestChange(idStartWavelength, "1350");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertEquals("1350.000", cached.getProperty(idStartWavelength).getCurrentValue());
			assertEquals("1650.000", cached.getProperty(idStopWavelength).getCurrentValue());
			assertEquals("1500.000", cached.getProperty(idCenterWavelength).getCurrentValue());
			assertEquals("300.000", cached.getProperty(idSpanWavelength).getCurrentValue());
			cached.commit();
			
			engine.requestChange(idCenterWavelength, "1450");
			cached = engine.getCachedPropertyStore();
			assertEquals("1300.000", cached.getProperty(idStartWavelength).getCurrentValue());
			assertEquals("1600.000", cached.getProperty(idStopWavelength).getCurrentValue());
			assertEquals("1450.000", cached.getProperty(idCenterWavelength).getCurrentValue());
			assertEquals("300.000", cached.getProperty(idSpanWavelength).getCurrentValue());
			cached.commit();
			
			engine.requestChange(idSpanWavelength, "100");
			cached = engine.getCachedPropertyStore();
			assertEquals("1400.000", cached.getProperty(idStartWavelength).getCurrentValue());
			assertEquals("1500.000", cached.getProperty(idStopWavelength).getCurrentValue());
			assertEquals("1450.000", cached.getProperty(idCenterWavelength).getCurrentValue());
			assertEquals("100.000", cached.getProperty(idSpanWavelength).getCurrentValue());
			cached.commit();

			// Enabledの変更なのにStart/Stopの値を変えようとするバグがある
			engine.requestChange(idBand, idBandC);
			cached = engine.getCachedPropertyStore();
			assertEquals("1530.000", cached.getProperty(idStartWavelength).getCurrentValue());
			assertEquals("1565.000", cached.getProperty(idStopWavelength).getCurrentValue());
			assertEquals("1547.500", cached.getProperty(idCenterWavelength).getCurrentValue());
			assertEquals("35.000", cached.getProperty(idSpanWavelength).getCurrentValue());
			cached.commit();	
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		
	}

	
	@Test
	void testTwoElses() {
		String idAllTest = "ID_ALL_TEST";
		String idAllTestStart = "ID_ALL_TEST_START";
		String idAllTestStop = "ID_ALL_TEST_STOP";
		
		String idOsaTest = "ID_OSA_TEST";
		String idOsaTestStart = "ID_OSA_TEST_START";
		String idOsaTestStop = "ID_OSA_TEST_STOP";
		
		String idOtdrTest = "ID_OTDR_TEST";
		String idOtdrTestStart = "ID_OTDR_TEST_START";
		String idOtdrTestStop = "ID_OTDR_TEST_STOP";
		
		DepPropertyStore store = createPropertyStore();
		store.add(createListProperty(idAllTest, Arrays.asList(idAllTestStart, idAllTestStop), idAllTestStop));
		store.add(createListProperty(idOsaTest, Arrays.asList(idOsaTestStart, idOsaTestStop), idOsaTestStop));
		store.add(createListProperty(idOtdrTest, Arrays.asList(idOtdrTestStart, idOtdrTestStop), idOtdrTestStop));
		
		DependencySpecHolder holder = new DependencySpecHolder();
		
		////////// idOsaTestStart ///////////
		{
			DependencySpec spec = new DependencySpec(idOsaTest);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("%" + idOsaTestStart).conditionExpression("$" + idAllTest + ".Value ==%" + idAllTestStart);
			detail.addExpression().resultExpression("%" + idOsaTestStop).conditionElse();
			spec.add(detail);
		}
		
		////////// idOtdrTestStart ///////////
		{
			DependencySpec spec = new DependencySpec(idOtdrTest);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("%" + idOtdrTestStart).conditionExpression("$" + idAllTest + ".Value ==%" + idAllTestStart);
			detail.addExpression().resultExpression("%" + idOtdrTestStop).conditionElse();
			spec.add(detail);
		}
		
//		DependencyBuilder3 builder = new DependencyBuilder3(idAllTest, holder);
		
		DependencyEngine engine = createEngine(holder, store);
		
		try {
			engine.requestChange(idAllTest, idAllTestStart);
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			
			assertEquals(idAllTestStart, cached.getProperty(idAllTest).getCurrentValue());
			assertEquals(idOsaTestStart, cached.getProperty(idOsaTest).getCurrentValue());
			assertEquals(idOtdrTestStart, cached.getProperty(idOtdrTest).getCurrentValue());
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testNoDependency() {
		String idAllTest = "ID_ALL_TEST";
		String idAllTestStart = "ID_ALL_TEST_START";
		String idAllTestStop = "ID_ALL_TEST_STOP";
				
		DepPropertyStore store = createPropertyStore();
		store.add(createListProperty(idAllTest, Arrays.asList(idAllTestStart, idAllTestStop), idAllTestStop));
		
		DependencySpecHolder holder = new DependencySpecHolder();
		DependencyEngine engine = createEngine(holder, store);
		
		try {
			engine.requestChange(idAllTest, idAllTestStart);
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			
			assertEquals(idAllTestStart, cached.getProperty(idAllTest).getCurrentValue());
			//assertEquals(idOtdrTestStart, cached.getProperty(idOtdrTest).getCurrentValue());
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testMultiConditionWithElse() {
		String idPulse = "ID_PULSEWIDTH";
		String idPulse10ns = "ID_PULSEWIDTH_10NS";
		String idPulse20ns = "ID_PULSEWIDTH_20NS";
		String idPulse50ns = "ID_PULSEWIDTH_50NS";
		String idPulse100ns = "ID_PULSEWIDTH_100NS";
		String idPulse500ns = "ID_PULSEWIDTH_500NS";
		
		String idRange = "ID_RANGE";
		String idRange5km = "ID_RANGE_5KM";
		String idRange10km = "ID_RANGE_10KM";
		String idRange50km = "ID_RANGE_50KM";
		String idRange100km = "ID_RANGE_100KM";
		String idRange200km = "ID_RANGE_200KM";
		
		DepPropertyStore store = createPropertyStore();
		store.add(createListProperty(idPulse, Arrays.asList(idPulse10ns, idPulse20ns, idPulse50ns, idPulse100ns, idPulse500ns), idPulse10ns));
		store.add(createListProperty(idRange, Arrays.asList(idRange5km, idRange10km, idRange50km, idRange100km, idRange200km), idRange5km));
		
		DependencySpecHolder holder = new DependencySpecHolder();
		
		{
			DependencySpec spec = new DependencySpec(idPulse);
			holder.add(spec);
			
			{////////// Pulse500ns is OK when 200km///////////
				DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.ListItemEnabled);
				detail.addExpression().resultExpression(DependencyExpression.True).conditionExpression("$" + idRange + ".Value ==%" + idRange200km);
				detail.addExpression().resultExpression(DependencyExpression.False).conditionElse();
				spec.add(idPulse500ns, detail);
			}
			{////////// Pulse100ns is OK when 100km and 200km///////////
				DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.ListItemEnabled);
				detail.addExpression().resultExpression(DependencyExpression.True).conditionExpression(
						"($" + idRange + ".Value ==%" + idRange200km + ")||(" + 
								"$" + idRange + ".Value ==%" + idRange100km + ")");
				detail.addExpression().resultExpression(DependencyExpression.False).conditionElse();
				spec.add(idPulse100ns, detail);
			}
			{////////// Pulse50ns is OK when 200km, 100km, 50km///////////
				DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.ListItemEnabled);
				detail.addExpression().resultExpression(DependencyExpression.True).conditionExpression("($" + idRange + ".Value ==%" + idRange200km + ")" + "||" + 
						"($" + idRange + ".Value ==%" + idRange100km + ")" +"||" + "($" + idRange + ".Value ==%" + idRange50km + ")");
				detail.addExpression().resultExpression(DependencyExpression.False).conditionElse();
				spec.add(idPulse50ns, detail);
			}
		}
		DependencyEngine engine = createEngine(holder, store);
		
		try {
			engine.requestChange(idRange, idRange5km);
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			
			assertEquals(2, cached.getProperty(idPulse).getAvailableListDetail().size());
			assertEquals(idPulse10ns, cached.getProperty(idPulse).getAvailableListDetail().get(0).getId());
			assertEquals(idPulse20ns, cached.getProperty(idPulse).getAvailableListDetail().get(1).getId());
			
			engine.requestChange(idRange, idRange50km);
			cached = engine.getCachedPropertyStore();
			assertEquals(3, cached.getProperty(idPulse).getAvailableListDetail().size());
			assertEquals(idPulse10ns, cached.getProperty(idPulse).getAvailableListDetail().get(0).getId());
			assertEquals(idPulse20ns, cached.getProperty(idPulse).getAvailableListDetail().get(1).getId());
			assertEquals(idPulse50ns, cached.getProperty(idPulse).getAvailableListDetail().get(2).getId());
						
			engine.requestChange(idRange, idRange100km);
			cached = engine.getCachedPropertyStore();
			assertEquals(4, cached.getProperty(idPulse).getAvailableListDetail().size());
			assertEquals(idPulse10ns, cached.getProperty(idPulse).getAvailableListDetail().get(0).getId());
			assertEquals(idPulse20ns, cached.getProperty(idPulse).getAvailableListDetail().get(1).getId());
			assertEquals(idPulse50ns, cached.getProperty(idPulse).getAvailableListDetail().get(2).getId());
			assertEquals(idPulse100ns, cached.getProperty(idPulse).getAvailableListDetail().get(3).getId());
			
			engine.requestChange(idRange, idRange200km);
			cached = engine.getCachedPropertyStore();
			assertEquals(5, cached.getProperty(idPulse).getAvailableListDetail().size());
			assertEquals(idPulse10ns, cached.getProperty(idPulse).getAvailableListDetail().get(0).getId());
			assertEquals(idPulse20ns, cached.getProperty(idPulse).getAvailableListDetail().get(1).getId());
			assertEquals(idPulse50ns, cached.getProperty(idPulse).getAvailableListDetail().get(2).getId());
			assertEquals(idPulse100ns, cached.getProperty(idPulse).getAvailableListDetail().get(3).getId());
			assertEquals(idPulse500ns, cached.getProperty(idPulse).getAvailableListDetail().get(4).getId());
			
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void  testEdit() {
		String idPulse = "ID_PULSEWIDTH";
		String idPulse10ns = "ID_PULSEWIDTH_10NS";
		String idPulse20ns = "ID_PULSEWIDTH_20NS";
		String idPulse50ns = "ID_PULSEWIDTH_50NS";
		String idPulse100ns = "ID_PULSEWIDTH_100NS";
		String idPulse500ns = "ID_PULSEWIDTH_500NS";
		
		String idRange = "ID_RANGE";
		String idRange5km = "ID_RANGE_5KM";
		String idRange10km = "ID_RANGE_10KM";
		String idRange50km = "ID_RANGE_50KM";
		String idRange100km = "ID_RANGE_100KM";
		String idRange200km = "ID_RANGE_200KM";
		
		DepPropertyStore store = createPropertyStore();
		store.add(createListProperty(idPulse, Arrays.asList(idPulse10ns, idPulse20ns, idPulse50ns, idPulse100ns, idPulse500ns), idPulse10ns));
		store.add(createListProperty(idRange, Arrays.asList(idRange5km, idRange10km, idRange50km, idRange100km, idRange200km), idRange5km));
		
		DependencySpecHolder holder = new DependencySpecHolder();

		DependencySpec spec = new DependencySpec(idPulse);
		spec.add(DependencyTargetElement.Enabled, DependencyExpression.True, "$ID_RANGE.Value=%ID_RANGE_200KM");
		holder.add(spec);
		
		assertEquals("$ID_RANGE.Value=%ID_RANGE_200KM", holder.get(idPulse).getDependencyExpressionHolder(DependencyTargetElement.Enabled).getExpressions().get(DependencyExpression.True).getExpression().getExpression());
		assertEquals(DependencyExpression.ELSE, holder.get(idPulse).getDependencyExpressionHolder(DependencyTargetElement.Enabled).getExpressions().get(DependencyExpression.False).getExpression().getExpression());

		assertEquals(2, holder.get(idPulse).getDependencyExpressionHolder(DependencyTargetElement.Enabled).getExpressions().keySet().size());
	}
	
	@Test
	void testConvert() {
		String idMode = "ID_MODE";
		String idModeA = "ID_MODE_A";
		String idModeB = "ID_MODE_B";

		String idRoot = "ID_ROOT";
		String idRootA = "ID_ROOT_A";
		String idRootB = "ID_ROOT_B";
		
		DependencySpecHolder holder = new DependencySpecHolder();
		holder.get(idMode).add(DependencyTargetElement.ListItemEnabled, idModeA, DependencyExpression.True, new DependencyExpression().getExpression().conditionIdValue(idRoot).equals().conditionSelectionId(idRootA).getExpression());
		holder.get(idMode).add(DependencyTargetElement.ListItemEnabled, idModeA, DependencyExpression.False, new DependencyExpression().getExpression().conditionElse().getExpression());
		holder.get(idMode).add(DependencyTargetElement.ListItemEnabled, idModeB, DependencyExpression.True, new DependencyExpression().getExpression().conditionIdValue(idRoot).equals().conditionSelectionId(idRootB).getExpression());
		holder.get(idMode).add(DependencyTargetElement.ListItemEnabled, idModeB, DependencyExpression.False, new DependencyExpression().getExpression().conditionElse().getExpression());

		String idMode2 = "ID_MODE2";
		String idMode2A = "ID_MODE2_A";
		String idMode2B = "ID_MODE2_B";

		String idRoot2 = "ID_ROOT2";
		String idRoot2A = "ID_ROOT2_A";
		String idRoot2B = "ID_ROOT2_B";
		
		holder.get(idMode2).add(DependencyTargetElement.ListItemEnabled, idMode2A, DependencyExpression.False, new DependencyExpression().getExpression().conditionIdValue(idRoot2).equals().conditionSelectionId(idRoot2A).getExpression());
		holder.get(idMode2).add(DependencyTargetElement.ListItemEnabled, idMode2A, DependencyExpression.True, new DependencyExpression().getExpression().conditionElse().getExpression());
		holder.get(idMode2).add(DependencyTargetElement.ListItemEnabled, idMode2B, DependencyExpression.False, new DependencyExpression().getExpression().conditionIdValue(idRoot2).equals().conditionSelectionId(idRoot2B).getExpression());
		holder.get(idMode2).add(DependencyTargetElement.ListItemEnabled, idMode2B, DependencyExpression.True, new DependencyExpression().getExpression().conditionElse().getExpression());


		try {
			DepPropertyStore store = createPropertyStore();
			store.add(createListProperty(idMode, Arrays.asList(idModeA, idModeB), idModeA));
			store.add(createListProperty(idRoot, Arrays.asList(idRootA, idRootB), idRootA));
			DependencyEngine engine = createEngine(holder, store);
			CachedPropertyStore cached;
			engine.requestChange(idRoot, idRootA);
			cached = engine.getCachedPropertyStore();
			
			assertTrue(1 == cached.getProperty(idMode).getAvailableListDetail().size());
			assertTrue(idModeA.equals(cached.getProperty(idMode).getAvailableListDetail().get(0).getId()));
			
			engine.requestChange(idRoot, idRootB);
			cached = engine.getCachedPropertyStore();
			
			assertTrue(1 == cached.getProperty(idMode).getAvailableListDetail().size());
			assertTrue(idModeB.equals(cached.getProperty(idMode).getAvailableListDetail().get(0).getId()));
		}
		catch (Exception e) {
			
		}
		
		try {
			DepPropertyStore store = createPropertyStore();
			PropertyGetter getter = new PropertyGetter() {
				@Override
				public SvProperty getProperty(String id) {
					return store.getProperty(id);
				}
			};
			store.add(createListProperty(idMode, Arrays.asList(idModeA, idModeB), idModeA));
			store.add(createListProperty(idRoot, Arrays.asList(idRootA, idRootB), idRootA));
			DependencySpecHolder alternativeholder = new AlternativeDependencyGenerator().convert(holder, getter);
			DependencyEngine engine = createEngine(alternativeholder, store);
			CachedPropertyStore cached;
			
			engine.requestChange(idRoot, idRootA);
			cached = engine.getCachedPropertyStore();
			assertTrue(2 == cached.getProperty(idMode).getAvailableListDetail().size());
			assertTrue(idModeA.equals(cached.getProperty(idMode).getAvailableListDetail().get(0).getId()));
			assertTrue(idModeB.equals(cached.getProperty(idMode).getAvailableListDetail().get(1).getId()));
			
			store.getProperty(idMode).setCurrentValue(idModeB);
			store.getProperty(idRoot).setCurrentValue(idRootB);
			engine.requestChange(idMode, idModeA);
			cached = engine.getCachedPropertyStore();
			assertEquals(idRootA, cached.getProperty(idRoot).getCurrentValue());

			store.getProperty(idMode).setCurrentValue(idModeA);
			store.getProperty(idRoot).setCurrentValue(idRootA);
			engine.requestChange(idMode, idModeB);
			cached = engine.getCachedPropertyStore();
			assertEquals(idRootB, cached.getProperty(idRoot).getCurrentValue());
		}
		catch (Exception e) {
			
		}
				
		try {
			DepPropertyStore store = createPropertyStore();
			PropertyGetter getter = new PropertyGetter() {
				@Override
				public SvProperty getProperty(String id) {
					return store.getProperty(id);
				}
			};
			store.add(createListProperty(idMode2, Arrays.asList(idMode2A, idMode2B), idMode2A));
			store.add(createListProperty(idRoot2, Arrays.asList(idRoot2A, idRoot2B), idRoot2A));
			DependencySpecHolder alternativeholder = new AlternativeDependencyGenerator().convert(holder, getter);
			
			store.getProperty(idMode2).setCurrentValue(idMode2A);
			store.getProperty(idRoot2).setCurrentValue(idRoot2B);
			DependencyEngine engine = createEngine(alternativeholder, store);
			engine.requestChange(idMode2, idMode2B);
			CachedPropertyStore cached;	
			cached = engine.getCachedPropertyStore();
			assertEquals(idRoot2A, cached.getProperty(idRoot2).getCurrentValue());
			
			engine.requestChange(idMode2, idMode2A);
			cached = engine.getCachedPropertyStore();
			assertEquals(idRoot2B, cached.getProperty(idRoot2).getCurrentValue());
		}
		catch (Exception e) {
			
		}
	}
	
	@Test
	void testConvert2() {
		String idRoot = "ID_ROOT";
		String idRootA = "ID_ROOT_A";
		String idRootB = "ID_ROOT_B";
		String idRootC = "ID_ROOT_C";
		
		String idMiddle = "ID_MIDDLE";
		String idMiddle1 = "ID_MIDDLE_1";
		String idMiddle2 = "ID_MIDDLE_2";
		String idMiddle3 = "ID_MIDDLE_3";
	
		String idTop = "ID_TOP";
		String idTop1 = "ID_TOP_1";
		String idTop2 = "ID_TOP_2";
		String idTop3 = "ID_TOP_3";
		String idTop4 = "ID_TOP_4";
		String idTop5 = "ID_TOP_5";
		String idTop6 = "ID_TOP_6";
		
		DependencySpecHolder holder = new DependencySpecHolder();
		holder.get(idMiddle).add(DependencyTargetElement.ListItemEnabled, idMiddle1, DependencyExpression.True, new DependencyExpression().getExpression().conditionIdValue(idRoot).equals().conditionSelectionId(idRootA).getExpression());
		holder.get(idMiddle).add(DependencyTargetElement.ListItemEnabled, idMiddle1, DependencyExpression.False, new DependencyExpression().getExpression().conditionElse().getExpression());
		holder.get(idMiddle).add(DependencyTargetElement.ListItemEnabled, idMiddle2, DependencyExpression.True, new DependencyExpression().getExpression().conditionIdValue(idRoot).equals().conditionSelectionId(idRootB).getExpression());
		holder.get(idMiddle).add(DependencyTargetElement.ListItemEnabled, idMiddle2, DependencyExpression.False, new DependencyExpression().getExpression().conditionElse().getExpression());
		holder.get(idMiddle).add(DependencyTargetElement.ListItemEnabled, idMiddle3, DependencyExpression.True, new DependencyExpression().getExpression().conditionIdValue(idRoot).equals().conditionSelectionId(idRootC).getExpression());
		holder.get(idMiddle).add(DependencyTargetElement.ListItemEnabled, idMiddle3, DependencyExpression.False, new DependencyExpression().getExpression().conditionElse().getExpression());

		holder.get(idTop).add(DependencyTargetElement.ListItemEnabled, idTop1, DependencyExpression.False, new DependencyExpression().getExpression().conditionIdValue(idMiddle).equals().conditionSelectionId(idMiddle1).getExpression());
		holder.get(idTop).add(DependencyTargetElement.ListItemEnabled, idTop1, DependencyExpression.True, new DependencyExpression().getExpression().conditionElse().getExpression());
		holder.get(idTop).add(DependencyTargetElement.ListItemEnabled, idTop2, DependencyExpression.False, new DependencyExpression().getExpression().conditionIdValue(idMiddle).equals().conditionSelectionId(idMiddle1).getExpression());
		holder.get(idTop).add(DependencyTargetElement.ListItemEnabled, idTop2, DependencyExpression.True, new DependencyExpression().getExpression().conditionElse().getExpression());
		holder.get(idTop).add(DependencyTargetElement.ListItemEnabled, idTop3, DependencyExpression.False, new DependencyExpression().getExpression().conditionIdValue(idMiddle).equals().conditionSelectionId(idMiddle2).getExpression());
		holder.get(idTop).add(DependencyTargetElement.ListItemEnabled, idTop3, DependencyExpression.True, new DependencyExpression().getExpression().conditionElse().getExpression());
		holder.get(idTop).add(DependencyTargetElement.ListItemEnabled, idTop4, DependencyExpression.False, new DependencyExpression().getExpression().conditionIdValue(idMiddle).equals().conditionSelectionId(idMiddle2).getExpression());
		holder.get(idTop).add(DependencyTargetElement.ListItemEnabled, idTop4, DependencyExpression.True, new DependencyExpression().getExpression().conditionElse().getExpression());
		holder.get(idTop).add(DependencyTargetElement.ListItemEnabled, idTop5, DependencyExpression.False, new DependencyExpression().getExpression().conditionIdValue(idMiddle).equals().conditionSelectionId(idMiddle3).getExpression());
		holder.get(idTop).add(DependencyTargetElement.ListItemEnabled, idTop5, DependencyExpression.True, new DependencyExpression().getExpression().conditionElse().getExpression());
		holder.get(idTop).add(DependencyTargetElement.ListItemEnabled, idTop6, DependencyExpression.False, new DependencyExpression().getExpression().conditionIdValue(idMiddle).equals().conditionSelectionId(idMiddle3).getExpression());
		holder.get(idTop).add(DependencyTargetElement.ListItemEnabled, idTop6, DependencyExpression.True, new DependencyExpression().getExpression().conditionElse().getExpression());
		
		try {
			DepPropertyStore store = createPropertyStore();
			store.add(createListProperty(idRoot, Arrays.asList(idRootA, idRootB, idRootC), idRootA));
			store.add(createListProperty(idMiddle, Arrays.asList(idMiddle1, idMiddle2, idMiddle3), idMiddle1));
			store.add(createListProperty(idTop, Arrays.asList(idTop1, idTop2, idTop3, idTop4, idTop5, idTop6), idTop1));
			
			holder = new AlternativeDependencyGenerator().convert(holder, new PropertyGetter() {
				@Override
				public SvProperty getProperty(String id) {
					return store.getProperty(id);
				}
			});
			
			assertEquals("$" + idTop + "==%" + idTop1, holder.get(idMiddle).getDependencyExpressionHolder(DependencyTargetElement.Value).getExpressions().get(idMiddle1).getExpression().getExpression());

			DependencyEngine engine = createEngine(holder, store);
			CachedPropertyStore cached;
			engine.requestChange(idTop, idTop6);
			cached = engine.getCachedPropertyStore();
			
			assertEquals(idMiddle3, cached.getProperty(idMiddle).getCurrentValue());
			assertEquals(idRootC, cached.getProperty(idRoot).getCurrentValue());
		}
		catch (Exception e) {
			

		}
	}
	private DependencyEngine createEngine(DependencySpecHolder holder, DepPropertyStore store) {
		DependencyEngine engine = new DependencyEngine() {
			@Override
			protected DependencySpecHolder getDependencyHolder() {
				return holder;
			}

			@Override
			protected DepPropertyStore getPropertiesStore() {
				return store;
			}
		};
		return engine;
	}

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

	
}

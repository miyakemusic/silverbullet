package jp.silverbullet.dependency.speceditor3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.Test;

import jp.silverbullet.ChangedItemValue;
import jp.silverbullet.SvProperty;
import jp.silverbullet.SvPropertyStore;
import jp.silverbullet.XmlPersistent;
import jp.silverbullet.dependency.engine.DependencyBuilder;
import jp.silverbullet.dependency.engine.RequestRejectedException;
import jp.silverbullet.dependency.speceditor3.DependencyExpressionHolder.SettingDisabledBehavior;
import jp.silverbullet.property.ArgumentDefInterface;
import jp.silverbullet.property.ListDetailElement;
import jp.silverbullet.property.PropertyDef;
import jp.silverbullet.spec.SpecElement;

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
		
		DependencySpecHolder2 holder = new DependencySpecHolder2();
		
		{
			DependencySpec2 spec = new DependencySpec2(idSecond);
			holder.add(spec);
			DependencyExpressionHolder value = new DependencyExpressionHolder(DependencyTargetElement.Value);
			value.addExpression().resultExpression("$"+idFirst + ".Value * 2");
			spec.add(value);	
		}
		{
			DependencySpec2 spec = new DependencySpec2(idFirst);
			holder.add(spec);
			DependencyExpressionHolder enabled = new DependencyExpressionHolder(DependencyTargetElement.Enabled);
			enabled.addExpression().resultExpression(DependencyExpression.False).conditionExpression("$" + idEnableChanger + ".Value > 100");
			spec.add(enabled);	
		}
		
		DependencyEngine2 engine = createEngine(holder, store);
		
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
		
		DependencySpecHolder2 holder = new DependencySpecHolder2();
		
		//////////BAND///////////
		DependencySpec2 specBand = new DependencySpec2(idBand);
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
			specDetailBand.addExpression().resultExpression("%" + idBandManual).conditionIdValue(idStartWavelength).equals().anyValue();
//			specDetailBand.addExpression().resultValue(/*"%" +*/ idBandManual).conditionIdValue(idStartWavelength).equals().anyValue();
			specDetailBand.addExpression().resultExpression("%" + idBandManual).conditionIdValue(idStopWavelength).equals().anyValue();
			specBand.add(specDetailBand);
		}
		
		////////// Start Wavelength///////////
		//// Value ////
		{
			DependencySpec2 spec = new DependencySpec2(idStartWavelength);
			holder.add(spec);
			DependencyExpressionHolder specDetailStartW = new DependencyExpressionHolder(DependencyTargetElement.Value);
			specDetailStartW.addExpression().resultExpression("1530").conditionIdValue(idBand).equals().conditionSelectionId(idBandC);
			
			specDetailStartW.addExpression().resultExpression("$" + idStopWavelength + ".Value").conditionExpression("$" + idStartWavelength + ".Value > " + "$" + idStopWavelength + ".Value");
			spec.add(specDetailStartW);
			
		}
		////////// Stop Wavelength///////////
		//// Value ////
		{
			DependencySpec2 spec = new DependencySpec2(idStopWavelength);
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
			DependencySpec2 spec = new DependencySpec2(idApplicationInBand);
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
		
		DependencyEngine2 engine = createEngine(holder, store);
		DependencyBuilder3 builder2 = new DependencyBuilder3(idStartWavelength, holder);
		List<DependencyProperty> spec2 = builder2.getSpecs(0);
		assertTrue(builder2.getLayerCount() == 3);
		
		assertTrue(spec2.get(0).getId().equals(idBand));
		assertTrue(spec2.get(0).getElement().equals(DependencyTargetElement.Value));
		assertTrue(spec2.get(0).getCondition().equals("$ID_OSA_START_WAVELENGTH.Value==*any"));
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
		assertTrue(spec3.get(1).getCondition().equals("$ID_OSA_STOP_WAVELENGTH.Value==*any"));
//		assertTrue(spec3.get(1).getValue().equals("ID_OSA_BAND_MANUAL"));	
		
		assertTrue(store.getProperty(idBand).getCurrentValue().equals(idBandC));
		try {
			engine.requestChange(idStartWavelength, "1400");
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			assertTrue(cached.getProperty(idBand).getCurrentValue().equals(idBandManual));
			assertTrue(cached.getProperty(idStartWavelength).getCurrentValue().equals("1400.000"));
			
			{
				List<ChangedItemValue2> changed = cached.getChanged(idStartWavelength);
				assertEquals(DependencyTargetElement.Value, changed.get(0).getElement());
				assertEquals("1400.000", changed.get(0).getValue());
			}
			{
				List<ChangedItemValue2> changed = cached.getChanged(idBand);
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
				List<ChangedItemValue2> changed = cached.getChanged(idBand);
				assertEquals(DependencyTargetElement.Value, changed.get(0).getElement());
				assertEquals(idBandC, changed.get(0).getValue());
			}
			{
				List<ChangedItemValue2> changed = cached.getChanged(idStartWavelength);
				assertEquals(DependencyTargetElement.Value, changed.get(0).getElement());
				assertEquals("1530.000", changed.get(0).getValue());
			}
			{
				List<ChangedItemValue2> changed = cached.getChanged(idStopWavelength);
				assertEquals(DependencyTargetElement.Value, changed.get(0).getElement());
				assertEquals("1565.000", changed.get(0).getValue());
			}

		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		
//		DependencyBuilder3 builder = new DependencyBuilder3(idBand, holder);
//		assertTrue(builder.getTree().getDependencyProperty().getId().equals(idBand));
		
		XmlPersistent<DependencySpecHolder2> propertyPersister = new XmlPersistent<>();
		try {
			propertyPersister.save(holder, "C:\\Projects\\dep.xml", DependencySpecHolder2.class);
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
		
		DependencySpecHolder2 holder = new DependencySpecHolder2();
				
		////////// Start Wavelength///////////
		{
			DependencySpec2 spec = new DependencySpec2(idStartWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("$"+idCenterWavelength+".Value" + "-" + "$"+idSpanWavelength+".Value/2");
			spec.add(detail);
		}
		////////// Stop Wavelength///////////
		{
			DependencySpec2 spec = new DependencySpec2(idStopWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("$"+idCenterWavelength+".Value" + "+" + "$"+idSpanWavelength+".Value/2");
			spec.add(detail);
		}
		////////// Center Wavelength///////////
		{
			DependencySpec2 spec = new DependencySpec2(idCenterWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("($"+idStartWavelength+".Value" + "+" + "$"+idStopWavelength+".Value)/2");
			spec.add(detail);
		}
		////////// Span Wavelength///////////
		{
			DependencySpec2 spec = new DependencySpec2(idSpanWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("$"+idStopWavelength +".Value - " + "$"+idStartWavelength + ".Value");
			spec.add(detail);
		}
		DependencyEngine2 engine = createEngine(holder, store);
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
		
		DependencySpecHolder2 holder = new DependencySpecHolder2();
		
		////////// Start Wavelength///////////
		{
			DependencySpec2 spec = new DependencySpec2(idStartWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("$" + idStopWavelength +".Value").conditionIdValue(idStartWavelength).largerThan().conditionIdValue(idStopWavelength);
			spec.add(detail);
		}
		////////// Stop Wavelength///////////
		{
			DependencySpec2 spec = new DependencySpec2(idStopWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("$" +idStartWavelength +".Value").conditionIdValue(idStartWavelength).largerThan().conditionIdValue(idStopWavelength);
			spec.add(detail);
		}
		
		DependencyEngine2 engine = createEngine(holder, store);
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
		
		DependencySpecHolder2 holder = new DependencySpecHolder2();
		
		CachedPropertyStore store = new CachedPropertyStore(createPropertyStore());
		store.add(createListProperty(idModel, Arrays.asList(idModel20A, idModel21A), idModel20A));
		store.add(createListProperty(idApplication, Arrays.asList(idApplicationWdm, idApplicationDrift, idApplicationInBand), idApplicationInBand));

		{
			DependencySpec2 spec = new DependencySpec2(idApplication);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.ListItemVisible);
			detail.addExpression().resultExpression(DependencyExpression.True).conditionIdValue(idModel).equals().conditionSelectionId(idModel21A);
			detail.addExpression().resultExpression(DependencyExpression.False).conditionIdValue(idModel).equals().conditionSelectionId(idModel20A);
			spec.add(idApplicationInBand, detail);
		}
		
		DependencyEngine2 engine = createEngine(holder, store);
		try {
			engine.requestChange(idModel, idModel20A);
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			
			assertTrue(cached.getProperty(idApplication).getAvailableListDetail().size() == 2);
			assertTrue(cached.getProperty(idApplication).getAvailableListDetail().get(0).getId().equals(idApplicationWdm));
			assertTrue(cached.getProperty(idApplication).getAvailableListDetail().get(1).getId().equals(idApplicationDrift));
			assertTrue(cached.getProperty(idApplication).getCurrentValue().equals(idApplicationWdm));
			
			{
				List<ChangedItemValue2> changed = cached.getChanged(idApplication);
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
		
		DependencySpecHolder2 holder = new DependencySpecHolder2();
		
		DepPropertyStore store = createPropertyStore();
		store.add(createListProperty(idModel, Arrays.asList(idModel20A, idModel21A, idModel22A, idModel23A), idModel20A));
		store.add(createListProperty(idApplication, Arrays.asList(idApplicationWdm, idApplicationDrift, idApplicationInBand), idApplicationInBand));

		{
			DependencySpec2 spec = new DependencySpec2(idApplication);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.ListItemVisible);
			detail.addExpression().resultExpression(DependencyExpression.True).conditionIdValue(idModel).equals().conditionSelectionId(idModel21A);
			detail.addExpression().resultExpression(DependencyExpression.True).conditionIdValue(idModel).equals().conditionSelectionId(idModel23A);
			detail.addExpression().resultExpression(DependencyExpression.False).conditionElse();
			spec.add(idApplicationInBand, detail);
		}
		DependencyEngine2 engine = createEngine(holder, store);
		try {
			// 20A
			engine.requestChange(idModel, idModel20A);
			CachedPropertyStore cached = engine.getCachedPropertyStore();
			
			assertEquals(2, cached.getProperty(idApplication).getAvailableListDetail().size());
			assertTrue(cached.getProperty(idApplication).getAvailableListDetail().get(0).getId().equals(idApplicationWdm));
			assertTrue(cached.getProperty(idApplication).getAvailableListDetail().get(1).getId().equals(idApplicationDrift));
			assertTrue(cached.getProperty(idApplication).getCurrentValue().equals(idApplicationWdm));
			
			{
				List<ChangedItemValue2> changed = cached.getChanged(idApplication);
				assertEquals(DependencyTargetElement.ListItemEnabled, changed.get(0).getElement());
				assertEquals(idApplicationInBand + ",true", changed.get(0).getValue());
			}
			{
				List<ChangedItemValue2> changed = cached.getChanged(idApplication);
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

		DependencySpecHolder2 holder = new DependencySpecHolder2();
				
		////////// Start Wavelength///////////
		{
			DependencySpec2 spec = new DependencySpec2(idStartWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultScript("if ($ID_OSA_MODEL.Value == %ID_OSA_MODEL_20A) {ret=$ID_OSA_STOP_WAVELENGTH.Value-$ID_OSA_START_WAVELENGTH.Value;} else if ($ID_OSA_MODEL.Value == %ID_OSA_MODEL_23A) {ret=$ID_OSA_STOP_WAVELENGTH.Value+$ID_OSA_START_WAVELENGTH.Value;} else {ret=100;}");
			spec.add(detail);
		}
		
		DependencyEngine2 engine = createEngine(holder, store);
			
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
		
		DependencySpecHolder2 holder = new DependencySpecHolder2();
		////////// Stop Wavelength///////////
		{
			DependencySpec2 spec = new DependencySpec2(idStopWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("$" + idStartWavelength + ".Value" + "+100");
			detail.setSettingDisabledBehavior(SettingDisabledBehavior.Reject);
			spec.add(detail);
		}
		////////// Start Wavelength///////////
		{
			DependencySpec2 spec = new DependencySpec2(idStartWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("$" + idStopWavelength + ".Value" + "-100");
			detail.setSettingDisabledBehavior(SettingDisabledBehavior.Reject);
			spec.add(detail);
		}
		DependencyEngine2 engine = createEngine(holder, store);
		
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
		DependencySpecHolder2 holder = new DependencySpecHolder2();
		////////// Band ///////////
		{
			DependencySpec2 spec = new DependencySpec2(idBand);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("%" + idBandManual).conditionExpression("$ID_OSA_START_WAVELENGTH.Value > 2000");
			detail.setSettingDisabledBehavior(SettingDisabledBehavior.Reject);
			spec.add(detail);
		}

		DependencyEngine2 engine = createEngine(holder, store);
		
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
		
		DependencySpecHolder2 holder = new DependencySpecHolder2();
		
		////////// Start Wavelength///////////
		{
			DependencySpec2 spec = new DependencySpec2(idStartWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("$" + idStopWavelength +".Value + 100");
			spec.add(detail);
		}
		
		DependencyEngine2 engine = createEngine(holder, store);
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

		DependencySpecHolder2 holder = new DependencySpecHolder2();
				
		////////// Start Wavelength///////////
		{
			DependencySpec2 spec = new DependencySpec2(idStartWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("$"+idCenterWavelength + ".Value - " + "$"+idSpanWavelength +".Value/2");
			detail.addExpression().resultExpression("1530").conditionIdValue(idBand).equals().conditionSelectionId(idBandC);
			spec.add(detail);
		}
		////////// Stop Wavelength///////////
		{
			DependencySpec2 spec = new DependencySpec2(idStopWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("$"+idCenterWavelength + ".Value + " + "$"+idSpanWavelength +".Value/2");
			detail.addExpression().resultExpression("1565").conditionIdValue(idBand).equals().conditionSelectionId(idBandC);
			spec.add(detail);
		}
		////////// Center Wavelength///////////
		{
			DependencySpec2 spec = new DependencySpec2(idCenterWavelength);
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
			DependencySpec2 spec = new DependencySpec2(idSpanWavelength);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("$"+idStopWavelength + ".Value - " + "$"+idStartWavelength +".Value");
			spec.add(detail);
		}

		{
			DependencyBuilder3 builder = new DependencyBuilder3(idStartWavelength, holder);
			assertEquals(4, builder.getLayerCount());
			
			List<DependencyProperty> layer0 = builder.getSpecs(0);
			assertEquals(2, layer0.size());
			assertEquals(idCenterWavelength, layer0.get(0).getId());
			assertEquals(idSpanWavelength, layer0.get(1).getId());
		}
		{
			DependencyBuilder3 builder = new DependencyBuilder3(idStopWavelength, holder);
			assertEquals(4, builder.getLayerCount());
		}
		{
			DependencyBuilder3 builder = new DependencyBuilder3(idCenterWavelength, holder);
			assertEquals(4, builder.getLayerCount());
		}
		{
			DependencyBuilder3 builder = new DependencyBuilder3(idSpanWavelength, holder);
			assertEquals(4, builder.getLayerCount());
		}

		DependencyEngine2 engine = createEngine(holder, store);
			
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
		
		DependencySpecHolder2 holder = new DependencySpecHolder2();
		
		////////// idOsaTestStart ///////////
		{
			DependencySpec2 spec = new DependencySpec2(idOsaTest);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("%" + idOsaTestStart).conditionExpression("$" + idAllTest + ".Value ==%" + idAllTestStart);
			detail.addExpression().resultExpression("%" + idOsaTestStop).conditionElse();
			spec.add(detail);
		}
		
		////////// idOtdrTestStart ///////////
		{
			DependencySpec2 spec = new DependencySpec2(idOtdrTest);
			holder.add(spec);
			DependencyExpressionHolder detail = new DependencyExpressionHolder(DependencyTargetElement.Value);
			detail.addExpression().resultExpression("%" + idOtdrTestStart).conditionExpression("$" + idAllTest + ".Value ==%" + idAllTestStart);
			detail.addExpression().resultExpression("%" + idOtdrTestStop).conditionElse();
			spec.add(detail);
		}
		
//		DependencyBuilder3 builder = new DependencyBuilder3(idAllTest, holder);
		
		DependencyEngine2 engine = createEngine(holder, store);
		
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
		
		DependencySpecHolder2 holder = new DependencySpecHolder2();
		DependencyEngine2 engine = createEngine(holder, store);
		
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
		
		DependencySpecHolder2 holder = new DependencySpecHolder2();
		
		{
			DependencySpec2 spec = new DependencySpec2(idPulse);
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
		DependencyEngine2 engine = createEngine(holder, store);
		
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
	private DependencyEngine2 createEngine(DependencySpecHolder2 holder, DepPropertyStore store) {
		DependencyEngine2 engine = new DependencyEngine2() {
			@Override
			protected DependencySpecHolder2 getDependencyHolder() {
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

	private ArgumentDefInterface argumentDef = new ArgumentDefInterface() {
		private List<String> arguments = Arrays.asList("defaultKey", "defaultValue", "min", "max", "decimal");
		@Override
		public int indexOf(String type, String key) {
			return arguments.indexOf(key);
		}

		@Override
		public List<String> get(String type) {
			return arguments;
		}
		
	};
	
	private SvProperty createDoubleProperty(String id, double defaultValue, String unit, double min, double max, int decimal) {
		PropertyDef def = new PropertyDef();
		def.setType("DoubleProperty");
		def.setOthers(Arrays.asList("", String.valueOf(defaultValue), String.valueOf(min), String.valueOf(max), String.valueOf(decimal)));
		def.setArgumentDef(argumentDef);
		def.setId(id);
		SvProperty ret = new SvProperty(def);	
		return ret;
	}

	private SvProperty createListProperty(String id, List<String> asList, String defaultId) {
		PropertyDef def = new PropertyDef();
		def.setOthers(Arrays.asList(defaultId));
		def.setArgumentDef(argumentDef);
		def.setId(id);
		SvProperty ret = new SvProperty(def);
		
		for (String e : asList) {
			def.getListDetail().add(new ListDetailElement(e));
		}
		
		return ret;
	}

}

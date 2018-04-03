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
import jp.silverbullet.SvPropertyStore;
import jp.silverbullet.XmlPersistent;
import jp.silverbullet.dependency.engine.RequestRejectedException;
import jp.silverbullet.dependency.speceditor3.DependencyExpressionHolder.SettingDisabledBehavior;
import jp.silverbullet.property.ArgumentDefInterface;
import jp.silverbullet.property.ListDetailElement;
import jp.silverbullet.property.PropertyDef;
import jp.silverbullet.spec.SpecElement;

class DependencySpecDetail2Test {

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

		CachedPropertyStore store = new CachedPropertyStore(createPropertyStore());
		
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
			assertTrue(store.getProperty(idBand).getCurrentValue().equals(idBandManual));
			assertTrue(store.getProperty(idStartWavelength).getCurrentValue().equals("1400.000"));
			
			assertTrue(store.getLogs().get(0).getId().equals(idStartWavelength));
			assertTrue(store.getLogs().get(0).getElement().equals(DependencyTargetElement.Value));
			assertTrue(store.getLogs().get(0).getValue().equals("1400.000"));
			
			assertTrue(store.getLogs().get(1).getId().equals(idBand));
			assertTrue(store.getLogs().get(1).getElement().equals(DependencyTargetElement.Value));
			assertTrue(store.getLogs().get(1).getValue().equals(idBandManual));
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}

		try {
			store.clearLogs();
			engine.requestChange(idBand, idBandC);
			assertTrue(store.getProperty(idStartWavelength).getCurrentValue().equals("1530.000"));
			assertTrue(store.getProperty(idStopWavelength).getCurrentValue().equals("1565.000"));
			assertTrue(store.getProperty(idBand).getCurrentValue().equals(idBandC));
			
			assertTrue(store.getLogs().size() == 3);
			
			DependencyChangedLog log0 = store.getLogs().get(0);
			assertTrue(log0.getId().equals(idBand));
			assertTrue(log0.getElement().equals(DependencyTargetElement.Value));
			assertTrue(log0.getValue().equals(idBandC));
			
			DependencyChangedLog log1 = store.getLogs().get(1);
			assertTrue(log1.getId().equals(idStartWavelength));
			assertTrue(log1.getElement().equals(DependencyTargetElement.Value));
			assertTrue(log1.getValue().equals("1530.000"));
		
			DependencyChangedLog log2 = store.getLogs().get(2);
			assertTrue(log2.getId().equals(idStopWavelength));
			assertTrue(log2.getElement().equals(DependencyTargetElement.Value));
			assertTrue(log2.getValue().equals("1565.000"));
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
		
		DepProperyStore store = createPropertyStore();
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
			assertTrue(store.getProperty(idStartWavelength).getCurrentValue().equals("1350.000"));
			assertTrue(store.getProperty(idStopWavelength).getCurrentValue().equals("1750.000"));
			assertTrue(store.getProperty(idSpanWavelength).getCurrentValue().equals("400.000"));
			assertTrue(store.getProperty(idCenterWavelength).getCurrentValue().equals("1550.000"));
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testOverLimit() {
		String idStartWavelength = "ID_OSA_START_WAVELENGTH";
		String idStopWavelength = "ID_OSA_STOP_WAVELENGTH";
		
		DepProperyStore store = createPropertyStore();
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
			assertTrue(store.getProperty(idStartWavelength).getCurrentValue().equals("1500.0"));
			assertTrue(store.getProperty(idStopWavelength).getCurrentValue().equals("1550.0"));
			engine.requestChange(idStartWavelength, "1600");
			assertTrue(store.getProperty(idStartWavelength).getCurrentValue().equals("1600.000"));
			assertTrue(store.getProperty(idStopWavelength).getCurrentValue().equals("1600.000"));
			
			engine.requestChange(idStopWavelength, "1500");
			assertTrue(store.getProperty(idStartWavelength).getCurrentValue().equals("1500.000"));
			assertTrue(store.getProperty(idStopWavelength).getCurrentValue().equals("1500.000"));
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
			assertTrue(store.getProperty(idApplication).getAvailableListDetail().size() == 2);
			assertTrue(store.getProperty(idApplication).getAvailableListDetail().get(0).getId().equals(idApplicationWdm));
			assertTrue(store.getProperty(idApplication).getAvailableListDetail().get(1).getId().equals(idApplicationDrift));
			assertTrue(store.getProperty(idApplication).getCurrentValue().equals(idApplicationWdm));
			
			assertTrue(store.getLogs().get(0).getId().equals(idApplication));
			assertTrue(store.getLogs().get(0).getElement().equals(DependencyTargetElement.ListItemEnabled));
			assertTrue(store.getLogs().get(0).getValue().equals(idApplicationInBand + ",true"));
			
			assertTrue(store.getLogs().get(1).getId().equals(idApplication));
			assertTrue(store.getLogs().get(1).getElement().equals(DependencyTargetElement.Value));
			assertTrue(store.getLogs().get(1).getValue().equals(idApplicationWdm));
			
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
		
		CachedPropertyStore store = new CachedPropertyStore(createPropertyStore());
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
			assertTrue(store.getProperty(idApplication).getAvailableListDetail().size() == 2);
			assertTrue(store.getProperty(idApplication).getAvailableListDetail().get(0).getId().equals(idApplicationWdm));
			assertTrue(store.getProperty(idApplication).getAvailableListDetail().get(1).getId().equals(idApplicationDrift));
			assertTrue(store.getProperty(idApplication).getCurrentValue().equals(idApplicationWdm));
			
			assertTrue(store.getLogs().get(0).getId().equals(idApplication));
			assertTrue(store.getLogs().get(0).getElement().equals(DependencyTargetElement.ListItemEnabled));
			assertTrue(store.getLogs().get(0).getValue().equals(idApplicationInBand + ",true"));
			
			assertTrue(store.getLogs().get(1).getId().equals(idApplication));
			assertTrue(store.getLogs().get(1).getElement().equals(DependencyTargetElement.Value));
			assertTrue(store.getLogs().get(1).getValue().equals(idApplicationWdm));
			
			// 22A
			engine.requestChange(idModel, idModel22A);
			assertTrue(store.getProperty(idApplication).getAvailableListDetail().size() == 2);
			assertTrue(store.getProperty(idApplication).getAvailableListDetail().get(0).getId().equals(idApplicationWdm));
			assertTrue(store.getProperty(idApplication).getAvailableListDetail().get(1).getId().equals(idApplicationDrift));
			assertTrue(store.getProperty(idApplication).getCurrentValue().equals(idApplicationWdm));
			
			// 21A
			engine.requestChange(idModel, idModel21A);
			assertTrue(store.getProperty(idApplication).getAvailableListDetail().size() ==3);
			assertTrue(store.getProperty(idApplication).getAvailableListDetail().get(0).getId().equals(idApplicationWdm));
			assertTrue(store.getProperty(idApplication).getAvailableListDetail().get(1).getId().equals(idApplicationDrift));
			assertTrue(store.getProperty(idApplication).getAvailableListDetail().get(2).getId().equals(idApplicationInBand));
			
			// 23A
			engine.requestChange(idModel, idModel23A);
			assertTrue(store.getProperty(idApplication).getAvailableListDetail().size() ==3);
			assertTrue(store.getProperty(idApplication).getAvailableListDetail().get(0).getId().equals(idApplicationWdm));
			assertTrue(store.getProperty(idApplication).getAvailableListDetail().get(1).getId().equals(idApplicationDrift));
			assertTrue(store.getProperty(idApplication).getAvailableListDetail().get(2).getId().equals(idApplicationInBand));
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
		
		DepProperyStore store = createPropertyStore();
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
			assertTrue(store.getProperty(idStartWavelength).getCurrentValue().equals("400.000"));
			
			engine.requestChange(idModel, idModel23A);
			assertTrue(store.getProperty(idStartWavelength).getCurrentValue().equals("2050.000"));

			engine.requestChange(idModel, idModel21A);
			assertTrue(store.getProperty(idStartWavelength).getCurrentValue().equals("100.000"));
			
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	void testLimitOverReject() {
		String idStartWavelength = "ID_OSA_START_WAVELENGTH";
		String idStopWavelength = "ID_OSA_STOP_WAVELENGTH";
		
		DepProperyStore store = createPropertyStore();
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
		try {
			engine.requestChange(idStartWavelength, "1600");
			assertTrue(store.getProperty(idStopWavelength).getCurrentValue().equals("1650.000"));
			
		} catch (RequestRejectedException e) {
			exception = true;
		}
		assertTrue(store.getProperty(idStopWavelength).getCurrentValue().equals("1600.0"));
		assertTrue(exception);
		
		try {
			exception = false;
			store.getProperty(idStartWavelength).setCurrentValue("1300.0");
			engine.requestChange(idStopWavelength, "1300");
			assertTrue(store.getProperty(idStartWavelength).getCurrentValue().equals("1250.000"));
			
		} catch (RequestRejectedException e) {
			exception = true;
		}
		assertTrue(store.getProperty(idStartWavelength).getCurrentValue().equals("1300.000"));
		assertTrue(exception);
	}
	
	@Test
	void testResultExpression() {
		String idStartWavelength = "ID_OSA_START_WAVELENGTH";
		String idStopWavelength = "ID_OSA_STOP_WAVELENGTH";
		String idBand = "ID_OSA_BAND";
		String idBandC = "ID_OSA_BAND_C";
		String idBandManual = "ID_OSA_BAND_MANUAL";
		
		DepProperyStore store = createPropertyStore();
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
			assertTrue(store.getProperty(idBand).getCurrentValue().equals(idBandManual));
			
		} catch (RequestRejectedException e) {

		}
	}
	
	@Test
	void testConditionOnly() {
		String idStartWavelength = "ID_OSA_START_WAVELENGTH";
		String idStopWavelength = "ID_OSA_STOP_WAVELENGTH";
		
		DepProperyStore store = createPropertyStore();
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
			assertTrue(store.getProperty(idStartWavelength).getCurrentValue().equals("1600.000"));
			assertTrue(store.getProperty(idStopWavelength).getCurrentValue().equals("1500.000"));
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}
	
	private DependencyEngine2 createEngine(DependencySpecHolder2 holder, DepProperyStore store) {
		DependencyEngine2 engine = new DependencyEngine2() {
			@Override
			protected DepProperyStore getPropertiesStore() {
				return store;
			}

			@Override
			protected DependencySpecHolder2 getDependencyHolder() {
				return holder;
			}
		};
		return engine;
	}

	private DepProperyStore createPropertyStore() {
		DepProperyStore store = new DepProperyStore() {
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

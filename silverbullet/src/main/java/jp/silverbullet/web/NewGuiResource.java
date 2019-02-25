package jp.silverbullet.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.property2.PropertyDef2;
import jp.silverbullet.web.ui.part2.Pane;
import jp.silverbullet.web.ui.part2.Tab;
import jp.silverbullet.web.ui.part2.TabPane;
import jp.silverbullet.web.ui.part2.UiBuilder;
import jp.silverbullet.web.ui.part2.WidgetGeneratorHelper;
import jp.silverbullet.web.ui.part2.UiBuilder.Layout;
import jp.silverbullet.web.ui.part2.UiBuilder.PropertyField;

@Path("/newGui")
public class NewGuiResource {
	@GET
	@Path("/getDesign")
	@Produces(MediaType.APPLICATION_JSON) 
	public UiBuilder getDesign() {
		return createDesign2();
	}

	private UiBuilder createDesign2() {
		UiBuilder builder = new UiBuilder();
		Pane pane = builder.createPane(UiBuilder.Layout.VERTICAL);
		pane.css("width", "1000").css("height", "1000");
		WidgetGeneratorHelper helper = new WidgetGeneratorHelper(StaticInstances.getInstance().getBuilderModel().getPropertiesHolder2());
		
		Pane applicationPane = pane.createPane(Layout.HORIZONTAL);
		helper.generateToggleButton("ID_APPLICATION", applicationPane).css("width", "100").css("height", "40").css("background-color", "lightgray");
//		applicationPane.createLabel("ID_APPLICATION", PropertyField.VALUE);
		
		//// OTDR
		Pane otdrPane = pane.createPane(Layout.VERTICAL).condition("ID_APPLICATION", "ID_APPLICATION_OTDR");
		otdrPane./*size(1000, 800).*/css("border-width", "1px").css("background-color", "lightgray").css("width", "1000").css("height", "800px");
		
		Pane distPane = otdrPane.createPane(Layout.HORIZONTAL);
		otdrPane.css("padding", "5");
		distPane.createLabel("ID_DISTANCERANGE", PropertyField.TITLE).css("width","150").css("height", "30").css("font-size", "16px").css("font-weight", "bold");
		distPane.createStaticText(":");
		distPane.css("border-width", "1px").css("border-color", "black").css("border-style", "solid");
		helper.generateToggleButton("ID_DISTANCERANGE", distPane).css("width","80").css("height", "30");
		
		Pane pulsePane = otdrPane.createPane(Layout.HORIZONTAL);
		pulsePane.css(distPane.css);
		pulsePane.createLabel("ID_PULSEWIDTH", PropertyField.TITLE).css("width","150").css("height", "30");
		pulsePane.createStaticText(":");
		helper.generateToggleButton("ID_PULSEWIDTH", pulsePane).css("width","80").css("height", "30");
		
		Pane otdrSetupPane = otdrPane.createPane(Layout.HORIZONTAL);
		Pane collectionPane = otdrSetupPane.createPane(Layout.HORIZONTAL).title("ID_COLLECMODE", PropertyField.TITLE);
		helper.generateToggleButton("ID_COLLECMODE", collectionPane).css("margin", "5");
		otdrSetupPane.createButton("ID_OTDR_TESTCONTROL").css("width","100").css("height", "60");
		otdrSetupPane.createButton("ID_COLLECMODE").css("width","130").css("height", "60");
		otdrSetupPane.createComboBox("ID_COLLECMODE").css("font-size", "29px");
		
		Pane averagePane = otdrSetupPane.createPane(Layout.HORIZONTAL);
		averagePane.css("width","250").css("height", "35");
		averagePane.css(distPane.css);
		averagePane.css("font-size", "20px");
		averagePane.createLabel("ID_AVERAGETIME", PropertyField.TITLE);
		averagePane.createStaticText(":");
		averagePane.createTextField("ID_AVERAGETIME", PropertyField.VALUE).css("width","50").css("height", "24");
		averagePane.createLabel("ID_AVERAGE_RESULT", PropertyField.VALUE);
		
		helper.generateTitledSetting("ID_OTDR_SAMPLINGPOINTS", otdrPane);
		otdrPane.createChart("ID_TRACE").css("width","600").css("height", "300");
		otdrPane.createTable("ID_TABLE").css("width","600").css("height", "200");
		
		//// SQA
		Pane sqaPane = pane.createPane(Layout.VERTICAL).condition("ID_APPLICATION", "ID_APPLICATION_SQA");
		sqaPane.css("font-size", "24px");
		sqaPane.css(otdrPane.css);
		
		Pane sqaSetupPane = sqaPane.createPane(Layout.HORIZONTAL);
		Pane ppgPane = sqaPane.createPane(Layout.HORIZONTAL).title("Pulse Pattern Generator");
	
		helper.generateTitledSetting("ID_PPG_PATTERN", ppgPane);
		helper.generateTitledSetting("ID_PPG_MODULATION", ppgPane);
		helper.generateTitledSetting("ID_PPG_FREQUENCY", ppgPane);
		
		Pane edPane = sqaPane.createPane(Layout.HORIZONTAL).title("Error Detector");
		helper.generateTitledSetting("ID_ED_PATTERN", edPane);
		helper.generateTitledSetting("ID_ED_MODULATION", edPane);
		helper.generateTitledSetting("ID_ED_FREQUENCY", edPane);
		
		/// OSCILLO
		Pane oscilloPane = pane.createPane(Layout.VERTICAL).condition("ID_APPLICATION", "ID_APPLICATION_OSCILLO");
		oscilloPane.css(otdrPane.css);
//		Pane optionPane = sqaPane.createPane(Layout.HORIZONTAL);
//		optionPane.createCheckBox("ID_OPTION_001").size(300, 20).position(10, 10);
//		optionPane.createToggleButton("ID_OPTION_001").size(100, 50).position(10, 10).css("line-height", "25px");;
//		optionPane.createButton("ID_OPTION_001").size(100, 50).position(10, 10).css("line-height", "25px");;
		oscilloPane.createButton("ID_OSC_TESTCONTROL").css("width","100").css("height", "50").css("line-height", "25px");
		Pane triggerPane = oscilloPane.createPane(Layout.VERTICAL).title("ID_OSC_TRIGGER", PropertyField.TITLE);
		triggerPane.createTextField("ID_OSC_TRIGGER", PropertyField.VALUE);
		triggerPane.createSlider("ID_OSC_TRIGGER");//.size(500, 30);
		oscilloPane.createImage("ID_OSC_EYEDIAGRAM").css("width","1000").css("height", "600");
		return builder;
	}
	
	private UiBuilder createDesign() {
		UiBuilder builder = new UiBuilder();
		Pane pane = builder.createPane(UiBuilder.Layout.VERTICAL);
		pane.css("width","1000").css("height", "1000");

		WidgetGeneratorHelper helper = new WidgetGeneratorHelper(StaticInstances.getInstance().getBuilderModel().getPropertiesHolder2());
		
		Pane paneRoot = pane.createPane(Layout.HORIZONTAL);
		helper.generateToggleButton("ID_APPLICATION", paneRoot).css("width","100").css("height", "40");
		
		Pane pane1 = pane.createPane(Layout.HORIZONTAL).condition("ID_APPLICATION", "ID_APPLICATION_OTDR");
		pane1.createLabel("ID_APPLICATION", UiBuilder.PropertyField.TITLE).css("width","100").css("height", "20");
		pane1.createComboBox("ID_APPLICATION").css("width","200").css("height", "20");
		pane1.createButton("ID_APPLICATION").css("width","200").css("height", "60");
		Pane panepp = pane.createPane(Layout.HORIZONTAL);

		
		pane1.createButton("ID_OTDR_TESTCONTROL");
		pane1.createComboBox("ID_OTDR_TESTCONTROL");
		{
			Pane subPane = pane.createPane(Layout.VERTICAL).condition("ID_APPLICATION", "ID_APPLICATION_SQA");
			subPane.createLabel("ID_TEST_MODE", UiBuilder.PropertyField.TITLE).css("width","100").css("height", "40");
			subPane.createTextField("ID_TEST_MODE", UiBuilder.PropertyField.VALUE).css("width","100").css("height", "40");
			subPane.createLabel("ID_TEST_MODE", UiBuilder.PropertyField.UNIT).css("width","20").css("height", "40");
			subPane.createTextField("ID_AVERAGETIME", UiBuilder.PropertyField.VALUE).css("width","100").css("height", "40");
		}
		{
			Pane subPane = pane.createPane(Layout.HORIZONTAL);
			subPane.createToggleButton("ID_APPLICATION", "ID_APPLICATION_OTDR").css("width","200").css("height", "40");
			subPane.createToggleButton("ID_APPLICATION", "ID_APPLICATION_SQA").css("width","200").css("height", "40");
			subPane.createComboBox("ID_PULSEWIDTH").css("width","100").css("height", "40");
			subPane.createComboBox("ID_DISTANCERANGE").css("width","100").css("height", "40");
			
			Pane pulsePane = pane.createPane(Layout.HORIZONTAL);
			pulsePane.createToggleButton("ID_PULSEWIDTH", "ID_PULSEWIDTH_1NS");
			pulsePane.createToggleButton("ID_PULSEWIDTH", "ID_PULSEWIDTH_10NS");
			pulsePane.createToggleButton("ID_PULSEWIDTH", "ID_PULSEWIDTH_20NS");
			pulsePane.createToggleButton("ID_PULSEWIDTH", "ID_PULSEWIDTH_50NS");
			pulsePane.createToggleButton("ID_PULSEWIDTH", "ID_PULSEWIDTH_100NS");
			pulsePane.createToggleButton("ID_PULSEWIDTH", "ID_PULSEWIDTH_500NS");
			
			Pane distPane = pane.createPane(Layout.HORIZONTAL);
			distPane.createToggleButton("ID_DISTANCERANGE", "ID_DISTANCERANGE_1KM");
			distPane.createToggleButton("ID_DISTANCERANGE", "ID_DISTANCERANGE_10KM");
			distPane.createToggleButton("ID_DISTANCERANGE", "ID_DISTANCERANGE_20KM");
			distPane.createToggleButton("ID_DISTANCERANGE", "ID_DISTANCERANGE_50KM");
			distPane.createToggleButton("ID_DISTANCERANGE", "ID_DISTANCERANGE_100KM");
			distPane.createToggleButton("ID_DISTANCERANGE", "ID_DISTANCERANGE_200KM");
			
		}
		{
			Pane subPane = pane.createPane(Layout.ABSOLUTE);
			subPane.createCheckBox("ID_OPTION_001").css("width","200").css("height", "20").position(10, 10);
			subPane.createCheckBox("ID_OPTION_001").css("width","300").css("height", "20").position(10, 10);
			subPane.createToggleButton("ID_OPTION_001").css("width","100").css("height", "50").position(10, 10);
			subPane.createButton("ID_OPTION_001").css("width","100").css("height", "50").position(10, 10);
			
			Pane subSubPane = subPane.createPane(Layout.HORIZONTAL);
			subSubPane.createLabel("ID_OPTION_001", UiBuilder.PropertyField.TITLE);
			subSubPane.createStaticText(":");
			subSubPane.createLabel("ID_OPTION_001", UiBuilder.PropertyField.VALUE).css("width","100").css("height", "40").position(10, 10);
		}
		
		{
			TabPane tabPane = pane.createTabPane();
			//Pane paneA = tabPane.createPane("ID_APPLICATION", UiBuilder.ProprtyField.TITLE, UiBuilder.Layout.VERTICAL);
			
			Tab paneA = tabPane.createTab(UiBuilder.Layout.VERTICAL).id("ID_APPLICATION", "ID_APPLICATION_SQA");
			paneA.createCheckBox("ID_OPTION_002");
			Tab paneB = tabPane.createTab(UiBuilder.Layout.VERTICAL).id("ID_APPLICATION", "ID_APPLICATION_OTDR");
			
			Pane PaneBB = paneB.createPane(Layout.HORIZONTAL);
			PaneBB.createLabel("ID_APPLICATION", UiBuilder.PropertyField.TITLE).css("width","400").css("height", "40");
			
			PaneBB.createComboBox("ID_APPLICATION");
			Tab paneOtdr = tabPane.createTab(UiBuilder.Layout.VERTICAL).title("Other");
			
			Pane distPane = paneOtdr.createPane(Layout.HORIZONTAL);
			helper.generateToggleButton("ID_DISTANCERANGE", distPane).css("width","100").css("height", "40");
			Pane pulsePane = paneOtdr.createPane(Layout.HORIZONTAL);
			helper.generateToggleButton("ID_PULSEWIDTH", pulsePane).css("width","100").css("height", "40");			
		}
		
		return builder;
	}
}

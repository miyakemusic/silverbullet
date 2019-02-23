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
		pane.size(1000, 1000);
		WidgetGeneratorHelper helper = new WidgetGeneratorHelper(StaticInstances.getInstance().getBuilderModel().getPropertiesHolder2());
		
		Pane applicationPane = pane.createPane(Layout.HORIZONTAL);
		helper.generateToggleButton("ID_APPLICATION", applicationPane).size(100, 40).css("background-color", "lightgray");
		applicationPane.createLabel("ID_APPLICATION", PropertyField.VALUE);
		
		//// OTDR
		Pane otdrPane = pane.createPane(Layout.VERTICAL).condition("ID_APPLICATION", "ID_APPLICATION_OTDR");
		otdrPane.size(1000, 800).css("border-width", "1px").css("background-color", "lightgray");
		
		Pane distPane = otdrPane.createPane(Layout.HORIZONTAL);
		otdrPane.css("padding", "5");
		distPane.createLabel("ID_DISTANCERANGE", PropertyField.TITLE).size(150, 30).css("font-size", "16px").css("font-weight", "bold");
		distPane.createStaticText(":");
		distPane.css("border-width", "1px").css("border-color", "black").css("border-style", "solid");
		helper.generateToggleButton("ID_DISTANCERANGE", distPane).size(80, 30);
		
		Pane pulsePane = otdrPane.createPane(Layout.HORIZONTAL);
		pulsePane.css(distPane.css);
		pulsePane.createLabel("ID_PULSEWIDTH", PropertyField.TITLE).size(150, 30);
		pulsePane.createStaticText(":");
		helper.generateToggleButton("ID_PULSEWIDTH", pulsePane).size(80, 30);
		
		Pane otdrSetupPane = otdrPane.createPane(Layout.HORIZONTAL);
		Pane collectionPane = otdrSetupPane.createPane(Layout.HORIZONTAL).title("ID_COLLECMODE", PropertyField.TITLE);
		helper.generateToggleButton("ID_COLLECMODE", collectionPane).css("margin", "5");
		otdrSetupPane.createButton("ID_OTDR_TESTCONTROL").size(100, 60);
		otdrSetupPane.createButton("ID_COLLECMODE").size(130, 60);
		otdrSetupPane.createComboBox("ID_COLLECMODE").css("font-size", "29px");
		
		Pane averagePane = otdrSetupPane.createPane(Layout.HORIZONTAL);
		averagePane.size(250, 35);
		averagePane.css(distPane.css);
		averagePane.css("font-size", "20px");
		averagePane.createLabel("ID_AVERAGETIME", PropertyField.TITLE);
		averagePane.createStaticText(":");
		averagePane.createTextField("ID_AVERAGETIME", PropertyField.VALUE).size(50, 24);
		averagePane.createLabel("ID_AVERAGE_RESULT", PropertyField.VALUE);
		
		otdrPane.createChart("ID_TRACE").size(600, 300);
		otdrPane.createTable("ID_TABLE").size(600, 200);
		
		//// SQA
		Pane sqaPane = pane.createPane(Layout.VERTICAL).condition("ID_APPLICATION", "ID_APPLICATION_SQA");
		sqaPane.css(otdrPane.css);
//		Pane optionPane = sqaPane.createPane(Layout.HORIZONTAL);
//		optionPane.createCheckBox("ID_OPTION_001").size(300, 20).position(10, 10);
//		optionPane.createToggleButton("ID_OPTION_001").size(100, 50).position(10, 10).css("line-height", "25px");;
//		optionPane.createButton("ID_OPTION_001").size(100, 50).position(10, 10).css("line-height", "25px");;
		sqaPane.createButton("ID_OSC_TESTCONTROL").size(100, 50).css("line-height", "25px");
		Pane triggerPane = sqaPane.createPane(Layout.VERTICAL).title("ID_OSC_TRIGGER", PropertyField.TITLE);
		triggerPane.createTextField("ID_OSC_TRIGGER", PropertyField.VALUE);
		triggerPane.createSlider("ID_OSC_TRIGGER");//.size(500, 30);
		sqaPane.createImage("ID_OSC_EYEDIAGRAM").size(1000, 600);
		return builder;
	}
	
	private UiBuilder createDesign() {
		UiBuilder builder = new UiBuilder();
		Pane pane = builder.createPane(UiBuilder.Layout.VERTICAL);
		pane.size(1000, 1000);

		WidgetGeneratorHelper helper = new WidgetGeneratorHelper(StaticInstances.getInstance().getBuilderModel().getPropertiesHolder2());
		
		Pane paneRoot = pane.createPane(Layout.HORIZONTAL);
		helper.generateToggleButton("ID_APPLICATION", paneRoot).size(100, 40);
		
		Pane pane1 = pane.createPane(Layout.HORIZONTAL).condition("ID_APPLICATION", "ID_APPLICATION_OTDR");
		pane1.createLabel("ID_APPLICATION", UiBuilder.PropertyField.TITLE).size(100, 20);
		pane1.createComboBox("ID_APPLICATION").size(200, 20);
		pane1.createComboBox("ID_APPLICATION").size(200, 20);
		pane1.createButton("ID_APPLICATION").size(200, 60);
		Pane panepp = pane.createPane(Layout.HORIZONTAL);

		
		pane1.createButton("ID_OTDR_TESTCONTROL");
		pane1.createComboBox("ID_OTDR_TESTCONTROL");
		{
			Pane subPane = pane.createPane(Layout.VERTICAL).condition("ID_APPLICATION", "ID_APPLICATION_SQA");
			subPane.createLabel("ID_TEST_MODE", UiBuilder.PropertyField.TITLE).size(100, 40);
			subPane.createTextField("ID_TEST_MODE", UiBuilder.PropertyField.VALUE).size(100, 40);
			subPane.createLabel("ID_TEST_MODE", UiBuilder.PropertyField.UNIT).size(20, 40);
			subPane.createTextField("ID_AVERAGETIME", UiBuilder.PropertyField.VALUE).size(100, 40);
			subPane.createTextField("ID_AVERAGETIME", UiBuilder.PropertyField.VALUE).size(100, 40);

		}
		{
			Pane subPane = pane.createPane(Layout.HORIZONTAL);
			subPane.createToggleButton("ID_APPLICATION", "ID_APPLICATION_OTDR").size(200, 40);
			subPane.createToggleButton("ID_APPLICATION", "ID_APPLICATION_SQA").size(200, 40);
			subPane.createComboBox("ID_PULSEWIDTH").size(100, 40);
			subPane.createComboBox("ID_DISTANCERANGE").size(100, 40);
			
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
			subPane.createCheckBox("ID_OPTION_001").size(200, 20).position(10, 10);
			subPane.createCheckBox("ID_OPTION_001").size(300, 20).position(10, 10);
			subPane.createToggleButton("ID_OPTION_001").size(100, 50).position(10, 10);
			subPane.createButton("ID_OPTION_001").size(100, 50).position(10, 10);
			
			Pane subSubPane = subPane.createPane(Layout.HORIZONTAL);
			subSubPane.createLabel("ID_OPTION_001", UiBuilder.PropertyField.TITLE);
			subSubPane.createStaticText(":");
			subSubPane.createLabel("ID_OPTION_001", UiBuilder.PropertyField.VALUE).size(100, 40).position(10, 10);
		}
		
		{
			TabPane tabPane = pane.createTabPane();
			//Pane paneA = tabPane.createPane("ID_APPLICATION", UiBuilder.ProprtyField.TITLE, UiBuilder.Layout.VERTICAL);
			
			Tab paneA = tabPane.createTab(UiBuilder.Layout.VERTICAL).id("ID_APPLICATION", "ID_APPLICATION_SQA");
			paneA.createCheckBox("ID_OPTION_002");
			Tab paneB = tabPane.createTab(UiBuilder.Layout.VERTICAL).id("ID_APPLICATION", "ID_APPLICATION_OTDR");
			
			Pane PaneBB = paneB.createPane(Layout.HORIZONTAL);
			PaneBB.createLabel("ID_APPLICATION", UiBuilder.PropertyField.TITLE).size(400, 40);
			
			PaneBB.createComboBox("ID_APPLICATION");
			Tab paneOtdr = tabPane.createTab(UiBuilder.Layout.VERTICAL).title("Other");
			
			Pane distPane = paneOtdr.createPane(Layout.HORIZONTAL);
			helper.generateToggleButton("ID_DISTANCERANGE", distPane).size(100, 40);
			Pane pulsePane = paneOtdr.createPane(Layout.HORIZONTAL);
			helper.generateToggleButton("ID_PULSEWIDTH", pulsePane).size(100, 40);			
		}
		
		return builder;
	}
}

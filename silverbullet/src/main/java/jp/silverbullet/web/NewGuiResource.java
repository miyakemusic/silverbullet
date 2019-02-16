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

@Path("/newGui")
public class NewGuiResource {
	@GET
	@Path("/getDesign")
	@Produces(MediaType.APPLICATION_JSON) 
	public UiBuilder getDesign() {
		UiBuilder builder = new UiBuilder();
		Pane pane = builder.createPane(UiBuilder.Layout.VERTICAL);
		WidgetGeneratorHelper helper = new WidgetGeneratorHelper(StaticInstances.getInstance().getBuilderModel().getPropertiesHolder2());

		
		Pane pane1 = pane.createPane(Layout.HORIZONTAL);
		pane1.createLabel("ID_APPLICATION", UiBuilder.ProprtyField.TITLE).size(100, 20);
		pane1.createComboBox("ID_APPLICATION").size(200, 20);
		pane1.createComboBox("ID_APPLICATION").size(200, 20);
		helper.generateToggleButton("ID_APPLICATION", pane1).size(100, 40);
		{
			Pane subPane = pane.createPane(Layout.VERTICAL);
			subPane.createLabel("ID_TEST_MODE", UiBuilder.ProprtyField.TITLE).size(100, 20);
			subPane.createTextField("ID_TEST_MODE", UiBuilder.ProprtyField.VALUE).size(100, 20);
			subPane.createLabel("ID_TEST_MODE", UiBuilder.ProprtyField.UNIT).size(20, 20);
			subPane.createTextField("ID_AVERAGETIME", UiBuilder.ProprtyField.VALUE).size(100, 20);
			subPane.createTextField("ID_AVERAGETIME", UiBuilder.ProprtyField.VALUE).size(100, 20);

		}
		{
			Pane subPane = pane.createPane(Layout.HORIZONTAL);
			subPane.createToggleButton("ID_APPLICATION", "ID_APPLICATION_OTDR").size(100, 20);
			subPane.createToggleButton("ID_APPLICATION", "ID_APPLICATION_SQA").size(100, 20);
			subPane.createComboBox("ID_PULSEWIDTH").size(100, 20);
			subPane.createComboBox("ID_DISTANCERANGE").size(50, 40);
			
			Pane pulsePane = pane.createPane(Layout.HORIZONTAL);
			pulsePane.createToggleButton("ID_PULSEWIDTH", "ID_PULSEWIDTH_1NS");
			pulsePane.createToggleButton("ID_PULSEWIDTH", "ID_PULSEWIDTH_10NS");
			pulsePane.createToggleButton("ID_PULSEWIDTH", "ID_PULSEWIDTH_20NS");
			pulsePane.createToggleButton("ID_PULSEWIDTH", "ID_PULSEWIDTH_50NS");
			pulsePane.createToggleButton("ID_PULSEWIDTH", "ID_PULSEWIDTH_100NS");
			pulsePane.createToggleButton("ID_PULSEWIDTH", "ID_PULSEWIDTH_500NS");
			
			Pane distPane = pane.createPane(Layout.HORIZONTAL);
			distPane.createToggleButton("ID_DISTANCERANGE", "ID_DISTANCERANGE_1KM");
			distPane.createToggleButton("ID_DISTANCERANGE", "ID_DISTANCERANGE_10KM").size(200, 20);
			distPane.createToggleButton("ID_DISTANCERANGE", "ID_DISTANCERANGE_20KM");
			distPane.createToggleButton("ID_DISTANCERANGE", "ID_DISTANCERANGE_50KM");
			distPane.createToggleButton("ID_DISTANCERANGE", "ID_DISTANCERANGE_100KM");
			distPane.createToggleButton("ID_DISTANCERANGE", "ID_DISTANCERANGE_200KM");
			
		}
		{
			Pane subPane = pane.createPane(Layout.ABSOLUTE);
			subPane.createCheckBox("ID_OPTION_001").size(200, 20).position(10, 10);
			subPane.createCheckBox("ID_OPTION_001").size(100, 20).position(10, 10);
			subPane.createToggleButton("ID_OPTION_001").size(100, 20).position(10, 10);
			
			Pane subSubPane = subPane.createPane(Layout.HORIZONTAL);
			subSubPane.createLabel("ID_OPTION_001", UiBuilder.ProprtyField.TITLE);
			subSubPane.createStaticText(":");
			subSubPane.createLabel("ID_OPTION_001", UiBuilder.ProprtyField.VALUE).size(100, 20).position(10, 10);
		}
		
		{
			TabPane tabPane = pane.createTabPane();
			//Pane paneA = tabPane.createPane("ID_APPLICATION", UiBuilder.ProprtyField.TITLE, UiBuilder.Layout.VERTICAL);
			
			Tab paneA = tabPane.createTab(UiBuilder.Layout.VERTICAL).id("ID_APPLICATION", "ID_APPLICATION_SQA");
			paneA.createCheckBox("ID_OPTION_002");
			Tab paneB = tabPane.createTab(UiBuilder.Layout.VERTICAL).id("ID_APPLICATION", "ID_APPLICATION_OTDR");
			
			Pane PaneBB = paneB.createPane(Layout.HORIZONTAL);
			PaneBB.createLabel("ID_APPLICATION", UiBuilder.ProprtyField.TITLE).size(400, 20);
			
			PaneBB.createComboBox("ID_APPLICATION");
			Tab paneOtdr = tabPane.createTab(UiBuilder.Layout.VERTICAL).title("Other");
			
			Pane distPane = paneOtdr.createPane(Layout.VERTICAL);
			
			helper.generateToggleButton("ID_DISTANCERANGE", distPane).size(200, 40);
			
		}
		
		return builder;
	}
}

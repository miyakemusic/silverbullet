package jp.silverbullet.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.web.ui.part2.Pane;
import jp.silverbullet.web.ui.part2.TabPane;
import jp.silverbullet.web.ui.part2.UiBuilder;
import jp.silverbullet.web.ui.part2.UiBuilder.Layout;

@Path("/newGui")
public class NewGuiResource {
	@GET
	@Path("/getDesign")
	@Produces(MediaType.APPLICATION_JSON) 
	public UiBuilder getDesign() {
		UiBuilder builder = new UiBuilder();
		Pane pane = builder.createPane(UiBuilder.Layout.VERTICAL);
		
		Pane pane1 = pane.createPane(Layout.HORIZONTAL);
		pane1.createLabel("$ID_APPLICATION", UiBuilder.ProprtyField.TITLE).size(100, 20);
		pane1.createComboBox("$ID_APPLICATION", UiBuilder.ProprtyField.VALUE).size(200, 20);
		pane1.createComboBox("$ID_APPLICATION", UiBuilder.ProprtyField.VALUE).size(200, 20);
		
		{
			Pane subPane = pane.createPane(Layout.VERTICAL);
			subPane.createLabel("$ID_TEST_MODE", UiBuilder.ProprtyField.TITLE).size(100, 20);
			subPane.createTextField("$ID_TEST_MODE", UiBuilder.ProprtyField.VALUE).size(100, 20);
			subPane.createLabel("$ID_TEST_MODE", UiBuilder.ProprtyField.UNIT).size(20, 20);
			subPane.createTextField("$ID_AVERAGETIME", UiBuilder.ProprtyField.VALUE).size(100, 20);
			subPane.createTextField("$ID_AVERAGETIME", UiBuilder.ProprtyField.VALUE).size(100, 20);

		}
		{
			Pane subPane = pane.createPane(Layout.HORIZONTAL);
			subPane.createToggleButton("$ID_APPLICATION", "ID_APPLICATION_OTDR").size(100, 20);
			subPane.createToggleButton("$ID_APPLICATION", "ID_APPLICATION_SQA").size(100, 20);
		}
		{
			Pane subPane = pane.createPane(Layout.ABSOLUTE);
			subPane.createCheckBox("$ID_OPTION_001").size(200, 20).position(10, 10);
			subPane.createCheckBox("$ID_OPTION_001").size(100, 20).position(10, 10);
			subPane.createToggleButton("$ID_OPTION_001").size(100, 20).position(10, 10);
			
			Pane subSubPane = subPane.createPane(Layout.HORIZONTAL);
			subSubPane.createLabel("$ID_OPTION_001", UiBuilder.ProprtyField.TITLE);
			subSubPane.createStaticText(":");
			subSubPane.createLabel("$ID_OPTION_001", UiBuilder.ProprtyField.VALUE).size(100, 20).position(10, 10);
		}
		
		{
			TabPane tabPane = pane.createTab();
			Pane paneA = tabPane.createPane("$ID_APPLICATION", UiBuilder.ProprtyField.TITLE, UiBuilder.Layout.VERTICAL);
			paneA.createCheckBox("$ID_OPTION_002");
			Pane paneB = tabPane.createPane("$ID_RESULT", UiBuilder.ProprtyField.TITLE, UiBuilder.Layout.VERTICAL);
			
			Pane PaneBB = paneB.createPane(Layout.HORIZONTAL);
			PaneBB.createLabel("$ID_APPLICATION", UiBuilder.ProprtyField.TITLE).size(400, 20);
			
			PaneBB.createComboBox("$ID_APPLICATION", UiBuilder.ProprtyField.VALUE);
			Pane paneOtdr = tabPane.createPane("Other", UiBuilder.ProprtyField.STATICTEXT, UiBuilder.Layout.VERTICAL);
			
		}
		
		return builder;
	}
}

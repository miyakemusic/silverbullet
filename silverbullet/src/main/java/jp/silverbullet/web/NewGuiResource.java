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
		pane.createComboBox("$ID_APPLICATION", UiBuilder.ProprtyElement.VALUE).size(400, 20);
		
		{
			Pane subPane = pane.createPane(Layout.VERTICAL);
			subPane.createStaticText("$ID_TESTMODE", UiBuilder.ProprtyElement.TITLE).size(100, 20);
			subPane.createTextField("$ID_TESTMODE", UiBuilder.ProprtyElement.VALUE).size(100, 20);
			subPane.createStaticText("$ID_TESTMODE", UiBuilder.ProprtyElement.UNIT).size(20, 20);
		}
		{
			Pane subPane = pane.createPane(Layout.HORIZONTAL);
			subPane.createToggleButton("$ID_APPLICATION.ID_APPLICATION_AAA").size(100, 20);
			subPane.createToggleButton("$ID_APPLICATION.ID_APPLICATION_BBB").size(100, 20);
		}
		{
			Pane subPane = pane.createPane(Layout.ABSOLUTE);
			subPane.createCheckBox("$ID_OPTION_A").size(100, 20).position(10, 10);
		}
		
		{
			TabPane tabPane = pane.createTab();
			Pane paneA = tabPane.createPane("$ID_APPLICATION", UiBuilder.ProprtyElement.TITLE, UiBuilder.Layout.VERTICAL);
			paneA.createCheckBox("$ID_OPTION_A");
			Pane paneB = tabPane.createPane("$ID_RESULT", UiBuilder.ProprtyElement.TITLE, UiBuilder.Layout.VERTICAL);
			paneB.createComboBox("$ID_APPLICATION", UiBuilder.ProprtyElement.VALUE);
			Pane paneOtdr = tabPane.createPane("Other", UiBuilder.ProprtyElement.STATICTEXT, UiBuilder.Layout.VERTICAL);
			
		}
		
		return builder;
	}
}

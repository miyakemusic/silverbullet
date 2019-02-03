package jp.silverbullet.web.ui;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jp.silverbullet.web.ui.part2.Pane;
import jp.silverbullet.web.ui.part2.TabPane;
import jp.silverbullet.web.ui.part2.UiBuilder;
import jp.silverbullet.web.ui.part2.UiBuilder.Layout;

class UiBuilderTest {

	@Test
	void test() {
		UiBuilder builder = new UiBuilder();
		Pane pane = builder.createPane(UiBuilder.Layout.VERTICAL);
		pane.createComboBox("$ID_APPLICATION", UiBuilder.ProprtyElement.VALUE).size(20, 400);
		
		{
			Pane subPane = pane.createPane(Layout.VERTICAL);
			subPane.createStaticText("$ID_TESTMODE", UiBuilder.ProprtyElement.TITLE).size(20, 400);
			subPane.createTextField("$ID_TESTMODE", UiBuilder.ProprtyElement.VALUE).size(20, 400);
			subPane.createStaticText("$ID_TESTMODE", UiBuilder.ProprtyElement.UNIT).size(20, 20);
		}
		{
			Pane subPane = pane.createPane(Layout.HORIZONTAL);
			subPane.createToggleButton("$ID_APPLICATION.ID_APPLICATION_AAA").size(20, 400);
			subPane.createToggleButton("$ID_APPLICATION.ID_APPLICATION_BBB").size(20, 400);
		}
		{
			Pane subPane = pane.createPane(Layout.ABSOLUTE);
			subPane.createCheckBox("$ID_OPTION_A");
		}
		
		{
			TabPane tabPane = pane.createTab();
			Pane paneA = tabPane.createPane("$ID_APPLICATION.ID_APPLICATION_AAA", UiBuilder.ProprtyElement.TITLE, UiBuilder.Layout.VERTICAL);
			Pane paneB = tabPane.createPane("$ID_RESULT", UiBuilder.ProprtyElement.TITLE, UiBuilder.Layout.VERTICAL);
			Pane paneOtdr = tabPane.createPane("Other", UiBuilder.ProprtyElement.STATICTEXT, UiBuilder.Layout.VERTICAL);
			
		}
		
	}

}

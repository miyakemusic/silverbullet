package jp.silverbullet.web.ui;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jp.silverbullet.web.ui.part2.Layout;
import jp.silverbullet.web.ui.part2.Pane;
import jp.silverbullet.web.ui.part2.TabPane;
import jp.silverbullet.web.ui.part2.UiBuilder;
import jp.silverbullet.web.ui.part2.UiBuilder.PropertyField;

class UiBuilderTest {

	@Test
	void test() {
		UiBuilder builder = new UiBuilder();
		Pane pane = builder.createPane(Layout.VERTICAL);
		pane.createComboBox("$ID_APPLICATION").css("width", "20").css("height", "400");
		
		{
			Pane subPane = pane.createPane(Layout.VERTICAL);
			subPane.createLabel("$ID_TESTMODE", PropertyField.TITLE).css("width", "20").css("height", "400");
			subPane.createTextField("$ID_TESTMODE", PropertyField.VALUE).css("width", "20").css("height", "400");
			subPane.createLabel("$ID_TESTMODE", PropertyField.UNIT).css("width", "20").css("height", "400");
		}
		{
			Pane subPane = pane.createPane(Layout.HORIZONTAL);
			subPane.createToggleButton("$ID_APPLICATION", "ID_APPLICATION_AAA").css("width", "20").css("height", "400");
			subPane.createToggleButton("$ID_APPLICATION", "ID_APPLICATION_BBB").css("width", "20").css("height", "400");
		}
		{
			Pane subPane = pane.createPane(Layout.ABSOLUTE);
			subPane.createCheckBox("$ID_OPTION_A");
		}
		
		{
			TabPane tabPane = pane.createTabPane();
//			Pane paneA = tabPane.createPane("$ID_APPLICATION.ID_APPLICATION_AAA", UiBuilder.ProprtyField.TITLE, UiBuilder.Layout.VERTICAL);
//			Pane paneB = tabPane.createPane("$ID_RESULT", UiBuilder.ProprtyField.TITLE, UiBuilder.Layout.VERTICAL);
//			Pane paneOtdr = tabPane.createPane("Other", UiBuilder.ProprtyField.STATICTEXT, UiBuilder.Layout.VERTICAL);
			
		}
		
	}

}

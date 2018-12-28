package jp.silverbullet.web.ui;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import jp.silverbullet.property2.PropertyFactory;
import jp.silverbullet.property2.PropertyHolder2;
import jp.silverbullet.property2.PropertyType2;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.property2.RuntimePropertyStore;
import jp.silverbullet.web.UiLayoutListener;

class UiLayoutHolderTest {

	@Test
	void testUiLayoutHolder() throws Exception {
		PropertyFactory factory = new PropertyFactory();
		PropertyHolder2 holder = new PropertyHolder2();
		RuntimePropertyStore store = new RuntimePropertyStore(holder);
		holder.addProperty(factory.create("ID_LIST", PropertyType2.List).option("ID_LIST_A", "A", "").option("ID_LIST_B", "B", ""));
		holder.addProperty(factory.create("ID_LIST2", PropertyType2.List).option("ID_LIST2_A", "A", "").option("ID_LIST2_B", "B", "").option("ID_LIST2_C", "C", ""));
		holder.addProperty(factory.create("ID_NUMERIC", PropertyType2.Numeric));
		holder.addProperty(factory.create("ID_BOOLEAN", PropertyType2.Boolean));
		holder.addProperty(factory.create("ID_TEXT", PropertyType2.Text));

		
		PropertyGetter getter = new PropertyGetter() {

			@Override
			public RuntimeProperty getProperty(String id) {
				return store.get(id);
			}

			@Override
			public RuntimeProperty getProperty(String id, int index) {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
		UiLayoutHolder uiHolder = new UiLayoutHolder(getter);
		uiHolder.createNewFile("newFile");
		UiLayout layout = uiHolder.switchFile("newFile");
		layout.addListener(new UiLayoutListener() {
			@Override
			public void onLayoutChange(String div, String currentFilename) {
				// TODO Auto-generated method stub
				
			}
		});
		JsWidget root = layout.getRoot();
		
		layout.addPanel(String.valueOf(root.getUnique()));
		
		JsWidget panel = root.getChildren().get(0);
		
		layout.addPanel(String.valueOf(panel.getUnique()));
		
		panel = panel.getChildren().get(0);
		
		panel.setCustomElemnt(CustomProperties.GUI_ID, "MYPANEL");
		assertEquals(JsWidget.VERTICALLAYOUT, panel.getLayout());
		
		layout.addWidget(String.valueOf(panel.getUnique()), Arrays.asList("ID_LIST", "ID_LIST2", "ID_NUMERIC", "ID_TEXT", "ID_BOOLEAN"));
		
		{
			JsWidget child = panel.getChildren().get(0);
			assertEquals("ID_LIST", child.getId());
			assertEquals(JsWidget.RADIOBUTTON, child.getWidgetType());
		}
		
		{
			JsWidget child = panel.getChildren().get(1);
			assertEquals("ID_LIST2", child.getId());
			assertEquals(JsWidget.COMBOBOX, child.getWidgetType());
		}
		
		{
			JsWidget child = panel.getChildren().get(2);
			assertEquals("ID_NUMERIC", child.getId());
			assertEquals(JsWidget.TEXTFIELD, child.getWidgetType());
		}
	
		{
			JsWidget child = panel.getChildren().get(3);
			assertEquals("ID_TEXT", child.getId());
			assertEquals(JsWidget.TEXTFIELD, child.getWidgetType());
		}
		
		{
			JsWidget child = panel.getChildren().get(4);
			assertEquals("ID_BOOLEAN", child.getId());
			assertEquals(JsWidget.CHECKBOX, child.getWidgetType());
		}
		
		{
			JsWidget child = panel.getChildren().get(0);
			uiHolder.changeId("ID_LIST", "ID_NEW_LIST");
			assertEquals("ID_NEW_LIST", child.getId());
		}
		
		{
			JsWidget subTree = layout.getSubTree("MYPANEL");
			assertEquals(5, subTree.getChildren().size());
		}
		
		assertEquals(5, layout.getWidget(panel.getUniqueText()).getChildren().size());
		
		layout.remove(String.valueOf(panel.getChildren().get(4).getUnique()));
		assertEquals(4, layout.getWidget(panel.getUniqueText()).getChildren().size());
		
		layout.addPanel(String.valueOf(root.getUnique()));
		layout.cutPaste(root.getChildren().get(1).getUniqueText(), root.getChildren().get(0).getChildren().get(0).getUniqueText());
		assertEquals(0, root.getChildren().get(0).getChildren().size());
		assertEquals(4, root.getChildren().get(1).getChildren().get(0).getChildren().size());
	}

}

package jp.silverbullet.web.ui;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import jp.silverbullet.property2.PropertyFactory;
import jp.silverbullet.property2.PropertyHolder2;
import jp.silverbullet.property2.PropertyType2;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.property2.RuntimePropertyStore;

public class UiLayoutHolderTest {

	@Test
	public void testUiLayoutHolder() throws Exception {
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
		uiHolder.createDefault();
		assertEquals("default.ui", uiHolder.getFileList().get(0));
		assertEquals(0, uiHolder.getCurrentUi().getRoot().getChildren().size());
		
		UiLayoutListenerImpl listener = new UiLayoutListenerImpl();
		uiHolder.addListener(listener);
		uiHolder.createNewFile("newFile.ui");
		
		assertEquals(0, listener.getLog().size());
		
		assertEquals(2, uiHolder.getFileList().size());
		
		UiLayout layout = uiHolder.switchFile("newFile.ui");

		JsWidget root = layout.getRoot();
		
		layout.addPanel(String.valueOf(root.getUnique()));
		
		assertEquals(root.getUniqueText(), listener.getLog().get(0).getKey());
		
		JsWidget panel = root.getChildren().get(0);
		
		layout.addPanel(String.valueOf(panel.getUnique()));
		
		panel = panel.getChildren().get(0);
		
		panel.setCustomElemnt(CustomProperties.GUI_ID, "MYPANEL");
		assertEquals(JsWidget.VERTICALLAYOUT, panel.getLayout());
		
		assertEquals(panel, layout.getDesign("MYPANEL"));
		assertEquals(root, layout.getDesign(""));
		
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
		assertEquals(panel.getUniqueText(), listener.getLastLog().getKey());
		
		layout.addPanel(String.valueOf(root.getUnique()));
		layout.cutPaste(root.getChildren().get(1).getUniqueText(), root.getChildren().get(0).getChildren().get(0).getUniqueText());
		assertEquals(0, root.getChildren().get(0).getChildren().size());
		
		uiHolder.createNewFile("newFile2.ui");
		uiHolder.switchFile("newFile2.ui");
		uiHolder.getCurrentUi().addPanel(uiHolder.getCurrentUi().getRoot().getUniqueText());
		uiHolder.getCurrentUi().addWidget(uiHolder.getCurrentUi().getRoot().getChildren().get(0).getUniqueText(), Arrays.asList("ID_BOOLEAN"));
		
		uiHolder.save(".");
		
		assertEquals(true, Files.exists(Paths.get("newFile.ui")));
		assertEquals(true, Files.exists(Paths.get("newFile2.ui")));
		
		listener.clearLog();
		uiHolder.load(".");
		assertEquals(3, uiHolder.getFileList().size());
		
		uiHolder.switchFile("newFile.ui");
		
		panel = uiHolder.getCurrentUi().getRoot().getChildren().get(1);
		panel = panel.getChildren().get(0);
		
		assertEquals(JsWidget.VERTICALLAYOUT, panel.getLayout());
		
		{
			JsWidget child = panel.getChildren().get(0);
			assertEquals("ID_NEW_LIST", child.getId());
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
				
		uiHolder.removeFile("newFile.ui");
		uiHolder.removeFile("newFile2.ui");
		
		assertEquals(1, uiHolder.getFileList().size());
		assertEquals("default.ui", uiHolder.getFileList().get(0));
		
		assertEquals("default.ui", uiHolder.getCurrentFilename());
		
		boolean ret = uiHolder.removeFile("default.ui");
		assertEquals(false, ret);
		
		JsWidget addedPanel = uiHolder.getCurrentUi().addPanel(uiHolder.getCurrentUi().root.getUniqueText());
		assertEquals(uiHolder.getCurrentUi().root.getUniqueText(), listener.getLastLog().getKey());
		
		addedPanel.setCustomElemnt(CustomProperties.GUI_ID, "ForDialog");
		JsWidget dialog = uiHolder.getCurrentUi().addDialog(uiHolder.getCurrentUi().root.getUniqueText());
		assertEquals(uiHolder.getCurrentUi().root.getUniqueText(), listener.getLastLog().getKey());
		
		listener.clearLog();
		uiHolder.getCurrentUi().resize(dialog.getUniqueText(), "123", "456");
		assertEquals(dialog.getUniqueText(), listener.getLastLog().getKey());
		assertEquals("123", dialog.getWidth());
		assertEquals("456", dialog.getHeight());
		
		listener.clearLog();
		uiHolder.getCurrentUi().move(dialog.getUniqueText(), "123", "456");
		assertEquals(dialog.getUniqueText(), listener.getLastLog().getKey());
		assertEquals("123", dialog.getLeft());
		assertEquals("456", dialog.getTop());
		
		uiHolder.getCurrentUi().clear();
		assertEquals(0, uiHolder.getCurrentUi().root.getChildren().size());
		uiHolder.getCurrentUi().addPanel(uiHolder.getCurrentUi().root.getUniqueText());
		uiHolder.getCurrentUi().addWidget(uiHolder.getCurrentUi().getRoot().getChildren().get(0).getUniqueText(), Arrays.asList("ID_NUMERIC"));

		assertEquals("ID_NUMERIC", uiHolder.getCurrentUi().getRoot().getChildren().get(0).getChildren().get(0).getId());

		// copy and paste
		uiHolder.getCurrentUi().copyPaste(uiHolder.getCurrentUi().getRoot().getUniqueText(),
				uiHolder.getCurrentUi().getRoot().getChildren().get(0).getUniqueText());
		// original stays the same
		assertEquals("ID_NUMERIC", uiHolder.getCurrentUi().getRoot().getChildren().get(0).getChildren().get(0).getId());
		// copied to another panel
		assertEquals("ID_NUMERIC", uiHolder.getCurrentUi().getRoot().getChildren().get(1).getChildren().get(0).getId());

		
		uiHolder.getCurrentUi().clear();
		uiHolder.getCurrentUi().addTab(uiHolder.getCurrentUi().getRoot().getUniqueText());
		assertEquals(JsWidget.TAB, uiHolder.getCurrentUi().getRoot().getChildren().get(0).getWidgetType());
		
		uiHolder.createNewFile("");
		assertEquals(2, uiHolder.getFileList().size());
		Thread.sleep(100);
		uiHolder.createNewFile("");
		assertEquals(3, uiHolder.getFileList().size());

		uiHolder.createNewFile("aaaa");
		assertEquals(4, uiHolder.getFileList().size());
		assertEquals("aaaa.ui", uiHolder.getFileList().get(3));
		
//		JsWidget miyake = uiHolder.getCurrentUi().getDesign("MYPANEL");
//		assertEquals("MYPANEL", miyake.getCustomElement(CustomProperties.GUI_ID));
		
	}

	@Test
	public void testArray() throws Exception {
		PropertyFactory factory = new PropertyFactory();
		PropertyHolder2 holder = new PropertyHolder2();
		RuntimePropertyStore store = new RuntimePropertyStore(holder);
		holder.addProperty(factory.create("ID_LIST", PropertyType2.List).option("ID_LIST_A", "A", "").option("ID_LIST_B", "B", "")
				.arraySize(5));
		
		UiLayoutHolder uiHolder = new UiLayoutHolder(new PropertyGetter() {
			@Override
			public RuntimeProperty getProperty(String id) {
				return store.get(id);
			}

			@Override
			public RuntimeProperty getProperty(String id, int index) {
				return store.get(id, index);
			}	
		});
		
		uiHolder.createDefault();
		
		// test auto dynamically update array panel
		uiHolder.getCurrentUi().addWidget(uiHolder.getCurrentUi().getRoot().getUniqueText(), Arrays.asList("ID_LIST"));
		uiHolder.getCurrentUi().setCustomElement(uiHolder.getCurrentUi().getRoot().getUniqueText(), CustomProperties.ARRAY, CustomProperties.True);
		uiHolder.getCurrentUi().doAutoDynamicPanel();
		assertEquals(5, uiHolder.getCurrentUi().getRoot().getChildren().size());
		
		store.get("ID_LIST").setSize(10);
		uiHolder.getCurrentUi().doAutoDynamicPanel();
		assertEquals(10, uiHolder.getCurrentUi().getRoot().getChildren().size());
		
		store.get("ID_LIST").setSize(1);
		uiHolder.getCurrentUi().doAutoDynamicPanel();
		assertEquals(1, uiHolder.getCurrentUi().getRoot().getChildren().size());
		uiHolder.getCurrentUi().setCustomElement(uiHolder.getCurrentUi().getRoot().getUniqueText(), CustomProperties.ARRAY, CustomProperties.False);
		store.get("ID_LIST").setSize(10);
		uiHolder.getCurrentUi().doAutoDynamicPanel(); // does not change. because Array check is disabled.
		assertEquals(1, uiHolder.getCurrentUi().getRoot().getChildren().size());
		
		// test manual array
		uiHolder.getCurrentUi().addArray(uiHolder.getCurrentUi().getRoot().getChildren().get(0).getUniqueText());
		assertEquals(10, uiHolder.getCurrentUi().getRoot().getChildren().size());

		// test manual array (on panel)
		  // -- once, back to 1
		uiHolder.getCurrentUi().getRoot().getChildren().clear();
//		uiHolder.getCurrentUi().addWidget(uiHolder.getCurrentUi().getRoot().getUniqueText(), Arrays.asList("ID_LIST"));
		assertEquals(0, uiHolder.getCurrentUi().getRoot().getChildren().size());
		  // --
		store.get("ID_LIST").setSize(10);
		JsWidget newPanel = uiHolder.getCurrentUi().addPanel(uiHolder.getCurrentUi().getRoot().getUniqueText());
		uiHolder.getCurrentUi().addWidget(newPanel.getUniqueText(), Arrays.asList("ID_LIST"));
		uiHolder.getCurrentUi().addArray(newPanel.getUniqueText());
		assertEquals(10, uiHolder.getCurrentUi().getRoot().getChildren().size());		
		
		// test updateBooleanProperty
		uiHolder.getCurrentUi().updateBooleanProperty(uiHolder.getCurrentUi().getRoot().getChildren().get(0).getUniqueText(), "editable", true);
		assertEquals(true, uiHolder.getCurrentUi().getRoot().getChildren().get(0).getEditable());
		uiHolder.getCurrentUi().updateBooleanProperty(uiHolder.getCurrentUi().getRoot().getChildren().get(0).getUniqueText(), "editable", false);
		assertEquals(false, uiHolder.getCurrentUi().getRoot().getChildren().get(0).getEditable());

		// test updateProperty
		uiHolder.getCurrentUi().updateProperty(uiHolder.getCurrentUi().getRoot().getChildren().get(0).getUniqueText(), "fontsize", "30");
		assertEquals("30", uiHolder.getCurrentUi().getRoot().getChildren().get(0).getFontsize());
		
	}
}

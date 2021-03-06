package jp.silverbullet.property2;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jp.silverbullet.core.property2.PropertyDef2;
import jp.silverbullet.core.property2.PropertyFactory;
import jp.silverbullet.core.property2.PropertyHolder2;
import jp.silverbullet.core.property2.PropertyType2;
import jp.silverbullet.web.JsonTable;
import jp.silverbullet.web.WebTableConverter;

public class WebTableConverterTest {

	@Test
	public void test() {
		PropertyHolder2 holder = new PropertyHolder2();
		WebTableConverter converter = new WebTableConverter(holder);
		PropertyFactory factory = new PropertyFactory();
		
		holder.addProperty(factory.createBoolean("ID_BOOLEAN"));
		holder.addProperty(factory.createChart("ID_CHART"));
		holder.addProperty(factory.createList("ID_LIST"));
		holder.addProperty(factory.createNumeric("ID_NUMERIC"));
		holder.addProperty(factory.createTable("ID_TABLE"));
		holder.addProperty(factory.createText("ID_TEXT"));
		
		{
			JsonTable table = converter.createIdTable(PropertyType2.NotSpecified);
			assertEquals(6, table.table.size());
		}
		{
			JsonTable table = converter.createIdTable(PropertyType2.List);
			assertEquals(1, table.table.size());
		}
		
		converter.updateMainField("ID_NUMERIC", PropertyDef2.UNIT, "Hz");
		assertEquals("Hz", holder.get("ID_NUMERIC").getUnit());
	}

	@Test
	public void testOption() {
		PropertyHolder2 holder = new PropertyHolder2();
		WebTableConverter converter = new WebTableConverter(holder);
		PropertyFactory factory = new PropertyFactory();
		try {
			holder.addProperty(factory.createList("ID_LIST").option("ID_LIST_A", "", "").option("ID_LIST_B", "", ""));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonTable table = converter.createOptionTable("ID_LIST");
		assertEquals("No.", table.header[0]);
		assertEquals("ID", table.header[1]);
		assertEquals("Title", table.header[2]);
		assertEquals("Comment", table.header[3]);
	}
	
	@Test
	public void testUpateValue() {
		PropertyHolder2 holder = new PropertyHolder2();
		WebTableConverter converter = new WebTableConverter(holder);
		PropertyFactory factory = new PropertyFactory();
		try {
			holder.addProperty(factory.createNumeric("ID_NUMERIC").title("Numeric").unit("Hz").min(-100).max(100).decimals(3));
			holder.addProperty(factory.createNumeric("ID_LIST").title("List").option("ID_LIST_A", "A", "").option("ID_LIST_B", "B", "").defaultId("ID_LIST_A"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		converter.updateMainField("ID_NUMERIC", PropertyDef2.PRESENTATION, "New Title");
		assertEquals("New Title", holder.get("ID_NUMERIC").getTitle());

		converter.updateMainField("ID_NUMERIC", PropertyDef2.UNIT, "GHz");
		assertEquals("GHz", holder.get("ID_NUMERIC").getUnit());
		
		converter.updateMainField("ID_NUMERIC", PropertyDef2.MIN, "-100");
		assertTrue(-100.0 == holder.get("ID_NUMERIC").getMin());
		
		converter.updateMainField("ID_NUMERIC", PropertyDef2.DECIMALS, "-3");
		assertTrue(-3 == holder.get("ID_NUMERIC").getDecimals());
	
		converter.updateMainField("ID_NUMERIC", PropertyDef2.ID, "ID_NEW_NUMERIC");
		assertTrue(-3 == holder.get("ID_NEW_NUMERIC").getDecimals());
		assertEquals(null, holder.get("ID_NUMERIC"));
		
		converter.updateMainField("ID_LIST", PropertyDef2.TYPE, "Text");
		assertEquals("Text", holder.get("ID_LIST").getType().toString());
		
		converter.updateOptionField("ID_LIST", "ID_LIST_A", PropertyDef2.ID, "ID_LIST_ABCD");
		assertEquals("ID_LIST_ABCD", holder.get("ID_LIST").getOption("ID_LIST_ABCD").getId());
		
		converter.updateOptionField("ID_LIST", "ID_LIST_ABCD", PropertyDef2.TITLE, "abcd");
		assertEquals("abcd", holder.get("ID_LIST").getOption("ID_LIST_ABCD").getTitle());
	}
}

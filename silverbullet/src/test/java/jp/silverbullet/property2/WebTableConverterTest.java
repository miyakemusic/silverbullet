package jp.silverbullet.property2;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jp.silverbullet.web.JsonTable;

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
		
		converter.updateMainField("ID_NUMERIC", "Unit", "Hz");
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		converter.updateMainField("ID_NUMERIC", "Presentation", "New Title");
		assertEquals("New Title", holder.get("ID_NUMERIC").getTitle());

		converter.updateMainField("ID_NUMERIC", "Unit", "GHz");
		assertEquals("GHz", holder.get("ID_NUMERIC").getUnit());
		
		converter.updateMainField("ID_NUMERIC", "Min", "-100");
		assertTrue(-100.0 == holder.get("ID_NUMERIC").getMin());
		
		converter.updateMainField("ID_NUMERIC", "Decimals", "-3");
		assertTrue(-3 == holder.get("ID_NUMERIC").getDecimals());
	
		converter.updateMainField("ID_NUMERIC", "ID", "ID_NEW_NUMERIC");
		assertTrue(-3 == holder.get("ID_NEW_NUMERIC").getDecimals());
	}
}

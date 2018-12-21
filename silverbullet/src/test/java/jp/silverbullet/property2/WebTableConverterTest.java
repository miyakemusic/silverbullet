package jp.silverbullet.property2;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jp.silverbullet.web.JsonTable;

class WebTableConverterTest {

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
		assertEquals("Comment", table.header[2]);
		assertEquals("Title", table.header[3]);
	}
}

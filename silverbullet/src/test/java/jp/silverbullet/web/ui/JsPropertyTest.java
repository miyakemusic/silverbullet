package jp.silverbullet.web.ui;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.core.BlobStore;
import jp.silverbullet.core.property2.ChartContent;
import jp.silverbullet.core.property2.PropertyFactory;
import jp.silverbullet.core.property2.PropertyHolder2;
import jp.silverbullet.core.property2.PropertyType2;
import jp.silverbullet.core.property2.RuntimePropertyStore;
import jp.silverbullet.core.property2.ChartContent.ChartType;

public class JsPropertyTest {

	@Test
	public void testJsProperty() throws Exception {
		PropertyFactory factory = new PropertyFactory();
		PropertyHolder2 holder = new PropertyHolder2();
		RuntimePropertyStore store = new RuntimePropertyStore(holder);
		holder.addProperty(factory.create("ID_LIST", PropertyType2.List).title("list").option("ID_LIST_A", "A", "cca").option("ID_LIST_B", "B", "ccb"));
		holder.addProperty(factory.create("ID_NUMERIC", PropertyType2.Numeric).defaultValue(123).decimals(2).title("number").unit("Hz"));
		holder.addProperty(factory.create("ID_BOOLEAN", PropertyType2.Boolean));
		holder.addProperty(factory.create("ID_TEXT", PropertyType2.Text));
		holder.addProperty(factory.create("ID_CHART", PropertyType2.Chart));
		BlobStore blobStore = new BlobStore();
		
		{
			JsProperty prop = JsPropertyConverter.convert(store.get("ID_LIST"), null, blobStore);
			assertEquals("ID_LIST", prop.getId());
			assertEquals("ID_LIST_A", prop.getCurrentSelectionId());
			assertEquals("list", prop.getTitle());
			assertEquals(2, prop.getElements().size());
			assertEquals("ID_LIST_A", prop.getElements().get(0).getId());
			assertEquals("A", prop.getElements().get(0).getTitle());
			assertEquals("cca", prop.getElements().get(0).getComment());
			assertEquals("ID_LIST_B", prop.getElements().get(1).getId());
			assertEquals("B", prop.getElements().get(1).getTitle());
			assertEquals("ccb", prop.getElements().get(1).getComment());
		}
		
		{
			JsProperty prop = JsPropertyConverter.convert(store.get("ID_NUMERIC"), null, blobStore);
			assertEquals("ID_NUMERIC", prop.getId());
			assertEquals("123.00", prop.getCurrentValue());
			assertEquals("number", prop.getTitle());
			assertEquals("Hz", prop.getUnit());
		}
		
		{
			ChartContent content = new ChartContent();
			content.setChartType(ChartType.XY);
			content.setYmax("100");
			content.setYmin("0");
			content.setY(Arrays.asList("1", "2", "3", "4", "5").toArray(new String[0]));
			store.get("ID_CHART").setCurrentValue(new ObjectMapper().writeValueAsString(content));
			JsProperty prop = JsPropertyConverter.convert(store.get("ID_CHART"), null, blobStore);
			assertEquals("ID_CHART", prop.getId());
			
			blobStore.put("ID_CHART", content);
			prop = JsPropertyConverter.convert(store.get("ID_CHART"), "2", blobStore);
			
			assertEquals("ID_CHART", prop.getId());
		}
	}

}

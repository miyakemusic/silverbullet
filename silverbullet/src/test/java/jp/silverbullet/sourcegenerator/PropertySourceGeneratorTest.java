package jp.silverbullet.sourcegenerator;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import jp.silverbullet.property2.PropertyDef2;
import jp.silverbullet.property2.PropertyFactory;
import jp.silverbullet.property2.PropertyHolder2;
import jp.silverbullet.property2.RuntimePropertyStore;

public class PropertySourceGeneratorTest {

	@Test
	public void test() throws IOException {
		PropertyHolder2 holder = new PropertyHolder2();
		PropertyFactory factory = new PropertyFactory();
		RuntimePropertyStore store = new RuntimePropertyStore(holder);
		
		try {
			holder.addProperty(factory.createList("ID_LIST").option("ID_LIST_A", "A", "").option("ID_LIST_D", "D", "").defaultId("ID_LIST_A"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		holder.addProperty(factory.createNumeric("ID_NUMERIC").min(-10).max(10).unit("Hz").decimals(2).defaultValue(10.0));
		holder.addProperty(factory.createBoolean("ID_BOOL").defaultValue(PropertyDef2.True));
		holder.addProperty(factory.createText("ID_TEXT").defaultValue("Default Text").maxLength(100));
		holder.addProperty(factory.createChart("ID_CHART"));
		holder.addProperty(factory.createTable("ID_TABLE"));
		
		PropertySourceGenerator generator = new PropertySourceGenerator(store.getAllProperties());
		generator.generate(".", "mypackage");
		
		List<String> ids = Files.readAllLines(Paths.get("./mypackage/ID.java"));
		assertEquals("	public static final String ID_LIST=\"ID_LIST\";", ids.get(2));
		
		List<String> usr = Files.readAllLines(Paths.get("./mypackage/UserEasyAccess.java"));
		assertEquals("    public enum EnumList{", usr.get(8));
	}

}

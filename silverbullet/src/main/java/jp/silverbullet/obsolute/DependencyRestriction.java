package jp.silverbullet.obsolute;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class DependencyRestriction {
	private List<RestrictionItem> items = new ArrayList<>();
	
	public void addColumn(String id, String item) {
		this.items.add(new RestrictionItem(id, item));
	}

	public void build() {
		List<String> rows = new ArrayList<String>();
		String title = ",";
		for (int col = 0; col < items.size(); col++) {
			RestrictionItem item = items.get(col);
			title += item.toString() + ",";
		}
		rows.add(title);
	
		for (int row = 0; row < items.size(); row++) {
			String r = "," + items.get(row);
			for (int col = 0; col < items.size(); col++) {
				r += ",";
			}
			rows.add(r);
		}
	
		try {
			Files.write(Paths.get("C:\\Projects\\restriction.csv"), rows, StandardOpenOption.CREATE_NEW);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

class RestrictionItem {
	public RestrictionItem(String id, String item) {
		this.id = id;
		this.item = item;
	}
	public String id;
	public String item;
	@Override
	public String toString() {
		return this.id + "." + this.item;
	}
	
	
}
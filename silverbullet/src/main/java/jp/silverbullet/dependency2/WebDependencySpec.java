package jp.silverbullet.dependency2;

import java.util.ArrayList;
import java.util.List;

public class WebDependencySpec {
	public List<WebDependencyRow> list = new ArrayList<>();

	public void add(String name, WebDependencyElement element) {
		get(name).rows.add(element);
	}
	
	private WebDependencyRow get(String name) {
		for (WebDependencyRow row : this.list) {
			if (row.element.equals(name)) {
				return row;
			}
		}
		WebDependencyRow ret = new WebDependencyRow();
		ret.element = name;
		this.list.add(ret);
		return ret;
	}
}

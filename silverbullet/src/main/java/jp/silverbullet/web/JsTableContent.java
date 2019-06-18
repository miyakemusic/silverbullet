package jp.silverbullet.web;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JsTableContent {

	public List<String> headers;
	public List<List<String>> data = new ArrayList<>();
	public boolean newFlag;
	public int selectedRow;
	
	public void addRow(List<String> line) {
		this.data.add(line);
	}

}

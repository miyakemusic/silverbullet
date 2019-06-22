package jp.silverbullet.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@XmlRootElement
public class JsTableContent {

	public List<String> headers;
	public List<List<String>> data = new ArrayList<>();
	public boolean newFlag;
	public boolean dataChanged = false;
	public int selectedRow;
	
	public void addRow(List<String> line) {
		this.dataChanged = true;
		this.data.add(line);
	}

	public static JsTableContent read(String str) {
		try {
			return new ObjectMapper().readValue(str, JsTableContent.class);
		} catch (Exception e) {
			return new JsTableContent();
		} 
	}

}

package jp.silverbullet.web;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JsTableContent {

	private List<String> headers;
	private List<List<String>> data = new ArrayList<>();
	private boolean newFlag;
	
	public void setHeaders(List<String> asList) {
		this.headers= asList;
	}

	public void addRow(List<String> line) {
		this.data.add(line);
	}

	public List<String> getHeaders() {
		return headers;
	}

	public List<List<String>> getData() {
		return data;
	}

	public boolean isNewFlag() {
		return newFlag;
	}

	public void setNewFlag(boolean newFlag) {
		this.newFlag = newFlag;
	}

}

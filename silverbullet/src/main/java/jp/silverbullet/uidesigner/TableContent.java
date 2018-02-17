package jp.silverbullet.uidesigner;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class TableContent {
	private String title = "";
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	private List<String> headers = new ArrayList<String>();
	private List<RowData> data = new ArrayList<>();
	private int xAxisColumn;
	public int getxAxisColumn() {
		return xAxisColumn;
	}
	public void setxAxisColumn(int xAxisColumn) {
		this.xAxisColumn = xAxisColumn;
	}
	public List<String> getHeaders() {
		return headers;
	}
	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}
	
//	@XmlAttribute(name="d")
	public List<RowData> getData() {
		return data;
	}
	public void setData(List<RowData> data) {
		this.data = data;
	}



}

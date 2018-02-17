package jp.silverbullet.test;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TableDataList {
	private ArrayList<TableData> data = new ArrayList<TableData>();

	public ArrayList<TableData> getData() {
		return data;
	}

	public void setData(ArrayList<TableData> data) {
		this.data = data;
	}
	
}

package jp.silverbullet.core.property2;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.ObjectMapper;

@XmlRootElement
public class JsTableContent {

	public List<String> headers = new ArrayList<>();
	private List<List<String>> data = new ArrayList<>();
	public boolean structureChanged = true;
	public boolean dataChanged = true;
	private int selectedRow;
	public boolean selectedRowChanged = true;
	
	public void addRow(List<String> line) {
		this.dataChanged = true;
		this.data.add(line);
		if (this.selectedRow == -1) {
			this.setSelectedRow( 0);
		}
	}

	
	public int getSelectedRow() {
		return selectedRow;
	}


	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
		this.selectedRowChanged = true;
	}

	public List<String> getSelectedData() {
		return this.getDataAt(this.selectedRow);
	}
	
	public List<String> getDataAt(int index) {
		return this.data.get(index);
	}
	
	public List<List<String>> getData() {
		return data;
	}


	public static JsTableContent read(String str) {
		try {
			return new ObjectMapper().readValue(str, JsTableContent.class);
		} catch (Exception e) {
			return new JsTableContent();
		} 
	}


	public List<String> getHeaders() {
		return headers;
	}


	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}


	public boolean isStructureChanged() {
		return structureChanged;
	}


	public void setStructureChanged(boolean structureChanged) {
		this.structureChanged = structureChanged;
	}


	public boolean isDataChanged() {
		return dataChanged;
	}


	public void setDataChanged(boolean dataChanged) {
		this.dataChanged = dataChanged;
	}


	public boolean isSelectedRowChanged() {
		return selectedRowChanged;
	}


	public void setSelectedRowChanged(boolean selectedRowChanged) {
		this.selectedRowChanged = selectedRowChanged;
	}


	public void setData(List<List<String>> data) {
		this.data = data;
	}


	public void clear() {
		this.data.clear();
		this.selectedRow = -1;
		this.dataChanged = true;
		this.selectedRowChanged = true;
	}

}

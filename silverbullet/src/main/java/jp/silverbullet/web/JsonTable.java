package jp.silverbullet.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JsonTable {
	public String[] header = new String[0];
	public List<String[]> table = new ArrayList<>();
	
	public void addRow(String header, String[] row) {
		List<String> t = new ArrayList<>();
		t.add(header);
		t.addAll(Arrays.asList(row));
		this.table.add(t.toArray(new String[0]));
	}

	public void setHeader(String header, String[] headers) {
		List<String> t = new ArrayList<>();
		t.add(header);
		t.addAll(Arrays.asList(headers));
		this.header = t.toArray(new String[0]);
	}

	public void setHeader(List<String> keys) {
		this.header = keys.toArray(new String[0]);
	}

	public void addRow(List<String> values) {
		this.table.add(values.toArray(new String[0]));
	}

	public void setHeader(String[] header2) {
		this.header = header2;
	}

	public void addRow(String[] val) {
		this.table.add(val);
	}

	public String[] getHeader() {
		return header;
	}

	public List<String[]> getTable() {
		return table;
	}

	public void setTable(List<String[]> table) {
		this.table = table;
	}
	
	
}

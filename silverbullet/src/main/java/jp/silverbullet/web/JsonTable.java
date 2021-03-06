package jp.silverbullet.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlRootElement
public class JsonTable {
	public String[] header = new String[0];
	public Integer[] widths = new Integer[0];
	public List<List<String>> table = new ArrayList<>();
	private Map<String, List<String>> options = new HashMap<>();
	
	public void addRow(String header, String[] row) {
		List<String> t = new ArrayList<>();
		t.add(header);
		t.addAll(Arrays.asList(row));
		this.table.add(t);
	}

	public void addHeader(String header, String[] headers) {
		List<String> t = new ArrayList<>();
		t.add(header);
		t.addAll(Arrays.asList(headers));
		this.header = t.toArray(new String[0]);
	}
	
	public void setHeaderList(List<String> keys) {
		this.header = keys.toArray(new String[0]);
	}

	public void addRow(List<String> values) {
		this.table.add(values);
	}

	public void setHeader(String[] header2) {
		this.header = header2;
	}

	public void addRow(String[] val) {
		this.table.add(new ArrayList<String>(Arrays.asList(val)));
	}

	public String[] getHeader() {
		return header;
	}

	public List<List<String>> getTable() {
		return table;
	}

	public void setTable(List<List<String>> table) {
		this.table = table;
	}

	public void setWidths(Integer[] widths) {
		this.widths = widths;
	}

	public Integer[] getWidths() {
		return this.widths;
	}

	public void addOption(String id, List<String> list) {
		this.options.put(id, list);
	}

	public Map<String, List<String>> getOptions() {
		return options;
	}

	public void setOptions(Map<String, List<String>> options) {
		this.options = options;
	}
	
}
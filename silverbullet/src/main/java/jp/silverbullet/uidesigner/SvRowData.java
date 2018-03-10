package jp.silverbullet.uidesigner;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import jp.silverbullet.uidesigner.SvTableElement.Status;

@XmlRootElement
public class SvRowData {	
	private List<SvTableElement> elements = new ArrayList<>();

	public SvRowData() {
		
	}
	
	public void add(String value, Status status) {
		this.elements.add(new SvTableElement(value, status));
	}

	public void add(String value) {
		this.elements.add(new SvTableElement(value));
	}
	
	public List<SvTableElement> getElements() {
		return elements;
	}

	public void setElements(List<SvTableElement> elements) {
		this.elements = elements;
	}
	
	
}

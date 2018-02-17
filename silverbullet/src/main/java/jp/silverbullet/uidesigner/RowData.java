package jp.silverbullet.uidesigner;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RowData {
	private List<String> element = new ArrayList<String>();

//	@XmlAttribute(name="e")
	public List<String> getElement() {
		return element;
	}

	public void setElement(List<String> element) {
		this.element = element;
	}
}

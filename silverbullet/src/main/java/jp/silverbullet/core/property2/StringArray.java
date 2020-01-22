package jp.silverbullet.core.property2;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class StringArray {

	public StringArray() {
		
	}
	
	public StringArray(List<String> arr) {
		this.list = arr;
	}

	public List<String> list = new ArrayList<String>();
}
